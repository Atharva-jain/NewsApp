 package com.example.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.model.Article

class NewsAdapter(private val listener: OnClickArticle): RecyclerView.Adapter<NewsAdapter.NewsAdapterHolder>() {
    class NewsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivArticleImage: ImageView = itemView.findViewById(R.id.ivArticleImage)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvSource: TextView = itemView.findViewById(R.id.tvSource)
        val tvPublishedAt: TextView = itemView.findViewById(R.id.tvPublishedAt)
        val articleLayout: ConstraintLayout = itemView.findViewById(R.id.articleLayout)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterHolder {
        return NewsAdapterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview, parent, false))
    }

    override fun onBindViewHolder(holder: NewsAdapterHolder, position: Int) {
        val article = differ.currentList[position]
        Glide.with(holder.ivArticleImage.context).load(article.urlToImage).into(holder.ivArticleImage)
        holder.tvSource.text = article.source?.name
        holder.tvTitle.text = article.title
        holder.tvDescription.text = article.description
        holder.tvPublishedAt.text = article.publishedAt
        holder.articleLayout.setOnClickListener {
            listener.onClickArticle(article)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
interface OnClickArticle{
    fun onClickArticle(article: Article)
}