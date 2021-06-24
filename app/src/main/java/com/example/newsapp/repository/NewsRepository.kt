package com.example.newsapp.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsResponse
import retrofit2.Response

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun getBreakingNewsAccordingToCategory(countryCode: String, category: String, pageNumber: Int): Response<NewsResponse>{
        return RetrofitInstance.api.getBreakingNewsAccordingToCategory(countryCode, category, pageNumber)
    }

    suspend fun upsert(article: Article) = db.getArticleDoa().upsert(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDoa().deleteArticle(article)

    fun getSavedNews(): List<Article> {
        return db.getArticleDoa().getAllArticles()
    }

    fun searchSaveNews(searchQuery: String): List<Article>{
        return db.getArticleDoa().searchSaveNews(searchQuery)
    }

}