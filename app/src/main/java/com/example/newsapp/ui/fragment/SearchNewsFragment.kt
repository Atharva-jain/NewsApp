package com.example.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.adapter.OnClickArticle
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.model.Article
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsapp.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(), OnClickArticle {

    private val TAG: String = "SearchNewsFragment: "
    lateinit var viewModel: NewsViewModel
    private lateinit var searchNewsBinding: FragmentSearchNewsBinding
    lateinit var searchNewsAdapter: NewsAdapter
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerAdapter()
        var job: Job? = null
        searchNewsBinding.tvSearchNews.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchForNews(editable.toString(), false)
                    }
                }
            }
        }
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        searchNewsAdapter.differ.submitList(newsResponse.articles.toList())

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchNewsBinding = FragmentSearchNewsBinding.inflate(layoutInflater)
        return searchNewsBinding.root
    }

    private fun hideProgressBar() {
        searchNewsBinding.searchPaginationProgressBar.visibility = View.INVISIBLE

    }

    private fun showProgressBar() {
        searchNewsBinding.searchPaginationProgressBar.visibility = View.VISIBLE

    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                val query: String = searchNewsBinding.tvSearchNews.text.toString()
                if(!query.isNullOrEmpty()){
                    viewModel.searchForNews(searchNewsBinding.tvSearchNews.text.toString(), true)
                }
            }
        }
    }

    private fun setupRecyclerAdapter() {
        searchNewsAdapter = NewsAdapter(this)
        searchNewsBinding.rvSearchNews.adapter = searchNewsAdapter
        searchNewsBinding.rvSearchNews.layoutManager = LinearLayoutManager(requireActivity())
        searchNewsBinding.rvSearchNews.addOnScrollListener(this@SearchNewsFragment.scrollListener)
    }

    override fun onClickArticle(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("articleData", article)
        }
        findNavController().navigate(
            R.id.action_searchNewsFragment_to_articleFragment,
            bundle
        )
    }
}