package com.glory.company.sociedad.Entry

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.recycler_view_entry.view.*
import java.text.DecimalFormat

class AdapterEntry(private val products: ArrayList<entry>, private val context: Context) : RecyclerView.Adapter<AdapterEntry.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_entry, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(products[p1],context)
    }

    override fun getItemCount()=products.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nformat = DecimalFormat("##,###,###.##")
        val PREFS_FILENAME = "com.company.glory.store.prefs"
        var db = FirebaseFirestore.getInstance()

        @SuppressLint("ResourceType")
        fun bindItems(item: entry, context: Context) {
            itemView.by_nit.text=item.nit

            itemView.total.text="$"+nformat.format(item.total!!)
            itemView.description.text=""+item.name
            itemView.date.text=item.date

        }
    }
}