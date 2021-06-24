package com.example.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.model.Article
import com.example.newsapp.model.Source

@Dao
interface ArticleDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("select * from articles")
    fun getAllArticles(): List<Article>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("Select * From articles Where author LIKE :searchQuery OR content LIKE :searchQuery OR description LIKE :searchQuery OR publishedAt LIKE :searchQuery OR title LIKE :searchQuery")
    fun searchSaveNews(searchQuery: String): List<Article>
}