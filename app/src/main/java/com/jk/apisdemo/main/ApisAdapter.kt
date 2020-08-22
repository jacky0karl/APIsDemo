package com.jk.apisdemo.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jk.apisdemo.R
import com.jk.apisdemo.model.Api
import java.util.*


class ApisAdapter : RecyclerView.Adapter<ApisAdapter.ViewHolder?>() {
    private var list: List<Api> = ArrayList<Api>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_api, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val api: Api = list[position]
        holder.name?.setText(api.name)
        holder.url?.setText(api.url)
    }

    fun setData(list: List<Api>?) {
        if (list != null) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null
        var url: TextView? = null

        init {
            name = itemView.findViewById(R.id.name)
            url = itemView.findViewById(R.id.url)
        }
    }

}