package com.example.healthyme.UI.View.News

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthyme.Data.Model.Article
import com.example.healthyme.databinding.NewsMakananBinding
import com.squareup.picasso.Picasso

class NewsAdapter(private val articles: List<Article>, private val onClick: (Article) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsMakananBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
        holder.itemView.setOnClickListener {
            onClick(article)  // Memanggil listener klik
        }
    }

    override fun getItemCount() = articles.size

    class NewsViewHolder(private val binding: NewsMakananBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                title.text = article.title
                Picasso.get().load(article.urlToImage).into(image)
            }
        }
    }
}

