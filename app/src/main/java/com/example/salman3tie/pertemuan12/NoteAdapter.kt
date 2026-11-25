package com.example.salman3tie.pertemuan12

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.salman3tie.data.model.NoteModel
import com.example.salman3tie.databinding.ItemNoteBinding

class NoteAdapter(
    private val items: MutableList<NoteModel>,
    private val onEditClick: (NoteModel) -> Unit,
    private val onDeleteClick: (NoteModel) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = items[position]
        holder.binding.tvTitle.text = note.title
        holder.binding.tvContent.text = note.content
        holder.binding.tvStatus.text = note.status

        holder.binding.btnEdit.setOnClickListener {
            onEditClick(note)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(note)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<NoteModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
