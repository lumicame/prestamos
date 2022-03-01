package com.glory.company.sociedad.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glory.company.sociedad.R
import java.text.DecimalFormat
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.Retrofit.ApiServices
import com.glory.company.sociedad.Retrofit.ApiUtils
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.cardview_addemployee.view.*
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception


class EmployeAdapter(private val clients: ArrayList<User>, private val context: Context) : RecyclerView.Adapter<EmployeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.cardview_addemployee, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(clients[p1],context,p1)
    }

    override fun getItemCount()=clients.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var mAPIService: ApiServices? = null
        val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
        val nformat = DecimalFormat("##,###,###.##")
        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference
        fun bindItems(item: User, context: Context,indice:Int) {
            mAPIService= ApiUtils.getAPIService()
            itemView.content.setOnLongClickListener {
               // createDeleteDialog(context, item.id!!,item.avatar!!).show()
                true
            }
            itemView.content.setOnClickListener{
                //createEditClientDialog(context,item,indice).show()
            }
            itemView.user_name.text=item.name
            itemView.user_pass.text=item.password
            itemView.user_nit.text=item.nit

            try {
                var string=""
                if (!item.routes.isNullOrEmpty()){
                    for (item in item.routes!!){
                        string+=item.name+"\n"
                    }
                    itemView.user_routes.text=string
                }

            }catch (e:Exception){}

        }
        fun createEditClientDialog(context: Context,user: User,indice: Int): AlertDialog {
            val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val dialog: Dialog
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val v = inflater.inflate(R.layout.dialog_addclient, null)
            v.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            builder.setView(v)
            dialog=builder.create()
            val title = v.findViewById(R.id.title) as TextView
            val name = v.findViewById(R.id.user_add_name) as EditText
            val tel = v.findViewById(R.id.user_add_tel) as EditText
            val addrres = v.findViewById(R.id.user_add_address) as EditText
            val nit = v.findViewById(R.id.user_add_nit) as EditText
            val btn = v.findViewById(R.id.Btn_registro) as Button
            val content = v.findViewById(R.id.content_image) as RelativeLayout
            content.visibility=View.GONE
            title.text = context.getString(R.string.edit_client)
            nit.isEnabled=false
            btn.text = context.getString(R.string.edit)
            nit.setText(user.nit)
            tel.setText(user.tel)
            name.setText(user.name)
            addrres.setText(user.address)
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle(context.getString(R.string.wait))
            progressDialog.setMessage(context.getString(R.string.saving))
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.setCancelable(false)
            name.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    btn.isEnabled = !(name.text.isNullOrEmpty())

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    btn.isEnabled = !(name.text.isNullOrEmpty())

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btn.isEnabled = !(name.text.isNullOrEmpty())

                }

            })
            btn.setOnClickListener {
                progressDialog.show()
                            // Create a new user with a first and last name

                            user.name = name.text.toString()
                            user.tel = tel.text.toString()
                            user.address=addrres.text.toString()
                            user.nit=nit.text.toString()
                mAPIService!!.editClient(user)!!.enqueue(object : Callback<User?> {
                    override fun onFailure(call: Call<User?>, t: Throwable) {
                        //Toast.makeText(this@ClientActivity,t.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<User?>, response: retrofit2.Response<User?>) {
                        if (response.isSuccessful){
                            progressDialog.dismiss()
                            dialog.dismiss()
                            //ClientActivity.update=true
                            //ClientActivity.activity.toString()
                            Toast.makeText(context, R.string.registeeditclient, Toast.LENGTH_LONG).show()

                        }
                    }


                })

                                     }

            return dialog
        }
        /*fun createDeleteDialog(context: Context, user:Long,photo:String): AlertDialog {
            val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Espere")
            progressDialog.setMessage("Eliminando...")
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false)

            val builder = AlertDialog.Builder(context)

            builder.setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.delete_message_client))
                .setPositiveButton(
                    context.getString(R.string.delete)
                ) { dialog, which ->
                    progressDialog.show()
                    val docRef = db.collection("users").document(user)
                    docRef.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            storageRef.child(photo).delete().addOnCompleteListener {

                            }
                            progressDialog.dismiss()
                            dialog.dismiss()
                            ClientActivity.delete=true
                            ClientActivity.toString()
                            Toast.makeText(context,context.getString(R.string.delete_client),Toast.LENGTH_SHORT).show()

                        }
                    }

                }
                .setNegativeButton(
                    context.getString(R.string.cancel)
                ) { dialog, which -> dialog.dismiss() }

            return builder.create()
        }*/
    }


}