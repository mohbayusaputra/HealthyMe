# HealthyMe 🥗

**HealthyMe** adalah aplikasi Android untuk membantu pengguna menjalani pola makan yang lebih sehat. Aplikasi ini menyediakan informasi nilai gizi makanan, kalkulator BMR, analisis nilai makanan berbasis Machine Learning (K-Means / ONNX), serta berita seputar makanan sehat.

## ✨ Fitur

- **Welcome & Splash Screen** – onboarding untuk pengguna baru.
- **Login & Register** – akun pengguna disimpan di Firebase Realtime Database.
- **Home** – slider otomatis dan daftar makanan beserta kandungan gizinya (kalori, protein, lemak, karbohidrat) dari dataset `nutrition_clustered.csv`.
- **Kalkulator BMR** – menghitung kebutuhan kalori harian (Basal Metabolic Rate).
- **Analisa Nilai Makanan (Prediksi)** – mengelompokkan makanan berdasarkan kalori, protein, lemak, dan karbohidrat menggunakan model K-Means (`kmeans_model_v9.onnx`) yang dijalankan on-device dengan ONNX Runtime.
- **Favorit** – simpan makanan favorit.
- **Berita Kesehatan** – berita seputar makanan sehat dari [NewsAPI](https://newsapi.org/), dapat dibuka langsung lewat WebView.
- **Profil** – lihat & edit profil, halaman About.

## 🛠️ Tech Stack

| Kategori | Teknologi |
|---|---|
| Bahasa | Kotlin |
| UI | XML Layout, ViewBinding, Material Components, Navigation Component, Shimmer |
| Arsitektur | MVVM (ViewModel + LiveData + Repository) |
| Networking | Retrofit, Gson, OkHttp Logging Interceptor |
| Database | Firebase Realtime Database, Firestore, Room |
| Machine Learning | ONNX Runtime Android (K-Means) |
| Gambar | Glide, Picasso |
| Lainnya | OpenCSV, DotsIndicator |

## 📁 Struktur Proyek

```
app/src/main/java/com/example/healthyme/
├── Data/
│   ├── Api/            # Retrofit & NewsAPI
│   ├── Model/          # Data class (Article, Nutrition, dll.)
│   └── Repository/     # NewsRepository
├── UI/
│   ├── View/           # Activity & Fragment (Home, News, Prediksi, Favorit, Profil)
│   └── ViewModel/      # NewsViewModel
├── splash_screen/
└── welcome_screen/
```

## 🚀 Cara Menjalankan

1. **Clone repository**
   ```bash
   git clone https://github.com/mohbayusaputra/HealthyMe.git
   ```
2. **Buka di Android Studio** (versi terbaru disarankan, JDK 11+).
3. **Siapkan Firebase**
   - Buat project di [Firebase Console](https://console.firebase.google.com/) dan tambahkan aplikasi Android dengan package `com.example.healthyme`.
   - Unduh `google-services.json` lalu letakkan di folder `app/`.
     (Lihat format contoh di `app/google-services.json.example`.)
   - Aktifkan **Realtime Database**.
4. **Siapkan NewsAPI key**
   - Daftar di [newsapi.org](https://newsapi.org/) untuk mendapatkan API key.
   - Tambahkan baris berikut ke file `local.properties` di root proyek:
     ```properties
     NEWS_API_KEY=api_key_kamu
     ```
5. **Sync Gradle** lalu jalankan aplikasi di emulator atau perangkat (minimal Android 8.1 / API 27).

> ⚠️ File `google-services.json` dan `local.properties` berisi data sensitif sehingga **tidak** disertakan di repository ini.

