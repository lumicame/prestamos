package com.glory.company.sociedad.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Models.Route
import com.glory.company.sociedad.R
import com.glory.company.sociedad.loans.LoansActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.recycler_view_route_redound.view.*
import java.text.DecimalFormat

class RouteAdapter(private val routes: ArrayList<Route>, private val context: Context) : RecyclerView.Adapter<RouteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_route_redound, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(routes[p1],context)
    }

    override fun getItemCount()=routes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var db = FirebaseFirestore.getInstance()
        val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
        val nformat = DecimalFormat("##,###,###.##")

        fun bindItems(item: Route, context: Context) {
            itemView.container.setOnClickListener {
                val intent= Intent(context, LoansActivity::class.java)
                intent.putExtra("route",item.id)
                intent.putExtra("route_name",item.name)
                context.startActivity(intent)
            }
            var count=0

            itemView.num.text=count.toString()

            itemView.name.text=item.name
        }

    }
}