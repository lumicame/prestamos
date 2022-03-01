package com.glory.company.sociedad.loans

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import kotlinx.android.synthetic.main.recycler_view_dues.view.*
import java.text.DecimalFormat

class DuesAdapter (private val dues: ArrayList<due>, private val context: Context) : RecyclerView.Adapter<DuesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_dues, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(dues[p1],context)
    }

    override fun getItemCount()=dues.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nformat = DecimalFormat("##,###,###.##")

        fun bindItems(item: due, context: Context) {

            itemView.num.text=""+item.num
            itemView.date.text=""+item.date
            itemView.due.text="$"+nformat.format(item.due)


        }

    }
}