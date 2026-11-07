package com.example.healthyme.UI.View.Prediksi

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthyme.databinding.FragmentPrediksiBinding
import com.google.firebase.database.FirebaseDatabase

class PrediksiFragment : Fragment() {

    private lateinit var env: OrtEnvironment
    private lateinit var session: OrtSession

    private var _binding: FragmentPrediksiBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi ONNX Runtime dan load model
        env = OrtEnvironment.getEnvironment()
        val assetManager = requireContext().assets
        val modelInputStream = assetManager.open("kmeans_model_v9.onnx")
        val modelBytes = modelInputStream.readBytes()
        session = env.createSession(modelBytes)
        modelInputStream.close()

        // Ambil userId dan username dari Shared Preferences
        loadUserDetails(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPrediksiBinding.inflate(inflater, container, false)

        // Setup toolbar
        setupPrediksiToolbar()

        // Set onClickListener for the button
        binding.btnCekHasil.setOnClickListener { performPrediction() }

        return binding.root
    }

    private fun setupPrediksiToolbar() {
        binding.toolbar.title = "Analisa Nilai Makanan"
    }

    private fun loadUserDetails(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null) ?: ""
        username = sharedPreferences.getString("username", null) ?: ""

        if (userId.isEmpty() || username.isEmpty()) {
            Toast.makeText(context, "Data pengguna tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performPrediction() {
        try {
            // Ambil input dari pengguna
            val kalori = binding.inputKalori.text.toString().toFloatOrNull() ?: 0.0f
            val protein = binding.inputProtein.text.toString().toFloatOrNull() ?: 0.0f
            val fat = binding.inputFat.text.toString().toFloatOrNull() ?: 0.0f
            val karbohidrat = binding.inputKarbohidrat.text.toString().toFloatOrNull() ?: 0.0f

            // Validasi input kosong
            if (kalori == 0.0f && protein == 0.0f && fat == 0.0f && karbohidrat == 0.0f) {
                Toast.makeText(requireContext(), "Input tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                return
            }

            // Buat input tensor
            val inputData = arrayOf(floatArrayOf(kalori, protein, fat, karbohidrat))
            val inputTensor = OnnxTensor.createTensor(env, inputData)

            // Jalankan prediksi
            val output = session.run(mapOf("float_input" to inputTensor))
            val cluster = (output[0].value as LongArray)[0] // Cluster hasil

            // Tentukan hasil cluster
            val hasilPrediksi = when (cluster) {
                0L -> "Cluster 0: Makanan rendah kalori dan protein atau makanan ringan"
                1L -> "Cluster 1: Makanan seimbang kalori dan protein"
                2L -> "Cluster 2: Makanan tinggi kalori dan protein"
                else -> "Cluster tidak diketahui"
            }

            // Tampilkan hasil
            binding.result.text = hasilPrediksi

            // Simpan ke Realtime Database jika userId valid
            if (userId.isNotEmpty() && username.isNotEmpty()) {
                savePredictionToRealtimeDatabase(kalori, protein, fat, karbohidrat, hasilPrediksi)
            } else {
                Toast.makeText(requireContext(), "User ID atau username tidak valid. Data tidak disimpan.", Toast.LENGTH_SHORT).show()
            }

            // Tutup tensor
            inputTensor.close()
        } catch (e: Exception) {
            binding.result.text = "Error: ${e.message}"
        }
    }

    private fun savePredictionToRealtimeDatabase(
        kalori: Float,
        protein: Float,
        fat: Float,
        karbohidrat: Float,
        hasilPrediksi: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val predictionsRef = database.getReference("Predictions").child(userId)

        val predictionData = mapOf(
            "username" to username,
            "kalori" to kalori,
            "protein" to protein,
            "fat" to fat,
            "karbohidrat" to karbohidrat,
            "hasilPrediksi" to hasilPrediksi,
            "timestamp" to System.currentTimeMillis()
        )

        // Simpan data prediksi ke Realtime Database berdasarkan userId
        predictionsRef.push().setValue(predictionData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Prediksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan prediksi.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
