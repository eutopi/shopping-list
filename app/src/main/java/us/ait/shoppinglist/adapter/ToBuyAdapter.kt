package us.ait.shoppinglist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tobuy_row.view.*
import us.ait.shoppinglist.R
import us.ait.shoppinglist.ScrollingActivity
import us.ait.shoppinglist.data.AppDatabase
import us.ait.shoppinglist.data.Tobuy
import us.ait.shoppinglist.touch.TobuyTouchHelperCallback
import java.util.*

class ToBuyAdapter : RecyclerView.Adapter<ToBuyAdapter.ViewHolder>, TobuyTouchHelperCallback {

    var toBuyItems = mutableListOf<Tobuy>()

    private val context: Context

    constructor(context: Context, listTobuys: List<Tobuy>) : super() {
        this.context = context
        toBuyItems.addAll(listTobuys)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val tobuyRowView = LayoutInflater.from(context).inflate(
            R.layout.tobuy_row, viewGroup, false
        )
        return ViewHolder(tobuyRowView)
    }

    override fun getItemCount(): Int {
        return toBuyItems.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val tobuy = toBuyItems.get(viewHolder.adapterPosition)
        viewHolder.tvName.text = tobuy.name
        viewHolder.tvPrice.text = context.getString(R.string.dollar_sign) + tobuy.price
        viewHolder.cbStatus.isChecked = tobuy.status
        var src = R.drawable.ic_add
        when (tobuy.category) {
            0 -> src = R.drawable.food
            1 -> src = R.drawable.drink
            2 -> src = R.drawable.book
            3 -> src = R.drawable.electronic
        }
        viewHolder.ivIcon.setImageResource(src)
        viewHolder.btnDelete.setOnClickListener {
            deleteTobuy(position)
        }
        viewHolder.cbStatus.setOnClickListener {
            tobuy.status = viewHolder.cbStatus.isChecked
            updateToBuy(tobuy)
        }
        viewHolder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditToBuyDialog(
                tobuy, viewHolder.adapterPosition
            )
        }
    }

    fun updateToBuy(tobuy: Tobuy) {
        Thread {
            AppDatabase.getInstance(context).tobuyDao().updateTobuy(tobuy)
        }.start()
    }

    fun updateTobuy(item: Tobuy, editIndex: Int) {
        toBuyItems[editIndex] = item
        notifyItemChanged(editIndex)
    }

    fun addTobuy(tobuy: Tobuy) {
        toBuyItems.add(0, tobuy)
        //notifyDataSetChanged() //redraws everything (don't have to rn since its just adding)
        notifyItemInserted(0)
    }

    fun deleteTobuy(deletePosition: Int) {
        Thread {
            AppDatabase.getInstance(context).tobuyDao().deleteTobuy(
                toBuyItems.get(deletePosition)
            )

            (context as ScrollingActivity).runOnUiThread {
                toBuyItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    fun removeAll() {
        toBuyItems.clear()
        notifyDataSetChanged()
    }

    override fun onDismissed(position: Int) {
        deleteTobuy(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(toBuyItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.tvName
        var ivIcon = itemView.ivIcon
        var tvPrice = itemView.tvPrice
        var cbStatus = itemView.cbStatus
        var btnDelete = itemView.btnDelete
        var btnEdit = itemView.btnEdit
    }
}