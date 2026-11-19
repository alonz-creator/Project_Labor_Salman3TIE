package com.example.salman3tie.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salman3tie.AuthActivity
import com.example.salman3tie.R
import com.example.salman3tie.databinding.FragmentHomeBinding
import com.example.salman3tie.home.data.api.CatFactApiClient
import com.example.salman3tie.home.data.api.PhotoApiClient
import com.example.salman3tie.home.photo.PhotoAdapter
import com.example.salman3tie.pertemuan_2.SecondActivity
import com.example.salman3tie.pertemuan_3.ThirdActivity
import com.example.salman3tie.pertemuan_4.FourthActivity
import com.example.salman3tie.pertemuan_5.FifthActivity
import com.example.salman3tie.pertemuan_6.ProfileActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur Toolbar
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(binding.toolbar)

        (requireActivity() as AppCompatActivity)
            .supportActionBar?.title = "Home"

        // Menampilkan nama user dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )

        val username = sharedPref.getString("username", "User")
        binding.textWelcome.text = getString(
            R.string.welcome_user,
            username
        )

        // Navigasi ke SecondActivity
        binding.btnToSecond.setOnClickListener {
            startActivity(
                Intent(requireContext(), SecondActivity::class.java)
            )
        }

        // Navigasi ke ThirdActivity
        binding.btnToThird.setOnClickListener {
            startActivity(
                Intent(requireContext(), ThirdActivity::class.java)
            )
        }

        // Navigasi ke FourthActivity
        binding.btnToFourth.setOnClickListener {
            val intent = Intent(requireContext(), FourthActivity::class.java)
            intent.putExtra("name", "Politeknik Caltex Riau")
            intent.putExtra("from", "Rumbai")
            intent.putExtra("age", 25)
            startActivity(intent)
        }

        // Navigasi ke FifthActivity
        binding.btnToFifth.setOnClickListener {
            startActivity(
                Intent(requireContext(), FifthActivity::class.java)
            )
        }

        // Profil Developer
        binding.btnProfile.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }

        // Navigasi ke TenthActivity
        binding.btnToTenth.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    com.example.salman3tie.pertemuan_10.TenthActivity::class.java
                )
            )
        }

        // Navigasi ke EleventhActivity
        binding.btnToEleventh.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    com.example.salman3tie.pertemuan11.EleventhActivity::class.java
                )
            )
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { dialog, _ ->

                    sharedPref.edit {
                        clear()
                    }

                    dialog.dismiss()

                    startActivity(
                        Intent(requireContext(), AuthActivity::class.java)
                    )

                    requireActivity().finish()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Refresh Cat Fact
        binding.btnRefresh.setOnClickListener {
            loadCatFact()
        }

        // Memuat data pertama kali
        loadCatFact()
        loadPhoto()
    }

    // Mengambil fakta kucing dari API
    private fun loadCatFact() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = CatFactApiClient.apiService.getCatFact()

                Log.d("RESULT", response.toString())

                binding.tvCatFact.text = "\"${response.fact}\""

            } catch (e: Exception) {
                Log.e("CAT_FACT_ERROR", e.message ?: "Unknown error")

                binding.tvCatFact.text =
                    "Gagal mengambil fakta kucing."
            }
        }
    }

    // Mengambil foto dari API
    private fun loadPhoto() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val photos = PhotoApiClient.apiService.getPhotos()

                val adapter = PhotoAdapter(photos)

                binding.rvGallery.adapter = adapter

                // Tampilan RecyclerView Vertical
                //binding.rvGallery.layoutManager =
                    LinearLayoutManager(requireContext())

                // Tampilan Horizontal
                // binding.rvGallery.layoutManager =
                //     LinearLayoutManager(
                //         requireContext(),
                //         LinearLayoutManager.HORIZONTAL,
                //         false
                //     )

                // Tampilan Grid
                 binding.rvGallery.layoutManager =
                     GridLayoutManager(requireContext(), 2)

            } catch (e: Exception) {
                Log.e("PHOTO_ERROR", e.message ?: "Unknown error")

                Toast.makeText(
                    requireContext(),
                    "Gagal memuat gambar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Membersihkan binding ketika Fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
