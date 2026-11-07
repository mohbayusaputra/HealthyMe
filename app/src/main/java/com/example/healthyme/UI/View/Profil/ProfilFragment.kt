package com.example.healthyme.UI.View.Profil

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthyme.Data.Model.Article
import com.example.healthyme.Data.Repository.NewsRepository
import com.example.healthyme.R
import com.example.healthyme.UI.View.Home.BMRActivity
import com.example.healthyme.UI.View.LoginActivity
import com.example.healthyme.UI.View.News.NewsAdapter
import com.example.healthyme.UI.View.News.WebViewActivity
import com.example.healthyme.UI.ViewModel.NewsViewModel
import com.example.healthyme.UI.ViewModel.NewsViewModelFactory
import com.example.healthyme.databinding.FragmentProfilBinding

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: NewsViewModel by activityViewModels {
//        NewsViewModelFactory(NewsRepository())
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfilBinding.inflate(inflater, container, false)

        // Menampilkan data profil dari SharedPreferences
        loadProfileData()

        // Setup toolbar
        setupProfilToolbar(binding)

        // Menampilkan menu ListView
        setupListActivity(binding)

        // setupObservers()

        //viewModel.fetchNews("konsumsi makanan sehat", "id", getString(R.string.news_api_key))


        return binding.root
    }

//    private fun setupObservers() {
//        viewModel.newsLiveData.observe(viewLifecycleOwner, Observer { response ->
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    setupRecyclerView(it.articles)
//                }
//            }
//        })
//    }

//    private fun setupRecyclerView(articles: List<Article>) {
//        val adapter = NewsAdapter(articles) { article ->
//            val intent = Intent(requireContext(), WebViewActivity::class.java)
//            intent.putExtra("url", article.url)
//            intent.putExtra("description", article.title)
//            startActivity(intent)
//        }
//
//        binding.newsRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            this.adapter = adapter
//        }
//    }

    private fun loadProfileData() {
        // Ambil data dari SharedPreferences untuk menampilkan data terbaru
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("user_preferences", 0)
        val username = sharedPreferences.getString("username", "Username Not Available")
        val email = sharedPreferences.getString("email", "Email Not Available")

        // Menampilkan username dan email di TextView menggunakan binding
        binding.username.text = username
        binding.email.text = email
    }

    private fun setupProfilToolbar(binding: FragmentProfilBinding) {
        binding.toolbar.setTitle("Profile")
    }

    private fun setupListActivity(binding: FragmentProfilBinding) {
        val menuList = listOf(
            ListActivityModel(
                R.drawable.ic_edit_profile,
                "Edit Profil",
                EditProfileActivity()::class.java
            ),
            ListActivityModel(
                R.drawable.ic_about,
                "About",
                AboutActivity()::class.java
            ),
            ListActivityModel(
                R.drawable.ic_logout,
                "Logout",
                null // Logout tidak memerlukan activity lain
            ),
        )

        // Set adapter untuk ListView
        val adapter = ListActivityAdapter(requireContext(), menuList)
        binding.listMore.adapter = adapter

        // Menangani item klik pada ListView
        binding.listMore.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = menuList[position]

            if (selectedItem.name == "Logout") {
                // kondisi jika logout
                showLogoutConfirmationDialog()
            } else {
                // Pindah ke Activity lain jika item bukan "Logout"
                selectedItem.activity?.let { activityClass ->
                    val intent = Intent(requireContext(), activityClass)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        // Menampilkan dialog konfirmasi logout
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setCancelable(false)
            .setPositiveButton("Iya") { dialog, _ ->
                // Jika pengguna memilih "Ya", lakukan logout
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Nanti aja deh") { dialog, _ ->
                // Jika pengguna memilih "Tidak", tutup dialog
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // Menghapus data login dari SharedPreferences
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("user_preferences", 0)
        val editor = sharedPreferences.edit()
        editor.clear()  // Menghapus semua data
        editor.apply()

        // Menampilkan toast untuk konfirmasi logout
        Toast.makeText(requireContext(), "Log Out Success", Toast.LENGTH_SHORT).show()

        // Pindah ke halaman Login setelah logout
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()  // Menutup aktivitas ProfilFragment agar tidak bisa kembali setelah logout
    }

    override fun onResume() {
        super.onResume()
        // Memuat ulang data profil setelah kembali dari EditProfileActivity
        loadProfileData()
    }

    // untuk memastikan binding dibersihkan ketika fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
