package com.glory.company.sociedad
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore


class login(val context: Context, val email: String, val pass: String){
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)

    fun verificar(): Int {
       val login= prefs!!.getBoolean("login", false)
       val type= prefs!!.getString("type", "")
        if (login){
            if (type=="adm")
                return 1
            else if (type=="emp")
                return 2
            else if(type=="sup")
            return 3
            else return 0
        }
        else return 0
    }
    fun singin():Boolean{
        var sing=false

        return sing
    }
}