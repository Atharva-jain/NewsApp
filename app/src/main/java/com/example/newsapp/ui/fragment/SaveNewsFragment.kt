package com.example.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.adapter.OnClickArticle
import com.example.newsapp.databinding.FragmentSaveNewsBinding
import com.example.newsapp.model.Article
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SaveNewsFragment : Fragment(), OnClickArticle {

    private val TAG: String = "SaveNewsFragment:---"
    private lateinit var saveNewsAdapter: NewsAdapter
    lateinit var viewModel: NewsViewModel
    private lateinit var saveNewsBinding: FragmentSaveNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerAdapter()

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = saveNewsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view, "successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(saveNewsBinding.rvSavedArticle)
        }

        viewModel.getSavedNews()
//        viewModel.searchGetAllNews.observe(viewLifecycleOwner, Observer { articles ->
//            Log.d(TAG, "Articles: - $articles")
//            if(articles.isNotEmpty()){
//                saveNewsAdapter.differ.submitList(articles)
//            }
//        })
        var job: Job? = null
        saveNewsBinding.tvSearchNewNews.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    Log.d(TAG,"$editable")
                    if (editable.toString().isNotEmpty()) {
                        val query = "%${editable.toString()}%"
                        Log.d(TAG,"Sent Query : $query")
                        viewModel.searchSaveNews(query)
                    } else {
                        viewModel.getSavedNews()
                    }
                }
            }
        }
        viewModel.searchSaveNews.observe(viewLifecycleOwner, Observer { articles ->
            Log.d(TAG, "Articles: - $articles")
            if(articles.isNotEmpty()){
                saveNewsAdapter.differ.submitList(articles)
            }else{
                Toast.makeText(requireContext(), "List is empty", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveNewsBinding = FragmentSaveNewsBinding.inflate(layoutInflater)
        return saveNewsBinding.root
    }

    private fun setupRecyclerAdapter() {
        saveNewsAdapter = NewsAdapter(this)
        saveNewsBinding.rvSavedArticle.adapter = saveNewsAdapter
        saveNewsBinding.rvSavedArticle.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onClickArticle(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("articleData", article)
        }
        findNavController().navigate(
            R.id.action_saveNewsFragment_to_articleFragment,
            bundle
        )
    }
}