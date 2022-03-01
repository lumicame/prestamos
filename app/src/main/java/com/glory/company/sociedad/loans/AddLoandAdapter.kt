package com.glory.company.sociedad.loans

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view_client_dialog.view.*
import java.lang.Exception
import java.text.DecimalFormat



class AddLoandAdapter(private val client: ArrayList<User>, private val context: Context) : RecyclerView.Adapter<AddLoandAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_client_dialog, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(client[p1],context)
    }

    override fun getItemCount()=client.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nformat = DecimalFormat("##,###,###.##")
        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference
        fun bindItems(item: User, context: Context) {

            if (item.avatar=="default")
            else{
                storageRef.child(item.avatar!!).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it.toString()).placeholder(R.drawable.progress_animation ).into(itemView.client_avatar)
                }.addOnFailureListener {
                    // Handle any errors
                }
            }
            itemView.client_name.text=item.name
            itemView.client_nit.text=""+item.nit
            try {
                if (item.points!!<=0){
                    itemView.client_points.text=""+0
                    itemView.ratingBar.rating= 0F
                }else
                {
                    val df = DecimalFormat("#.#")
                    itemView.client_points.text=""+df.format((item.points!!.toFloat()/item.dues!!.toFloat()))
                    itemView.ratingBar.rating= (item.points!!.toFloat()/item.dues!!.toFloat())
                }

            }catch (e: Exception){}


        }

    }
}