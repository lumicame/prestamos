package com.glory.company.sociedad.employee

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.R
import com.glory.company.sociedad.Retrofit.ApiServices
import com.glory.company.sociedad.Adapters.ClientAdapter
import com.glory.company.sociedad.Retrofit.ApiUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.android.synthetic.main.activity_employee.*
import kotlinx.android.synthetic.main.activity_employee.back
import kotlinx.android.synthetic.main.activity_employee.fab_add
import kotlinx.android.synthetic.main.activity_employee.recycler
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class Employee : AppCompatActivity() {
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var lists = ArrayList<User>()
    var mAPIService: ApiServices? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)
        mAPIService= ApiUtils.getAPIService()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        recycler.adapter= ClientAdapter(lists,this)
        LoadUsers()
        fab_add.setOnClickListener {
            createAddEmployeeDialogo().show()
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }
    fun LoadUsers(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs.getString("type","")
        val store_id=prefs.getInt("store_id",0)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        val data=HashMap<String,Any>()
        data["store"]=store_id
        mAPIService!!.getEmployes(data)!!.enqueue(object : Callback<List<User>?> {
            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                Toast.makeText(this@Employee,t.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<User>?>, response: retrofit2.Response<List<User>?>) {
                if (response.isSuccessful){
                    lists.clear()
                    for (client in response.body()!!){
                        lists.add(client)
                    }
                    if (lists.isNotEmpty()){
                        recycler.adapter!!.notifyDataSetChanged()
                    }else{
                        //progressDialog.dismiss()
                    }
                }
            }


        })

    }
    fun createAddEmployeeDialogo(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val store_id=prefs.getInt("store_id",0)
        val dialog: Dialog

        val builder = AlertDialog.Builder(this)

        val inflater = this.getLayoutInflater()

        val v = inflater.inflate(R.layout.dialog_addemployee, null)

        builder.setView(v)
        dialog=builder.create()
        val name = v.findViewById(R.id.user_add_name) as EditText
        val nit = v.findViewById(R.id.user_add_nit) as EditText
        val pass = v.findViewById(R.id.user_add_pass) as EditText
        val btn = v.findViewById(R.id.Btn_registro) as Button
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Guardando...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

        })
        pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

        })
        nit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||nit.text.isNullOrEmpty()||pass.text.isNullOrEmpty())

            }

        })
        btn.setOnClickListener {
            progressDialog.show()
            val client = User()
            client.store_id=store_id
            client.name = name.text.toString()
            client.nit = nit.text.toString()
            client.type = "emp"
            client.password = pass.text.toString()
            mAPIService!!.addEmployee(client)!!.enqueue(object : Callback<User?> {
                override fun onFailure(call: Call<User?>, t: Throwable) {
                    Toast.makeText(this@Employee,t.message,Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    progressDialog.dismiss()
                }

                override fun onResponse(call: Call<User?>, response: retrofit2.Response<User?>) {
                    if (response.isSuccessful){
                        nit.text.clear()
                        pass.text.clear()
                        name.text.clear()
                        LoadUsers()
                        dialog.dismiss()
                        progressDialog.dismiss()
                        Snackbar.make(container, R.string.registersuccess, Snackbar.LENGTH_LONG)
                            .setAction("OK", null).show()
                    }
                    if (response.code()==404){
                        Toast.makeText(this@Employee,"Este nit no se puede usar",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        progressDialog.dismiss()
                    }
                }


            })
        }
        return dialog
    }
   /*
    fun createDeleteDialog(user:String): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        val dialog:Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val v = inflater.inflate(R.layout.dialog_select_route_employee, null)
        //v.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.setView(v)
        dialog=builder.create()
        val delete = v.findViewById(R.id.text_delete) as TextView
        val btn = v.findViewById(R.id.save_route) as Button
        val recycler = v.findViewById(R.id.recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        var lists = ArrayList<Route>()
        if (type=="admin"){
            lists.clear()
            for (item in AdminActivity.ListsRoute){
                lists.add(
                    item
                )
            }
            recycler.adapter = RouteAdapter(lists, applicationContext)
        }
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Guardando...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        recycler.addOnItemTouchListener(
            RecyclerTouchListener(applicationContext, recycler,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        lists.get(position).check = !lists.get(position).check
                        recycler.adapter!!.notifyItemChanged(position)
                    }
                    override fun onLongClick(view: View?, position: Int) {
                        true
                    }
                })
        )
        btn.setOnClickListener {
            progressDialog.show()
            var lists_f = ArrayList<Route>()
            for (string in lists){
                if (string.check)
                    lists_f.add(string)
            }
            db.collection("users").document(user).update("routes",lists_f).addOnCompleteListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Rutas añadida correctamente",Toast.LENGTH_SHORT).show()
                LoadUsers()
                dialog.dismiss()
            }
        }


        delete.setOnClickListener {
            val docRef = db.collection("users").document(user)
            docRef.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    LoadUsers()
                    progressDialog.dismiss()
                    dialog.dismiss()
                    Snackbar.make(container, getString(R.string.delete_employee), Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show()
                }
            }
        }

        return dialog
    }*/
}
