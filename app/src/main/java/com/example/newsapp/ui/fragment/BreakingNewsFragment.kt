package com.example.newsapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.adapter.OnClickArticle
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.model.Article
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsapp.util.Resource
import com.google.android.material.snackbar.Snackbar


class BreakingNewsFragment : Fragment(), OnClickArticle {

    private val TAG: String = "BreakingNewsFragment: "
    private lateinit var breakingNewsBinding: FragmentBreakingNewsBinding
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    var isLoading  = false
    var isLastPage = false
    var isScrolling = false
    var category: String = "general"
    var myCountryCode: String = "in"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedCountry = requireActivity().getSharedPreferences("country", Context.MODE_PRIVATE)
        val countryCode: String? = sharedCountry.getString("countryCode", "in")
        Log.d(TAG,"Country Code Save is :$countryCode")
        viewModel = (activity as NewsActivity).viewModel
        if(countryCode != null){
            myCountryCode = countryCode
            viewModel.getBreakingNewsAccordingToCategory(countryCode, "general")
        }else{
            myCountryCode = "in"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, "general")
        }

        setupRecyclerAdapter()
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPage = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPage
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "$message", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })

        breakingNewsBinding.selectCountryButton.setOnClickListener {
            findNavController().navigate(R.id.action_breakingNewsFragment_to_selectCountryFragment)
        }

        breakingNewsBinding.businessChip.setOnClickListener {
            category = "business"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
        breakingNewsBinding.entertainmentChip.setOnClickListener {
            category = "entertainment"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
        breakingNewsBinding.generalChip.setOnClickListener {
            category = "general"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
        breakingNewsBinding.healthChip.setOnClickListener {
            category = "health"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
        breakingNewsBinding.scienceChip.setOnClickListener {
            category = "science"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
        breakingNewsBinding.sportChip.setOnClickListener {
            category = "sport"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
        breakingNewsBinding.technologyChip.setOnClickListener {
            category = "technology"
            viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        breakingNewsBinding = FragmentBreakingNewsBinding.inflate(layoutInflater)
        return breakingNewsBinding.root
    }

    private fun hideProgressBar() {
        Log.d(TAG,"Progress Bar Invisible")
        breakingNewsBinding.paginationProgressBar.visibility = View.INVISIBLE
        breakingNewsBinding.paginationProgressBar.alpha = 0F
        isLoading = false
    }

    private fun showProgressBar() {
        Log.d(TAG,"Progress Bar Visible")
        breakingNewsBinding.paginationProgressBar.visibility = View.VISIBLE
        breakingNewsBinding.paginationProgressBar.alpha = 1F
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotLoadingAndNotLastPage
                    && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNewsAccordingToCategory(myCountryCode, category)
                isScrolling = false
            }/* else{
                breakingNewsBinding.rvBreakingNews.setPadding(0,0,0,0)
            }*/


        }
    }

    private fun setupRecyclerAdapter() {
        newsAdapter = NewsAdapter(this)
        breakingNewsBinding.rvBreakingNews.adapter = newsAdapter
        breakingNewsBinding.rvBreakingNews.layoutManager = LinearLayoutManager(requireActivity())
        breakingNewsBinding.rvBreakingNews.addOnScrollListener(this@BreakingNewsFragment.scrollListener)
    }

    override fun onClickArticle(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("articleData", article)
        }
        findNavController().navigate(
            R.id.action_breakingNewsFragment_to_articleFragment,
            bundle
        )
    }
}