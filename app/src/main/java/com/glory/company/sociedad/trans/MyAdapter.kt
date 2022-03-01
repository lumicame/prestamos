package com.glory.company.sociedad.trans

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import com.glory.company.sociedad.loans.ViewLoandActivity
import com.glory.company.sociedad.loans.loand
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.recycler_view_trans.view.*
import java.text.DecimalFormat
import java.util.HashMap


class MyAdapter(private val trans: ArrayList<Trans>, private val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_trans, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(trans[p1],context)
    }

    override fun getItemCount()=trans.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var db = FirebaseFirestore.getInstance()
        val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
        val nformat = DecimalFormat("##,###,###.##")
        fun bindItems(item: Trans, context:Context) {
            var prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val type=prefs!!.getString("type", "")
            if (type=="client"){
                //itemView.content_by.visibility=View.GONE
            }
            if (type=="admin"){
            itemView.container.setOnLongClickListener {
                createDeleteDialog(context,item.date_hour,item.loand,item.due,item.total).show()
                false
            }
            }
            itemView.date.text=item.date_hour
            itemView.by_nit.text=item.user_nit
            itemView.loand.text="#"+item.loand
            itemView.total.text="$"+nformat.format(item.total)
            itemView.description.text=item.description
            if ((type=="admin"||type=="employee") && item.loand!="0"){
                itemView.container.setOnClickListener {
                    val intent= Intent(context, ViewLoandActivity::class.java)
                    intent.putExtra("id",item.loand)
                    context.startActivity(intent)
                }
            }

           /* if (type=="admin"){
                itemView.setOnLongClickListener {
                    createDeleteDialog(context,item.date,item.id_loand,item.id_due).show()
                    true
                }
            }*/

        }
        fun createDeleteDialog(context: Context, id:String,id_loand:String,id_due:String,total:Double): AlertDialog {
            val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Espere")
            progressDialog.setMessage("Eliminando...")
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false)

            val builder = AlertDialog.Builder(context)

            builder.setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.delete_message_charge))
                .setPositiveButton(
                    context.getString(R.string.delete)
                ) { dialog, which ->
                    progressDialog.show()
                    val docRef = db.collection("databases").document(database!!).collection("charges").document(id)
                    db.collection("databases").document(database!!).collection("loands")
                        .document(id_loand).get().addOnCompleteListener {
                            if (it.result!!.exists()){
                                val loan= it.result!!.toObject(loand::class.java)
                                var id_=0
                                for (item_ in loan?.dues!!){
                                    if (id_.toString()==id_due){
                                        if (item_.state==true){
                                            item_.state=false
                                            loan.slopes+=1
                                        }

                                        item_.due= item_.due!! + total.toInt()
                                        val charg = HashMap<String,Any>()
                                        charg["dues"]= loan.dues!!
                                        charg["paidout"]=loan.paidout-total
                                        charg["slopes"]=loan.slopes
                                        db.collection("databases").document(database).collection("loands")
                                            .document(id_loand).update(charg).addOnCompleteListener {
                                                docRef.delete().addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        progressDialog.dismiss()
                                                        dialog.dismiss()
                                                        Toast.makeText(context,context.getString(R.string.delete_charge), Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                             }
                                    }
                                    id_++
                                }
                            }
                            else{
                                progressDialog.dismiss()
                                Toast.makeText(context,context.getString(R.string.error_delete_charge), Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            }

                    }

                }
                .setNegativeButton(
                    context.getString(R.string.cancel)
                ) { dialog, which -> dialog.dismiss() }

            return builder.create()
        }
    }
}