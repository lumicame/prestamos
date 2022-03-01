package com.glory.company.sociedad.employee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Models.Route
import com.glory.company.sociedad.R
import kotlinx.android.synthetic.main.recycler_view_route_dialog.view.*

class RouteAdapter(private val routes: ArrayList<Route>, private val context: Context) : RecyclerView.Adapter<RouteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_route_dialog, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(routes[p1],context)
    }

    override fun getItemCount()=routes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(item: Route, context: Context) {
            var check=false
            if (check)
                itemView.img_check.visibility=View.VISIBLE
            else
                itemView.img_check.visibility=View.GONE
            itemView.route_name.text=item.name
            itemView.route_description.text=""+item.description


        }

    }
}