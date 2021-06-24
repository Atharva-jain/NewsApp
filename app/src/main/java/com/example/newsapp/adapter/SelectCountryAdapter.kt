package com.example.newsapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.model.Country

class SelectCountryAdapter(
    private val countryList: ArrayList<Country>,
    private val countryCode: String?,
    private val listener: SelectCountryListener
) : RecyclerView.Adapter<SelectCountryAdapter.SelectCountryHolder>() {
    private val TAG: String = "SelectCountyAdapter:---"

    class SelectCountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val correctImage: ImageView = itemView.findViewById(R.id.ivCountrySelect)
        val countryImage: ImageView = itemView.findViewById(R.id.ivCountryImage)
        val countryName: TextView = itemView.findViewById(R.id.tvCountryName)
        val countryLayout: ConstraintLayout = itemView.findViewById(R.id.selectCountryLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCountryHolder {
        return SelectCountryHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.country_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SelectCountryHolder, position: Int) {
        val country = countryList[position]
        Log.d(TAG,"Country Code is $countryCode")
        if(countryCode != null){
            if (countryCode == country.countryCode) {
                Log.d(TAG,"Country Save is $countryCode and FormClass ${country.countryCode}")
                holder.correctImage.visibility = View.VISIBLE
            }else{
                holder.correctImage.visibility = View.INVISIBLE
            }
        }
        Glide.with(holder.countryImage.context).load(country.countryImage).into(holder.countryImage)
        holder.countryName.text = country.countryName
        holder.countryLayout.setOnClickListener {
            listener.selectCountryListener(country)
        }
    }
    override fun getItemCount(): Int {
        return countryList.size
    }
}

interface SelectCountryListener {
    fun selectCountryListener(country: Country)
}