package com.example.salman3tie.pertemuan12

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.salman3tie.data.model.ProductModel
import com.example.salman3tie.databinding.ItemProductCrudBinding

class ProductCrudAdapter(
    private val items: MutableList<ProductModel>,
    private val onEditClick: (ProductModel) -> Unit,
    private val onDeleteClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductCrudAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductCrudBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCrudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvProductName.text = item.name
        holder.binding.tvProductPrice.text = item.price
        holder.binding.tvProductDesc.text = item.description

        holder.binding.btnEdit.setOnClickListener {
            onEditClick(item)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ProductModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
