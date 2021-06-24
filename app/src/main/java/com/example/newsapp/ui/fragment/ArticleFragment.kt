package com.example.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {

    lateinit var viewModel: NewsViewModel
    private lateinit var articleBinding : FragmentArticleBinding
    private val args by navArgs<ArticleFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        val article = args.articleData
        articleBinding.articleWebView.apply {
            webViewClient = WebViewClient()
            if(article.url != null){
                loadUrl(article.url!!)
            }

        }
        articleBinding.saveArticleFloatingActionButton.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully.....", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        articleBinding = FragmentArticleBinding.inflate(layoutInflater)
        return articleBinding.root
    }


}