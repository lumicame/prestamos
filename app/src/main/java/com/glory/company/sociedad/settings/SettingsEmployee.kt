package com.glory.company.sociedad.settings

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.glory.company.sociedad.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_settings_employee.*
import java.util.HashMap

class SettingsEmployee : AppCompatActivity() {
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_employee)
        LoadStores()

        back.setOnClickListener {
            onBackPressed()
        }
    }
    fun save(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setMessage(getString(R.string.saving))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        val user =  HashMap<String,Any>()
        user["name"] = settings_name.text.toString()
        user["pass"] = settings_pass.text.toString()

        val editor = prefs.edit()
        editor.putString("pass",user["pass"].toString())
        editor.putString("name",user["name"].toString())
        editor.apply()
        db.collection("users").document(settings_nit.text.toString()).update(user).addOnCompleteListener {
                progressDialog.dismiss()
        }
    }
    fun LoadStores(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        settings_nit.setText(prefs!!.getString("email", ""))
        settings_name.setText(prefs!!.getString("name", ""))
        settings_pass.setText(prefs!!.getString("pass", ""))
        val mySnackbar = Snackbar.make(
            settings_nit,
            getString(R.string.save_changes), Snackbar.LENGTH_INDEFINITE)
        mySnackbar.setAction(R.string.save) { save() }
        settings_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!mySnackbar.isShown){
                    mySnackbar.show()
                }
            }

        })
        settings_pass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!mySnackbar.isShown){
                    mySnackbar.show()
                }
            }

        })

    }
}
