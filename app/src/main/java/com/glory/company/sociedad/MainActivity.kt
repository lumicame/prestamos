package com.glory.company.sociedad

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.Retrofit.ApiServices
import com.glory.company.sociedad.Retrofit.ApiUtils
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var mAPIService: ApiServices? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAPIService=ApiUtils.getAPIService()
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Cargando...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)

        val login_= login(this,"","")
        when {
            login_.verificar()==1 -> {
                val intent= Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }
            login_.verificar()==2 -> {
                val intent= Intent(this, EmployeActivity::class.java)
                startActivity(intent)
                finish()
            }
            login_.verificar()==3 -> {
                val intent= Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
            }
        }
        EditText_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                lookBtn()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                lookBtn()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                lookBtn()
            }
        })
        EditText_pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                lookBtn()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                lookBtn()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                lookBtn()
            }
        })
        Btn_login.setOnClickListener {
            progressDialog.show()
            val login=HashMap<String,Any>()
            login["nit"]=EditText_email.text.toString()
            login["password"]=EditText_pass.text.toString()
            mAPIService!!.loginUser(login)!!.enqueue(object : Callback<User?> {
                override fun onFailure(call: Call<User?>, t: Throwable) {
                    Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<User?>, response: retrofit2.Response<User?>) {
                    if (response.isSuccessful){
                        val user=response.body()
                        val editor = prefs.edit()
                        editor.putBoolean("login", true)
                        editor.putString("type",user!!.type)
                        editor.putString("pass",user!!.password)
                        editor.putString("email",user!!.nit)
                        editor.putInt("store_id",user!!.store_id)
                        editor.putString("name",user!!.name)
                        editor.apply()
                        if (login_.verificar()==1){
                            progressDialog.dismiss()
                            val intent= Intent(this@MainActivity, AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if (login_.verificar()==2){
                            progressDialog.dismiss()
                            val intent= Intent(this@MainActivity,
                                EmployeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if (login_.verificar()==3){
                            progressDialog.dismiss()
                            val intent= Intent(this@MainActivity,
                                AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }
                }


            })

        }
    }

    fun lookBtn(){
        Btn_login.isEnabled = !(EditText_email.text.isNullOrEmpty()||EditText_pass.text.isNullOrEmpty())
    }

}