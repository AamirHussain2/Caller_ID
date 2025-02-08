package com.example.diallerapp.adapter.uicreatecontact

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diallerapp.R
import com.example.diallerapp.databinding.CustomAddToLabelDialogItemBinding
import com.example.diallerapp.databinding.CustomDialogEdittextBinding
import com.example.diallerapp.databinding.ItemFooterButtonBinding
import com.example.diallerapp.model.uicreatecontact.AddToLabelModel

class AddToLabelAdapter(val context : Context) :
    ListAdapter<AddToLabelModel, RecyclerView.ViewHolder>(AddToLabelDiffUtil()) {

    private val ITEM_TYPE = 0
    private val FOOTER_TYPE = 1

    private var onItemCheckedChangeListener: (() -> Unit)? = null

    fun setOnItemCheckedChangeListener(listener: () -> Unit) {
        onItemCheckedChangeListener = listener
    }

    inner class AddToLabelViewHolder(private val binding: CustomAddToLabelDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AddToLabelModel) {
            binding.tvAddToLabelItem.text = item.textViewLabel
            binding.checkboxItem.isChecked = item.isChecked


            binding.checkboxItem.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                onItemCheckedChangeListener?.invoke()
                Log.d("AddToLabel", "bind: $isChecked")
            }
        }
    }

    inner class FooterViewHolder(private val binding: ItemFooterButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.buttonFooter.setOnClickListener {

                val dialog = Dialog(context)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                val dialogBinding = CustomDialogEdittextBinding.inflate(LayoutInflater.from(context))
                dialog.setContentView(dialogBinding.root)

                dialogBinding.btnOk.setOnClickListener {

                    if (dialogBinding.edLabelName.editText?.text.isNullOrEmpty()){
                        dialogBinding.edLabelName.error = context.getString(R.string.please_enter_label_name)
                        return@setOnClickListener
                    }
                    val text = dialogBinding.edLabelName.editText?.text.toString()
                    val newList = currentList.toMutableList()
                    newList.add(AddToLabelModel(text))
                    submitList(newList)
                    dialog.dismiss()
                }
                dialogBinding.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

                Toast.makeText(context, "New label clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        val getItemViewType = if (position == currentList.size){
            FOOTER_TYPE
        } else{

            ITEM_TYPE
        }

        return getItemViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == FOOTER_TYPE) {
            val binding = ItemFooterButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterViewHolder(binding)
        } else {
            val binding = CustomAddToLabelDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AddToLabelViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is AddToLabelViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1  // +1 for footer
    }

    class AddToLabelDiffUtil : DiffUtil.ItemCallback<AddToLabelModel>() {
        override fun areItemsTheSame(oldItem: AddToLabelModel, newItem: AddToLabelModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AddToLabelModel, newItem: AddToLabelModel): Boolean {
            return oldItem.textViewLabel == newItem.textViewLabel
        }
    }
}
