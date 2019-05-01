package us.ait.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.new_tobuy_dialog.view.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import us.ait.shoppinglist.adapter.ToBuyAdapter
import us.ait.shoppinglist.data.AppDatabase
import us.ait.shoppinglist.data.Tobuy
import us.ait.shoppinglist.touch.ToBuyReyclerTouchCallback
import com.nightonke.boommenu.BoomButtons.OnBMClickListener
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton



class ScrollingActivity : AppCompatActivity(), TobuyDialog.TobuyHandler {

    lateinit var tobuyAdapter : ToBuyAdapter

    companion object {
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        //toolbar.title = ""
        setSupportActionBar(toolbar)

        makeBoomMenu()
        checkOpenEarlier()
        saveFirstOpenInfo()
        initRecyclerViewfromDB()
    }

    private fun makeBoomMenu() {
        val builder1 = TextInsideCircleButton.Builder()
            .normalImageRes(R.drawable.shoppingadd)
            .normalColorRes(R.color.lime)
            .listener { index ->
                showAddToBuyDialog()
            }
        bmb.addBuilder(builder1)
        val builder2 = TextInsideCircleButton.Builder()
            .normalImageRes(R.drawable.shoppingdelete)
            .normalColorRes(R.color.lightLime)
            .listener { index ->
                deleteAllItems()
            }
        bmb.addBuilder(builder2)
        val builder3 = TextInsideCircleButton.Builder()
            .normalImageRes(R.drawable.coin)
            .normalColorRes(R.color.orange)
            .listener { index ->
                startSummary()
            }
        bmb.addBuilder(builder3)
    }

    private fun deleteAllItems() {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).tobuyDao().deleteAll()
            runOnUiThread {
                tobuyAdapter.removeAll()
            }
        }.start()
    }

    fun checkOpenEarlier() {
        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.bmb)
                .setPrimaryText(getString(R.string.preference_title))
                .setSecondaryText(getString(R.string.preference_subtitle))
                .show()
        }
    }

    fun startSummary() {
        val mainIntent = Intent(this@ScrollingActivity, SummaryActivity::class.java)
        this@ScrollingActivity.startActivity(mainIntent)
    }

    fun saveFirstOpenInfo() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.preference_KEY_OPEN), true)
        editor.apply()
    }

    fun wasOpenedEarlier() : Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(getString(R.string.preference_KEY_OPEN), false)
    }

    private fun initRecyclerViewfromDB() {
        Thread {
            var tobuyList = AppDatabase.getInstance(this@ScrollingActivity).tobuyDao().getAllTobuy()
            runOnUiThread {
                tobuyAdapter = ToBuyAdapter(this, tobuyList)
                recyclerTobuy.layoutManager = LinearLayoutManager(this)
                recyclerTobuy.adapter = tobuyAdapter

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerTobuy.addItemDecoration(itemDecoration)

                val callback = ToBuyReyclerTouchCallback(tobuyAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerTobuy)
            }

        }.start()
    }

    private fun showAddToBuyDialog() {
        TobuyDialog().show(supportFragmentManager, getString(R.string.add_item_dialog_tag))
    }

    var editIndex: Int = -1

    public fun showEditToBuyDialog(tobuyToEdit: Tobuy, idx: Int) {
        editIndex = idx
        val editItemDialog = TobuyDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, tobuyToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager,
            getString(R.string.edit_item_dialog_tag))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun tobuyCreated(item: Tobuy) {
        Thread {
            val tobuyId = AppDatabase.getInstance(
                this@ScrollingActivity).tobuyDao().insertTobuy(item)
            item.tobuyId = tobuyId
            runOnUiThread {
                tobuyAdapter.addTobuy(item)
            }
        }.start()
    }

    override fun tobuyUpdated(item: Tobuy) {
        Thread {
            AppDatabase.getInstance(
                this@ScrollingActivity).tobuyDao().updateTobuy(item)
            runOnUiThread{
                tobuyAdapter.updateTobuy(item, editIndex)
            }
        }.start()
    }
}