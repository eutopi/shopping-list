package us.ait.shoppinglist

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_summary.*
import us.ait.shoppinglist.data.AppDatabase
import android.graphics.Color.parseColor
import org.eazegraph.lib.models.PieModel
import android.graphics.Color.parseColor





class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        showAmountStats()
        showCategoryStats()
    }

    private fun showAmountStats() {
        var toSpendAmount = 0
        var spentAmount = 0
        Thread {
            var tobuyList = AppDatabase.getInstance(this@SummaryActivity).tobuyDao().getAllTobuy()
            runOnUiThread {
                for (item in tobuyList) {
                    if (item.status) spentAmount += item.price.toInt()
                    else toSpendAmount += item.price.toInt()
                }
                txtToSpend.text = getString(R.string.summary_to_spend_label) + toSpendAmount
                txtSpent.text = getString(R.string.summary_spent_label) + spentAmount.toString()
                totalAmount.text = getString(R.string.summary_total_label) + (toSpendAmount + spentAmount).toString()
            }
        }.start()
    }

    private fun showCategoryStats() {
        var categoryAmounts = mutableListOf<Int>(0, 0, 0, 0)
        Thread {
            var tobuyList = AppDatabase.getInstance(this@SummaryActivity).tobuyDao().getAllTobuy()
            runOnUiThread {
                for (item in tobuyList) {
                    categoryAmounts[item.category] += categoryAmounts[item.category] + item.price.toInt()
                }
                if (categoryAmounts[0] != 0 || categoryAmounts[1] != 0 ||
                        categoryAmounts[2] != 0 || categoryAmounts[3] != 0) {
                    piechart.addPieSlice(PieModel(getString(R.string.category_food),
                        categoryAmounts[0].toFloat(), parseColor(getString(R.string.color_light_magenta))))
                    piechart.addPieSlice(PieModel(getString(R.string.category_drink),
                        categoryAmounts[1].toFloat(), parseColor(getString(R.string.color_orange))))
                    piechart.addPieSlice(PieModel(getString(R.string.category_book),
                        categoryAmounts[2].toFloat(), parseColor(getString(R.string.color_lime))))
                    piechart.addPieSlice(PieModel(getString(R.string.category_electronic),
                        categoryAmounts[3].toFloat(), parseColor(getString(R.string.color_yellow))))

                    piechart.startAnimation()
                }
            }
        }.start()
    }

}
