package com.example.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsapp.R
import com.example.newsapp.adapter.SelectionFragmentPagerAdapter
import com.example.newsapp.databinding.FragmentTabHolderBinding
import com.example.newsapp.ui.transmission.ZoomOutPageTransformer
import com.google.android.material.tabs.TabLayoutMediator

class TabFragmentHolder : Fragment() {

    private lateinit var holderBinding: FragmentTabHolderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        holderBinding = FragmentTabHolderBinding.inflate(layoutInflater)
        val fragmentList: ArrayList<Fragment> = ArrayList()
        fragmentList.add(BreakingNewsFragment())
        fragmentList.add(SelectCountryFragment())
        val adapter = SelectionFragmentPagerAdapter(childFragmentManager, lifecycle, fragmentList)
        holderBinding.optionNewsViewPager2.setPageTransformer(ZoomOutPageTransformer())
        holderBinding.optionNewsViewPager2.adapter = adapter
        TabLayoutMediator(holderBinding.optionNewsTabLayout, holderBinding.optionNewsViewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_all_news)
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_favorite)
                }

            }
        }.attach()
        return holderBinding.root
    }
}