package us.ait.shoppinglist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import kotlinx.android.synthetic.main.new_tobuy_dialog.view.*
import us.ait.shoppinglist.data.Tobuy
import java.lang.RuntimeException
import java.util.*

class TobuyDialog : DialogFragment() {

    // for interacting with the activity
    interface TobuyHandler {
        fun tobuyCreated(item: Tobuy)
        fun tobuyUpdated(item: Tobuy)
    }

    private lateinit var tobuyHandler: TobuyHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is TobuyHandler) {
            tobuyHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the TobuyHandlerInterface")
        }
    }

    private lateinit var etTobuyName: EditText
    private lateinit var etTobuyDescription: EditText
    private lateinit var etTobuyPrice: EditText
    private lateinit var etTobuyCategory: Spinner
    private lateinit var etTobuyStatus: CheckBox

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New item")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_tobuy_dialog, null
        )
        etTobuyName = rootView.nameFill
        etTobuyDescription = rootView.descriptionFill
        etTobuyPrice = rootView.priceFill
        etTobuyCategory = rootView.categorySelect
        etTobuyStatus = rootView.statusSelect
        builder.setView(rootView)

        val arguments = this.arguments

        // if i am in edit mode
        if (arguments != null && arguments.containsKey(
                ScrollingActivity.KEY_ITEM_TO_EDIT)) {
            val tobuyItem = arguments.getSerializable(
                ScrollingActivity.KEY_ITEM_TO_EDIT
            ) as Tobuy
            etTobuyName.setText(tobuyItem.name)
            etTobuyDescription.setText(tobuyItem.description)
            etTobuyPrice.setText(tobuyItem.price)
            etTobuyCategory.setSelection(tobuyItem.category)
            etTobuyStatus.isChecked = tobuyItem.status

            builder.setTitle("Edit item")
        }

        builder.setPositiveButton("OK") {
                dialog, witch -> // empty
        }

        return builder.create()
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etTobuyName.text.isNotEmpty() && etTobuyPrice.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                    handleTobuyEdit()
                } else {
                    handleTobuyCreate()
                }

                dialog.dismiss()
            } else {
                if (etTobuyName.text.isEmpty()) etTobuyName.error = "Name cannot be empty"
                if (etTobuyPrice.text.isEmpty()) etTobuyPrice.error = "Price cannot be empty"
            }
        }
    }

    private fun handleTobuyCreate() {
        tobuyHandler.tobuyCreated(
            Tobuy(
                null,
                etTobuyName.text.toString(),
                etTobuyDescription.text.toString(),
                etTobuyPrice.text.toString(),
                etTobuyCategory.selectedItemPosition,
                etTobuyStatus.isChecked
            )
        )
    }

    private fun handleTobuyEdit() {
        val tobuyToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_TO_EDIT
        ) as Tobuy
        tobuyToEdit.name = etTobuyName.text.toString()
        tobuyToEdit.description = etTobuyDescription.text.toString()
        tobuyToEdit.price = etTobuyPrice.text.toString()
        tobuyToEdit.category = etTobuyCategory.selectedItemPosition
        tobuyToEdit.status = etTobuyStatus.isChecked

        tobuyHandler.tobuyUpdated(tobuyToEdit)
    }

}