package com.example.salman3tie.pertemuan12

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salman3tie.data.api.SupabaseApiClient
import com.example.salman3tie.data.model.NoteModel
import com.example.salman3tie.databinding.ActivityNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var adapter: NoteAdapter
    private val noteList = mutableListOf<NoteModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()

        binding.fabAdd.setOnClickListener {
            showAddNoteDialog()
        }

        loadNotes()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(
            noteList,
            onEditClick = { note -> showEditNoteDialog(note) },
            onDeleteClick = { note -> showDeleteConfirmDialog(note) }
        )
        binding.rvNote.layoutManager = LinearLayoutManager(this)
        binding.rvNote.adapter = adapter
    }

    private fun loadNotes() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val notes = SupabaseApiClient.noteApiService.getNotes()
                binding.progressBar.visibility = View.GONE

                if (notes.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                }

                adapter.updateData(notes)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("NOTE_LOAD_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@NoteActivity, "Gagal memuat catatan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddNoteDialog() {
        val inputTitle = EditText(this).apply { hint = "Judul Catatan" }
        val inputContent = EditText(this).apply { hint = "Isi Catatan" }
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
            addView(inputTitle)
            addView(inputContent)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Tambah Catatan Baru")
            .setView(layout)
            .setPositiveButton("Simpan") { dialog, _ ->
                val title = inputTitle.text.toString().trim()
                val content = inputContent.text.toString().trim()

                if (title.isNotEmpty() && content.isNotEmpty()) {
                    createNote(title, content)
                } else {
                    Toast.makeText(this, "Judul dan Isi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun createNote(title: String, content: String) {
        lifecycleScope.launch {
            try {
                val newNote = NoteModel(title = title, content = content, status = "To Do")
                val response = SupabaseApiClient.noteApiService.createNote(newNote)
                if (response.isSuccessful) {
                    Toast.makeText(this@NoteActivity, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    loadNotes()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("NOTE_CREATE_FAIL", "Code ${response.code()}: $errorBody")
                    Toast.makeText(this@NoteActivity, "Gagal (${response.code()}): $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("NOTE_CREATE_ERROR", e.message ?: "Unknown error", e)
                Toast.makeText(this@NoteActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEditNoteDialog(note: NoteModel) {
        val inputTitle = EditText(this).apply {
            hint = "Judul Catatan"
            setText(note.title)
        }
        val inputContent = EditText(this).apply {
            hint = "Isi Catatan"
            setText(note.content)
        }
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
            addView(inputTitle)
            addView(inputContent)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Catatan")
            .setView(layout)
            .setPositiveButton("Update") { dialog, _ ->
                val title = inputTitle.text.toString().trim()
                val content = inputContent.text.toString().trim()

                if (title.isNotEmpty() && content.isNotEmpty() && note.id != null) {
                    updateNote(note.id, title, content, note.status)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateNote(id: Long, title: String, content: String, status: String) {
        lifecycleScope.launch {
            try {
                val updatedNote = NoteModel(title = title, content = content, status = status)
                val response = SupabaseApiClient.noteApiService.updateNote("eq.$id", updatedNote)
                if (response.isSuccessful) {
                    Toast.makeText(this@NoteActivity, "Catatan diperbarui", Toast.LENGTH_SHORT).show()
                    loadNotes()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@NoteActivity, "Gagal update: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("NOTE_UPDATE_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@NoteActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeleteConfirmDialog(note: NoteModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Catatan")
            .setMessage("Apakah Anda yakin ingin menghapus catatan \"${note.title}\"?")
            .setPositiveButton("Ya, Hapus") { dialog, _ ->
                note.id?.let { deleteNote(it) }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteNote(id: Long) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.noteApiService.deleteNote("eq.$id")
                if (response.isSuccessful) {
                    Toast.makeText(this@NoteActivity, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                    loadNotes()
                } else {
                    Toast.makeText(this@NoteActivity, "Gagal hapus: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("NOTE_DELETE_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@NoteActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
