package com.glory.company.sociedad.Entry

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import com.glory.company.sociedad.loans.ViewLoandActivity
import kotlinx.android.synthetic.main.recycler_view_egress.view.*
import java.text.DecimalFormat

class AdapterExpense(private val products: ArrayList<Egress>, private val context: Context) : RecyclerView.Adapter<AdapterExpense.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_egress, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(products[p1],context)
    }

    override fun getItemCount()=products.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nformat = DecimalFormat("##,###,###.##")
        fun bindItems(item: Egress, context: Context) {
            if (!item.loand.equals("null")){
            itemView.container.setOnClickListener {
                val intent= Intent(context, ViewLoandActivity::class.java)
                intent.putExtra("id",item.loand)
                context.startActivity(intent)
            }}
            itemView.name_egress.text=item.name
            itemView.date.text=item.date
            itemView.total.text="$"+nformat.format(item.total)
            itemView.by_nit.text=item.user_nit


        }
    }
}