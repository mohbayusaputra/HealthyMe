package com.example.healthyme.UI.View.News

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthyme.Data.Repository.NewsRepository
import com.example.healthyme.R
import com.example.healthyme.UI.ViewModel.NewsViewModel
import com.example.healthyme.UI.ViewModel.NewsViewModelFactory
import com.example.healthyme.databinding.FragmentNewsBinding
import androidx.lifecycle.Observer
import com.example.healthyme.Data.Model.Article

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by activityViewModels {
        NewsViewModelFactory(NewsRepository())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        setupNewsToolbar(binding)

        setupObservers()

        viewModel.fetchNews("konsumsi makanan sehat", "id", getString(R.string.news_api_key))

        return  binding.root
    }

    private fun setupObservers() {
        viewModel.newsLiveData.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                response.body()?.let {
                    setupRecyclerView(it.articles)
                }
            }
        })
    }

    // Fungsi untuk mengatur RecyclerView dan adapter
//    private fun setupRecyclerView(articles2: List<Article2>) {
//        adapter = NewsAdapter(articles2.toMutableList()) { article ->
//            val intent = Intent(this, WebViewActivity::class.java)
//            intent.putExtra("url", article.url)
//            intent.putExtra("description", article.title)
//            startActivity(intent)
//        }
//        binding.recyclerView.adapter = adapter
//    }
//
//
    private fun setupRecyclerView(articles: List<Article>) {
        val adapter = NewsAdapter(articles) { article ->
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            intent.putExtra("url", article.url)
            intent.putExtra("description", article.title)
            startActivity(intent)
        }

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    private fun setupNewsToolbar(binding: FragmentNewsBinding) {
        binding.toolbar.setTitle("News")
    }

}