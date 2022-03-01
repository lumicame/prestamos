package com.glory.company.sociedad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_begin.*
import java.util.HashMap

class BeginActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin)

        Btn_register.setOnClickListener {
            if (name_admin.text.isNullOrEmpty()
                && password_admin.text.isNullOrEmpty()
                && nit_admin.text.isNullOrEmpty()
                && name_store.text.isNullOrEmpty()
                && capital_store.text.isNullOrEmpty()){
                    Toast.makeText(this,"Completa todos los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val id=db.collection("databases").id
            val databases = HashMap<String, Any>()
            databases["active"] = true
            databases["cash"]= capital_store.text.toString().toInt()
            databases["cash_initial"]= capital_store.text.toString().toInt()
            databases["client"]= 1000
            databases["consecutive"]="0"
            databases["id"]=id
            databases["name"]= name_store.text.toString()
            db.collection("databases").document(id).set(databases).addOnCompleteListener {
                val users = HashMap<String, Any>()
                users["database"] = id
                users["name"] = name_admin.text.toString()
                users["nit"]= nit_admin.text.toString()
                users["pass"]= password_admin.text.toString()
                users["type"]="admin"
                db.collection("users").document(users["nit"].toString()).set(users).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Configuración guardada con exito ya puedes ingresar.",Toast.LENGTH_SHORT).show()
                        val intent= Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }


        }
    }
}