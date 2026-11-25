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
import com.example.salman3tie.data.model.ProductModel
import com.example.salman3tie.databinding.ActivityProductCrudBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProductCrudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductCrudBinding
    private lateinit var adapter: ProductCrudAdapter
    private val productList = mutableListOf<ProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()

        binding.fabAdd.setOnClickListener {
            showAddProductDialog()
        }

        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductCrudAdapter(
            productList,
            onEditClick = { product -> showEditProductDialog(product) },
            onDeleteClick = { product -> showDeleteConfirmDialog(product) }
        )
        binding.rvProduct.layoutManager = LinearLayoutManager(this)
        binding.rvProduct.adapter = adapter
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val products = SupabaseApiClient.productApiService.getProducts()
                binding.progressBar.visibility = View.GONE

                if (products.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                }

                adapter.updateData(products)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("PRODUCT_LOAD_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@ProductCrudActivity, "Gagal memuat produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddProductDialog() {
        val inputName = EditText(this).apply { hint = "Nama Produk" }
        val inputPrice = EditText(this).apply { hint = "Harga Produk (misal: Rp 150.000)" }
        val inputDesc = EditText(this).apply { hint = "Deskripsi Produk" }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
            addView(inputName)
            addView(inputPrice)
            addView(inputDesc)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Tambah Produk Baru")
            .setView(layout)
            .setPositiveButton("Simpan") { dialog, _ ->
                val name = inputName.text.toString().trim()
                val price = inputPrice.text.toString().trim()
                val desc = inputDesc.text.toString().trim()

                if (name.isNotEmpty() && price.isNotEmpty()) {
                    createProduct(name, price, desc)
                } else {
                    Toast.makeText(this, "Nama dan Harga tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun createProduct(name: String, price: String, desc: String) {
        lifecycleScope.launch {
            try {
                val newProduct = ProductModel(name = name, price = price, description = desc)
                val response = SupabaseApiClient.productApiService.createProduct(newProduct)
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductCrudActivity, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    loadProducts()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@ProductCrudActivity, "Gagal (${response.code()}): $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("PRODUCT_CREATE_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@ProductCrudActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEditProductDialog(product: ProductModel) {
        val inputName = EditText(this).apply {
            hint = "Nama Produk"
            setText(product.name)
        }
        val inputPrice = EditText(this).apply {
            hint = "Harga Produk"
            setText(product.price)
        }
        val inputDesc = EditText(this).apply {
            hint = "Deskripsi Produk"
            setText(product.description)
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 30, 50, 10)
            addView(inputName)
            addView(inputPrice)
            addView(inputDesc)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Produk")
            .setView(layout)
            .setPositiveButton("Update") { dialog, _ ->
                val name = inputName.text.toString().trim()
                val price = inputPrice.text.toString().trim()
                val desc = inputDesc.text.toString().trim()

                if (name.isNotEmpty() && price.isNotEmpty() && product.id != null) {
                    updateProduct(product.id, name, price, desc)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateProduct(id: Long, name: String, price: String, desc: String) {
        lifecycleScope.launch {
            try {
                val updatedProduct = ProductModel(name = name, price = price, description = desc)
                val response = SupabaseApiClient.productApiService.updateProduct("eq.$id", updatedProduct)
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductCrudActivity, "Produk diperbarui", Toast.LENGTH_SHORT).show()
                    loadProducts()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@ProductCrudActivity, "Gagal update: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("PRODUCT_UPDATE_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@ProductCrudActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeleteConfirmDialog(product: ProductModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Produk")
            .setMessage("Apakah Anda yakin ingin menghapus produk \"${product.name}\"?")
            .setPositiveButton("Ya, Hapus") { dialog, _ ->
                product.id?.let { deleteProduct(it) }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteProduct(id: Long) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.productApiService.deleteProduct("eq.$id")
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductCrudActivity, "Produk dihapus", Toast.LENGTH_SHORT).show()
                    loadProducts()
                } else {
                    Toast.makeText(this@ProductCrudActivity, "Gagal hapus: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PRODUCT_DELETE_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@ProductCrudActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
