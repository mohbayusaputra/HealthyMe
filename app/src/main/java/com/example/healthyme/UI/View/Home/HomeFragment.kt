package com.example.healthyme.UI.View.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthyme.Data.Model.Nutrition
import com.example.healthyme.R
import com.example.healthyme.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.opencsv.CSVReader
import java.io.InputStreamReader

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Deklarasikan nutritionList sebagai properti kelas
    private lateinit var nutritionList: List<Nutrition>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Ambil username dari SharedPreferences
        loadProfileData()
        // Baca data dari CSV
        nutritionList = readCSVData(requireContext(), "nutrition_clustered.csv")

        setupChips()
        setupAutoSlider(binding)

        // Setup RecyclerView
        setupRecyclerView(nutritionList)

        val fabAdd = binding.root.findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.imageTintList = null // Menghapus tinting
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), BMRActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "FAB clicked!", Toast.LENGTH_SHORT).show()
            // Tambahkan logika untuk tindakan FAB
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()  // Memastikan data selalu terupdate ketika fragment kembali aktif
    }

    fun loadProfileData() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("BMRResults")
            database.child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val bmrValue = dataSnapshot.child("bmr").getValue(Float::class.java)
                    if (bmrValue != null && bmrValue != 0f) {
                        val editor = sharedPreferences.edit()
                        editor.putFloat("bmr", bmrValue)
                        editor.apply()

                        binding.nilaiTargetKal.text = "$bmrValue Kcal"
                        updateProgressBar(bmrValue)
                    } else {
                        binding.nilaiTargetKal.text = "BMR not available"
                        updateProgressBar(0f)
                    }
                }
            }

            // Mengambil nilai progress_kalori
            val progressDatabase = FirebaseDatabase.getInstance().getReference("progress_kalori")
            progressDatabase.child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val progressKalori = snapshot.child("progress_kalori").getValue(Float::class.java) ?: 0f
                    val editor = sharedPreferences.edit()
                    editor.putFloat("progress_kalori", progressKalori)
                    editor.apply()

                    // Update nilai progress bar dan UI
                    binding.nilaiProgressKal.text = "$progressKalori"
                    updateProgressBar(progressKalori)
                } else {
                    // Jika tidak ada progress_kalori di database, reset progress_kalori ke 0
                    val editor = sharedPreferences.edit()
                    editor.putFloat("progress_kalori", 0f)
                    editor.apply()

                    // Reset nilai progress bar
                    binding.nilaiProgressKal.text = "0"
                    updateProgressBar(0f)
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load progress data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.welcomeText.text = "Welcome, $username"
    }



    fun updateProgressBar(progress_kalori: Float) {
        if (isAdded) {
            val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", AppCompatActivity.MODE_PRIVATE)
            val bmrValue = sharedPreferences.getFloat("bmr", 0f)

            if (bmrValue > 0) {
                val progress = ((progress_kalori / bmrValue) * 100).toInt()
                binding.nilaiProgressKal.text = "$progress_kalori"
                binding.ProgressKal.progress = progress
            }
        }
    }


    private fun setupAutoSlider(binding: FragmentHomeBinding) {
        val images = listOf(
            R.drawable.auto_slide1,
            R.drawable.auto_slide2,
            R.drawable.auto_slide3
        )

        binding.autoSlider.adapter = AutoSliderAdapter(images, binding.autoSlider)
        binding.dotsIndicator.attachTo(binding.autoSlider)
    }

    private fun addToFavorites(nutrition: Nutrition) {
        val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Tambah ke favorit")
                .setMessage("Apakah kamu ingin menambahkan ${nutrition.name} ke favoritmu?")
                .setPositiveButton("Iya") { _, _ ->
                    val database = FirebaseDatabase.getInstance().getReference("Favorit").child(userId)
                    val foodData = hashMapOf(
                        "id" to nutrition.id,
                        "calories" to nutrition.calories,
                        "proteins" to nutrition.proteins,
                        "fat" to nutrition.fat,
                        "carbohydrate" to nutrition.carbohydrate,
                        "name" to nutrition.name,
                        "image" to nutrition.image
                    )

                    val foodRef = database.push()
                    foodRef.setValue(foodData)
                        .addOnSuccessListener {
                            // Mengambil nilai total kalori yang ada di SharedPreferences
                            val currentCalories = sharedPreferences.getFloat("progress_kalori", 0f)
                            val newTotalCalories = currentCalories + nutrition.calories

                            // Menyimpan total kalori terbaru ke SharedPreferences
                            saveProgressToPreferences(newTotalCalories)

                            // Menampilkan pesan sukses
                            Toast.makeText(requireContext(), "${nutrition.name} telah ditambahkan ke favorit!", Toast.LENGTH_SHORT).show()

                            // Memperbarui progress bar
                            updateProgressBar(newTotalCalories)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Error adding to favorites: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Gamau") { dialogInterface, _ -> dialogInterface.dismiss() }
                .create()

            dialog.show()
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProgressToPreferences(progress_kalori: Float) {
        val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("progress_kalori", progress_kalori)
        editor.apply()

        // Menyimpan progress_kalori ke Firebase Realtime Database
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("progress_kalori")

            // Update data pada dokumen dengan user_id yang sesuai
            val progressData = hashMapOf(
                "progress_kalori" to progress_kalori
            )

            // Menyimpan atau mengupdate data berdasarkan user_id
            database.child(userId).setValue(progressData) // Fungsi setValue akan menggantikan data yang ada
                .addOnSuccessListener {
                    Log.d("HomeFragment", "Progress Kalori saved to Firebase successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeFragment", "Error saving progress kalori to Firebase: ${exception.message}")
                }
        }
    }



    // Fungsi untuk memfilter data berdasarkan cluster
    private fun filterDataByCluster(cluster: Int) {
        val filteredList = if (cluster == -1) {
            nutritionList // Menampilkan semua data
        } else {
            nutritionList.filter { it.cluster == cluster }
        }
        setupRecyclerView(filteredList) // Update RecyclerView dengan data yang difilter
    }

    //function untuk membuat chip
    private fun setupChips() {
        val chipGroup: ChipGroup = binding.chipgroup
        val chip1: Chip = binding.chip1
        val chip2: Chip = binding.chip2
        val chip3: Chip = binding.chip3
        val chip4: Chip = binding.chip4

        binding.chip1.text = "Semua"
        binding.chip2.text = "Rendah Kalori"
        binding.chip3.text = "Sedang Kalori"
        binding.chip4.text = "Tinggi Kalori"

        val chips: List<Chip> = listOf(chip1, chip2, chip3, chip4)

        chips.forEach { chip ->
            chip.setChipBackgroundColorResource(R.color.chipLatar)
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        chipGroup.setOnCheckedChangeListener { _, _ ->
            chips.forEach { chip ->
                chip.setChipBackgroundColorResource(R.color.chipLatar)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }

        chip1.setOnClickListener {
            chip1.setChipBackgroundColorResource(R.color.chipKlik)
            chip1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            chip2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip3.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip4.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            chip2.setChipBackgroundColorResource(R.color.chipLatar)
            chip3.setChipBackgroundColorResource(R.color.chipLatar)
            chip4.setChipBackgroundColorResource(R.color.chipLatar)
            filterDataByCluster(-1) // Menampilkan semua makanan
        }

        chip2.setOnClickListener {
            chip2.setChipBackgroundColorResource(R.color.chipKlik)
            chip2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            chip1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip3.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip4.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            chip1.setChipBackgroundColorResource(R.color.chipLatar)
            chip3.setChipBackgroundColorResource(R.color.chipLatar)
            chip4.setChipBackgroundColorResource(R.color.chipLatar)
            filterDataByCluster(0) // Menampilkan makanan cluster 0
        }

        chip3.setOnClickListener {
            chip3.setChipBackgroundColorResource(R.color.chipKlik)
            chip3.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            chip2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip4.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip1.setChipBackgroundColorResource(R.color.chipLatar)
            chip2.setChipBackgroundColorResource(R.color.chipLatar)
            chip4.setChipBackgroundColorResource(R.color.chipLatar)
            filterDataByCluster(1) // Menampilkan makanan cluster 1
        }

        chip4.setOnClickListener {
            chip4.setChipBackgroundColorResource(R.color.chipKlik)
            chip4.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            chip2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip3.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            chip1.setChipBackgroundColorResource(R.color.chipLatar)
            chip2.setChipBackgroundColorResource(R.color.chipLatar)
            chip3.setChipBackgroundColorResource(R.color.chipLatar)
            filterDataByCluster(2) // Menampilkan makanan cluster 2
        }
    }

    private fun readCSVData(context: android.content.Context, fileName: String): List<Nutrition> {
        val nutritionItems = mutableListOf<Nutrition>()

        try {
            val inputStream = context.assets.open(fileName)
            val reader = CSVReader(InputStreamReader(inputStream))
            val rows = reader.readAll()
            rows.removeAt(0) // Hapus header CSV

            for (row in rows) {
                val id = row[0].toInt()
                val calories = row[1].toFloat()
                val proteins = row[2].toFloat()
                val fat = row[3].toFloat()
                val carbohydrate = row[4].toFloat()
                val name = row[5]
                val image = row[6]
                val cluster = row[7].toInt()

                nutritionItems.add(Nutrition(id, calories, proteins, fat, carbohydrate, name, image, cluster))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return nutritionItems
    }

    private fun setupRecyclerView(nutritionList: List<Nutrition>) {
        val adapter = NutritionAdapter(nutritionList) { nutrition ->
            addToFavorites(nutrition)  // Menambahkan ke favorit saat item diklik
        }
        binding.newsVerticalList.layoutManager = LinearLayoutManager(requireContext())
        binding.newsVerticalList.adapter = adapter
    }


}
