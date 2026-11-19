package com.example.salman3tie.home.photo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.salman3tie.databinding.ItemPhotoBinding
import com.example.salman3tie.home.data.model.PhotoModel

class PhotoAdapter(
    private val items: List<PhotoModel>
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(
        val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {

        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) {

        val item = items[position]

        // Menampilkan data ke Logcat
        Log.d("RESPONSE", item.toString())

        // Menampilkan nama author
        holder.binding.tvAuthor.text = item.author

        // Menampilkan gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(item.download_url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_delete)
            .into(holder.binding.imgPhoto)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
