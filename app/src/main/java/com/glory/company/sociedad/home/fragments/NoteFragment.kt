package com.glory.company.sociedad.home.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Models.Note
import com.glory.company.sociedad.R
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment : Fragment() {
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var db = FirebaseFirestore.getInstance()
    var ListsNotes = ArrayList<Note>()
    var recycler: RecyclerView? =null
    var fab_add:FloatingActionButton?=null
    var not_results:TextView?=null

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view=inflater.inflate(R.layout.activity_notes, null)
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "FALSE")
        recycler= view!!.findViewById(R.id.recycler)
        fab_add=view!!.findViewById(R.id.fab_add)
        not_results=view!!.findViewById(R.id.not_results)
        recycler!!.layoutManager = LinearLayoutManager(context)
        recycler!!.hasFixedSize()

        fab_add!!.setOnClickListener {
            //createAddNoteDialog().show()
        }
        //Load()
        return view
    }

  /*  fun Load(){
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "FALSE")
        val type=prefs!!.getString("type", "")
        if (type=="admin"){
            if (AdminActivity.ListsNotes.isNotEmpty()){
                try {
                    ListsNotes.clear()
                    for (item in AdminActivity.ListsNotes){
                        ListsNotes.add(item)
                    }
                    not_results!!.visibility= View.GONE
                    recycler!!.adapter = AdapterNotes(ListsNotes, context!!)
                } catch (e: Exception) { }
            }
            else{
                try {
                    not_results!!.visibility= View.VISIBLE
                    recycler!!.adapter = AdapterNotes(ListsNotes, context!!)
                } catch (e: Exception) { }
            }
        }
        else{
            if (EmployeActivity.ListsNotes.isNotEmpty()){
                try {
                    ListsNotes.clear()
                    for (item in EmployeActivity.ListsNotes){
                        ListsNotes.add(item)
                    }
                    not_results!!.visibility= View.GONE
                    recycler!!.adapter = AdapterNotes(ListsNotes, context!!)
                } catch (e: Exception) { }
            }
            else{
                try {
                    not_results!!.visibility= View.VISIBLE
                    recycler!!.adapter = AdapterNotes(ListsNotes, context!!)
                } catch (e: Exception) { }
            }
        }

    }

    fun createAddNoteDialog(): AlertDialog {
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val user=prefs!!.getString("email", "")
        val dialog: Dialog

        val builder = AlertDialog.Builder(context!!)

        val inflater = this.layoutInflater

        val v = inflater.inflate(R.layout.dialog_add_note, null)

        builder.setView(v)
        dialog=builder.create()
        val description = v.findViewById(R.id.add_note_name) as EditText
        val btn = v.findViewById(R.id.add_note_btn) as Button

        val progressDialog = ProgressDialog(context!!)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setMessage(getString(R.string.saving))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn.isEnabled = !(description.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btn.isEnabled = !(description.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn.isEnabled = !(description.text.isNullOrEmpty())

            }

        })

        btn.setOnClickListener {
            progressDialog.show()
            val note = HashMap<String,Any>()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDateandTime = sdf.format(Calendar.getInstance().time)
            note["id"] = db.collection("users").document(user!!).collection("notes").document().id
            note["note"]= description.text.toString()
            note["date"]=currentDateandTime
            db.collection("users").document(user!!).collection("notes").document(note["id"].toString()).set(note).addOnCompleteListener {
                Toast.makeText(context,getString(R.string.note_successfull), Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                Load()
                dialog.dismiss()
            }

        }
        return dialog
    }*/
}