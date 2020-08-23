package com.jk.apisdemo.history

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.jk.apisdemo.R
import com.jk.apisdemo.impl.LogDbHelper
import com.jk.apisdemo.model.Api
import com.jk.apisdemo.model.ApiLog
import com.jk.apisdemo.utils.DateUtil
import java.util.*


class HistoryAdapter(activity: Activity) : RecyclerView.Adapter<HistoryAdapter.ViewHolder?>() {
    private var list: List<ApiLog> = ArrayList<ApiLog>()
    private var activity: Activity? = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log: ApiLog = list[position]
        if (LogDbHelper.STATUS_OK == log.status) {
            holder.status?.setText(R.string.fetching_ok)
        } else {
            holder.status?.setText(R.string.fetching_nok)
        }

        holder.time?.setText(log.date?.let { DateUtil.getDateTime(it) })
        holder.detailBtn?.setOnClickListener(View.OnClickListener {
            log.response?.let { showDetail(it) }
        })
    }

    fun setData(list: List<ApiLog>?) {
        if (list != null) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    fun showDetail(response: String) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder?.apply {
            setPositiveButton(R.string.ok) { _, _ -> }
        }
        builder?.setMessage(response)
        builder?.create()?.show()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var status: TextView? = null
        var time: TextView? = null
        var detailBtn: Button? = null

        init {
            status = itemView.findViewById(R.id.status)
            time = itemView.findViewById(R.id.time)
            detailBtn = itemView.findViewById(R.id.detailBtn)
        }
    }

}