package com.glory.company.sociedad.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import kotlinx.android.synthetic.main.recycler_view_user_info.view.*
import java.text.DecimalFormat

class AdapterUserInfo(private val products: ArrayList<UserInfoClass>, private val context: Context) : RecyclerView.Adapter<AdapterUserInfo.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_user_info, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(products[p1],context)
    }

    override fun getItemCount()=products.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nformat = DecimalFormat("##,###,###.##")

        fun bindItems(item: UserInfoClass, context: Context) {

            itemView.name.text=item.name
            itemView.cash.text="$"+nformat.format(item.cash)
            itemView.expenses.text="$"+nformat.format(item.expenses)
            itemView.credit.text="$"+nformat.format(item.credit)
            if (item.balance<0){
                itemView.balance.setTextColor(context.resources.getColor(R.color.red_400))
                itemView.balance.text="-$"+nformat.format(item.balance)
            }else{
                itemView.balance.setTextColor(context.resources.getColor(R.color.blue_400))
                itemView.balance.text="+$"+nformat.format(item.balance)
            }


        }

    }
}