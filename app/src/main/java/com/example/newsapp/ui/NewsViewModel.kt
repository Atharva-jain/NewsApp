package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.NewsApplication
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    private val TAG: String = "NewsViewModel:----"
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchSaveNews: MutableLiveData<List<Article>> = MutableLiveData()
    val searchGetAllNews: MutableLiveData<List<Article>> = MutableLiveData()
    var searchNewsPage = 1
    var categoryName = "general"
    var isCategoryRequest = false
    var searchNewsResponse: NewsResponse? = null

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun getBreakingNewsAccordingToCategory(
        countryCode: String,
        category: String
    ) {
        isCategoryRequest = categoryName == category
        Log.d(TAG, "Comparison Country Value $isCategoryRequest")
        Log.d(TAG, "Save Category Name $categoryName")
        Log.d(TAG, "Give Category Name $category")
        viewModelScope.launch {
            breakingNews.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = newsRepository.getBreakingNewsAccordingToCategory(
                        countryCode,
                        category,
                        breakingNewsPage
                    )
                    categoryName = category
                    Log.d(TAG, "Save Category Name when request Send $categoryName")
                    Log.d(TAG, "Give Category Name when request Send $category")
                    breakingNews.postValue(handleBreakingNewsResponse(response))
                } else {
                    breakingNews.postValue(Resource.Error("No Internet Connection"))
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> breakingNews.postValue(Resource.Error("Network Failed"))
                    else -> breakingNews.postValue(Resource.Error("${t.message}"))
                }
            }
        }
    }

    fun searchForNews(searchQuery: String, isScroll: Boolean) = viewModelScope.launch {
//        searchNews.postValue(Resource.Loading())
//        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
//        searchNews.postValue(handleSearchNewsResponse(response, isScroll))
        safeSearchNewsCall(searchQuery, isScroll)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (breakingNewsPage == 1) {
                    breakingNewsResponse = resultResponse
                } else {
                    if (!isCategoryRequest) {
                        breakingNewsPage = 1
                        Log.d(TAG, "When New Category Choose : $breakingNewsPage")
                        breakingNewsResponse = resultResponse
                    } else {
                        if (breakingNewsResponse == null) {
                            breakingNewsResponse = resultResponse
                        } else {
                            val oldArticles = breakingNewsResponse?.articles
                            val newArticle = resultResponse.articles
                            oldArticles?.addAll(newArticle)
                        }
                    }
                }
                Log.d(TAG, "Breaking News Page Number value : $breakingNewsPage")
                breakingNewsPage++
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(
        response: Response<NewsResponse>,
        isScroll: Boolean
    ): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (!isScroll) {
                    searchNewsPage = 1
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    if (searchNewsResponse == null) {
                        searchNewsResponse = resultResponse
                    } else {
                        val oldArticles = searchNewsResponse?.articles
                        val newArticle = resultResponse.articles
                        oldArticles?.addAll(newArticle)
                    }
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() {
        val article = newsRepository.getSavedNews()
        searchSaveNews.postValue(article)
    }

    fun searchSaveNews(searchQuery: String){
        val articles = newsRepository.searchSaveNews(searchQuery)
        searchSaveNews.postValue(articles)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error ${t.message}"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String, isScroll: Boolean) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response, isScroll))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error ${t.message}"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}