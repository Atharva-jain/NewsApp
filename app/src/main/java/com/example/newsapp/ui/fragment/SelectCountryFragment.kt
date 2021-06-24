package com.example.newsapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.SelectCountryAdapter
import com.example.newsapp.adapter.SelectCountryListener
import com.example.newsapp.databinding.FragmentSelectCountryBinding
import com.example.newsapp.model.Country
import com.example.newsapp.ui.NewsViewModel


class SelectCountryFragment : Fragment(), SelectCountryListener {

    private val TAG: String = "SelectCountryFragment:----"
    private lateinit var countryBinding: FragmentSelectCountryBinding
    private lateinit var selectCountryAdapter: SelectCountryAdapter
    private lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val countryList: ArrayList<Country> = getCountryList()
        Log.d(TAG,"Countries : $countryList")
        viewModel = (activity as NewsActivity).viewModel
        val sharedCountry = requireActivity().getSharedPreferences("country", Context.MODE_PRIVATE)
        val countryCode: String? = sharedCountry.getString("countryCode", "in")
        Log.d(TAG,"Get CountryCode from Shared Preference: $countryCode")
        selectCountryAdapter = SelectCountryAdapter(countryList, countryCode,this)
        countryBinding.rvSelectCountry.adapter = selectCountryAdapter
        countryBinding.rvSelectCountry.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getCountryList(): ArrayList<Country> {
        val list = ArrayList<Country>()
        val ae = Country("United Arab Emirates", R.drawable.ae,"ae")
        list.add(ae)
        val ar = Country("Argentina", R.drawable.ar,"ar")
        list.add(ar)
        val au = Country("Australia", R.drawable.au,"au")
        list.add(au)
        val be = Country("Belgium", R.drawable.be,"be")
        list.add(be)
        val br = Country("Brazil", R.drawable.br,"br")
        list.add(br)
        val ca = Country("Canada", R.drawable.ca,"ca")
        list.add(ca)
        val cn = Country("China", R.drawable.cn,"cn")
        list.add(cn)
        val de = Country("Germany", R.drawable.de,"de")
        list.add(de)
        val eg = Country("Egypt", R.drawable.eg,"eg")
        list.add(eg)
        val fr = Country("France", R.drawable.fr,"fr")
        list.add(fr)
        val gb = Country("United Kingdom", R.drawable.gb,"gb")
        list.add(gb)
        val hk = Country("Hong Kong", R.drawable.hk,"hk")
        list.add(hk)
        val il = Country("Israel", R.drawable.il,"il")
        list.add(il)
        val india = Country("India", R.drawable.india,"in")
        list.add(india)
        val it = Country("Italy", R.drawable.it,"it")
        list.add(it)
        val jp = Country("Japan", R.drawable. jp,"jp")
        list.add(jp)
        val kr = Country("South Korea", R.drawable. kr,"kr")
        list.add(kr)
        val ru = Country("Russia", R.drawable. ru,"ru")
        list.add(ru)
        val sa = Country("Saudi Arabia", R.drawable. sa,"sa")
        list.add(sa)
        val us = Country("United State", R.drawable. us,"us")
        list.add(us)
        return list
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        countryBinding = FragmentSelectCountryBinding.inflate(layoutInflater)
        return countryBinding.root
    }

    override fun selectCountryListener(country: Country) {
        val sharedCountry = requireActivity().getSharedPreferences("country", Context.MODE_PRIVATE)
        val editor = sharedCountry.edit()
        editor.putString("countryCode", country.countryCode)
        editor.commit()
        Log.d(TAG, "Save In Presence By selected User is ${country.countryCode}")
        viewModel.breakingNewsPage = 1
        findNavController().navigate(R.id.action_selectCountryFragment_to_breakingNewsFragment)
    }


}