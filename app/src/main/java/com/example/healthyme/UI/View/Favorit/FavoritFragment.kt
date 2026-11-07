package com.example.healthyme.UI.View.Favorit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthyme.Data.Model.Nutrition
import com.example.healthyme.R
import com.example.healthyme.databinding.FragmentFavoritBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoritFragment : Fragment() {

    private var _binding: FragmentFavoritBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var favoritAdapter: FavoritAdapter
    private val listMakanan = mutableListOf<Nutrition>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritBinding.inflate(inflater, container, false)

        setupFavoritToolbar(binding)
        setupRecyclerView()

        // Mendapatkan user_id dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            // Mengakses referensi favorit berdasarkan user_id
            database = FirebaseDatabase.getInstance().getReference("Favorit").child(userId)
            fetchFavoritFoods()
        } else {
            Log.d("FavoritFragment", "User ID is null, unable to fetch data.")
        }

        return binding.root
    }

    private fun setupFavoritToolbar(binding: FragmentFavoritBinding) {
        binding.toolbar.setTitle("Favorit")
    }

    private fun setupRecyclerView() {
        favoritAdapter = FavoritAdapter { food ->
            showRemoveFavoriteDialog(food)  // Menghapus makanan saat bookmark diklik
        }
        binding.newsVerticalList.layoutManager = LinearLayoutManager(context)
        binding.newsVerticalList.adapter = favoritAdapter
    }

    private fun fetchFavoritFoods() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listMakanan.clear()
                    for (dataSnapshot in snapshot.children) {
                        val nutrition = dataSnapshot.getValue(Nutrition::class.java)
                        nutrition?.firebaseKey = dataSnapshot.key  // Menyimpan firebaseKey di objek nutrition
                        if (nutrition != null) {
                            listMakanan.add(nutrition)
                        } else {
                            Log.d("FavoritFragment", "Nutrition object is null")
                        }
                    }
                    favoritAdapter.submitList(listMakanan)
                } else {
                    Log.d("FavoritFragment", "No data found in Firebase")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavoritFragment", "Failed to read value.", error.toException())
            }
        })
    }


    private fun showRemoveFavoriteDialog(food: Nutrition) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah kamu yakin ingin menghapus makanan ini dari favorit?")
            .setPositiveButton("Iya, gabutuh lagi") { _, _ ->
                // Jika pengguna mengonfirmasi, hapus makanan
                removeFavoriteFood(food)
            }
            .setNegativeButton("Ga kok") { dialog, _ ->
                dialog.dismiss()  // Menutup dialog jika tidak setuju
            }
            .create()
            .show()
    }

    private fun removeFavoriteFood(food: Nutrition) {
        val foodId = food.firebaseKey
        if (foodId != null) {
            // Ambil nilai total kalori saat ini dari Firebase
            val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", AppCompatActivity.MODE_PRIVATE)
            val currentCalories = sharedPreferences.getFloat("progress_kalori", 0f)

            // Mengurangi kalori
            val newCalories = (currentCalories - food.calories).coerceAtLeast(0f)

            // Update total kalori di Firebase
            val userId = sharedPreferences.getString("user_id", null)
            if (userId != null) {
                val userRef = FirebaseDatabase.getInstance().getReference("progress_kalori").child(userId)

                // Periksa apakah node 'total_calories' ada di Firebase, jika tidak, buat node tersebut
                userRef.child("progress_kalori").setValue(newCalories)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Kalori berhasil diperbarui di Firebase, lanjutkan penghapusan favorit
                            database.child(foodId).removeValue().addOnCompleteListener { removeTask ->
                                if (removeTask.isSuccessful) {
                                    listMakanan.remove(food)
                                    favoritAdapter.submitList(listMakanan)

                                    // Perbarui SharedPreferences
                                    with(sharedPreferences.edit()) {
                                        putFloat("progress_kalori", newCalories)
                                        apply()
                                    }

                                    refreshFavoriteFragment()
                                } else {
                                    Log.e("FavoritFragment", "Failed to remove food from Firebase: ${removeTask.exception?.message}")
                                }
                            }
                        } else {
                            Log.e("FavoritFragment", "Failed to update calories in Firebase: ${task.exception?.message}")
                        }
                    }
            } else {
                Log.e("FavoritFragment", "User ID is null, unable to update calories in Firebase.")
            }
        } else {
            Log.e("FavoritFragment", "Food ID is null, unable to remove.")
        }
    }

    private fun refreshFavoriteFragment() {
        // Untuk me-refresh fragment favorit, Anda bisa menggunakan metode berikut:
        // Ini akan memuat ulang fragment favorit atau mengganti fragment dengan fragment yang sama.
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, FavoritFragment())  // Ganti dengan ID container yang sesuai
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
