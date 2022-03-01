package com.glory.company.sociedad.trans

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Entry.AdapterExpense
import com.glory.company.sociedad.Entry.Egress
import com.glory.company.sociedad.R
import com.glory.company.sociedad.history.AdapterUserInfo
import com.glory.company.sociedad.history.UserInfoClass
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.glory.company.sociedad.home.SlopesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_trans.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class TransActivity : AppCompatActivity() {
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var db = FirebaseFirestore.getInstance()

    var ListsCharges= ArrayList<Trans>()
    var ListsExpenses= ArrayList<Egress>()
    var cash_=0.0
    var egres=0.0
    var egress_credit=0.0
    val nformat = DecimalFormat("##,###,###.##")
    var entry=true

    private val CERO = "0"
    private val BARRA = "-"
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans)
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs!!.getString("type", "")
        val database=prefs!!.getString("database", "FALSE")
        val user=prefs!!.getString("email", "FALSE")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())



        fab_add.setOnClickListener {
            createAddEntryDialog().show()
        }

        if(type=="client"){
            getListsClient()
            fab_info.visibility=View.GONE
            fab_add_egress.visibility=View.GONE
            fab_add.visibility=View.GONE
            recycler_expenses.visibility=View.GONE
            content_resume.visibility=View.GONE


        }

        back.setOnClickListener {
            onBackPressed()
        }
        fab_add_egress.setOnClickListener {
            createAddEgressDialog().show()
        }
        fab_info.setOnClickListener {
            createInfoDialg().show()
        }
        recycler_entry.layoutManager = LinearLayoutManager(this)
        recycler_entry.hasFixedSize()
        recycler_expenses.layoutManager = LinearLayoutManager(this)
        recycler_expenses.hasFixedSize()
        recycler_expenses.adapter = AdapterExpense(ListsExpenses, this)
        recycler_entry.adapter = MyAdapter(ListsCharges, this)

        btn_entry!!.setOnClickListener {
            if (!entry){
                entry=true
                btn_entry!!.setBackgroundResource(R.drawable.buttonok)
                btn_expenses!!.setBackgroundResource(R.drawable.gray)
                recycler_entry!!.visibility=View.VISIBLE
                recycler_expenses!!.visibility=View.GONE
                recycler_entry!!.adapter= MyAdapter(ListsCharges, this)
                //recycler_entry!!.adapter!!.notifyDataSetChanged()
            }
        }
        btn_expenses!!.setOnClickListener {
            if (entry){
                entry=false
                btn_entry!!.setBackgroundResource(R.drawable.gray)
                btn_expenses!!.setBackgroundResource(R.drawable.buttonok)
                recycler_entry!!.visibility=View.GONE
                recycler_expenses!!.visibility=View.VISIBLE
                recycler_expenses!!.adapter= AdapterExpense(ListsExpenses, this)
                //recycler_expenses!!.adapter!!.notifyDataSetChanged()
            }
        }
        getLists()

    }


    /*fun getLists() {
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")

    }*/
    fun getLists() {

        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        if (type=="admin"){
            ListsCharges.clear()
            ListsExpenses.clear()
            if (AdminActivity.ListsCharges.size>0){
                for (document in AdminActivity.ListsCharges) {
                    ListsCharges.add(document)
                }
            }
            if (AdminActivity.ListsExpenses.size>0){
                for (document in AdminActivity.ListsExpenses) {
                    ListsExpenses.add(document)
                }
            }
            if (ListsExpenses.isNotEmpty()){
                egres=0.0
                egress_credit=0.0
                for (document in ListsExpenses){
                    if (document.loand.equals("null")){
                        egres+= document.total!!
                    }
                    else{
                        egress_credit+= document.total!!
                    }
                }
            }
            if (ListsCharges.isNotEmpty()){
                cash_=0.0
                for (document in ListsCharges){
                    cash_+=document.total
                }
            }

        }
        else{
            ListsCharges.clear()
            ListsExpenses.clear()
            if (EmployeActivity.ListsCharges.size>0){
                for (document in EmployeActivity.ListsCharges) {
                    ListsCharges.add(document)
                }
            }
            if (EmployeActivity.ListsExpenses.size>0){
                for (document in EmployeActivity.ListsExpenses) {
                    ListsExpenses.add(document)
                }
            }
            if (ListsExpenses.isNotEmpty()){
                egres=0.0
                egress_credit=0.0
                for (document in ListsExpenses){
                    if (document.loand.equals("null")){
                        egres+= document.total!!

                    }
                    else{
                        egress_credit+= document.total!!
                    }
                }
            }
            if (ListsCharges.isNotEmpty()){
                cash_=0.0
                for (document in ListsCharges){
                    cash_+=document.total
                }
            }
            fab_info.visibility=View.GONE
            fab_add.visibility=View.GONE
        }
        val balance=cash_-egres
        credit.text="$"+nformat.format(egress_credit)
        egress.text="$"+nformat.format(egres)
        box_credit.text="$"+nformat.format(egress_credit)
        box_expenses.text="-$"+nformat.format(egres)
        cash.text="$"+nformat.format(cash_)
        box_total.text="+$"+nformat.format(cash_)
        recycler_expenses.adapter!!.notifyDataSetChanged()
        recycler_entry.adapter!!.notifyDataSetChanged()
        if (balance<0){
            box_balance.setTextColor(resources.getColor(R.color.red_400))
        box_balance.text="-$"+nformat.format(cash_-egres)
        }
        else{
            box_balance.setTextColor(resources.getColor(R.color.blue_400))
            box_balance.text="+$"+nformat.format(cash_-egres)
        }

    }

    fun getListsClient(){
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val user=prefs!!.getString("email", "")
        db.collection("databases").document(database!!).collection("charges")
            .whereEqualTo("client",user).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (!querySnapshot!!.isEmpty){
                    ListsCharges.clear()
                    for (document in querySnapshot!!) {
                        ListsCharges.add(
                            Trans(
                                document["loand"].toString(),
                                document["due"].toString(),
                                document["date_hour"].toString(),
                                document["description"].toString(),
                                document["user_nit"].toString(),
                                document["total"].toString().toDouble()
                            )
                        )
                    }

                    try{
                        recycler_entry.adapter = MyAdapter(ListsCharges, this)
                    }catch (e:Exception){}

                }else{
                    recycler_entry.adapter = MyAdapter(ListsCharges, this)
                }
            }

    }
    fun createAddEgressDialog(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val nit_user=prefs!!.getString("email", "")
        val type=prefs!!.getString("type", "")
        val dialog: Dialog

        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater

        val v = inflater.inflate(R.layout.dialog_add_expense, null)

        builder.setView(v)
        dialog=builder.create()
        val description = v.findViewById(R.id.egress_description) as EditText
        val total = v.findViewById(R.id.total) as EditText
        val btnok = v.findViewById(R.id.pay) as Button
        val btncancel = v.findViewById(R.id.cancel) as Button

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Guardando...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

        })
        total.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

        })
        btncancel.setOnClickListener {
            dialog.dismiss()
        }
        btnok.setOnClickListener {
            progressDialog.show()
            db.collection("databases").document(database!!).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                    val currentDateandTime = sdf.format(Calendar.getInstance().getTime())
                    val currentDateandTime2 = sdf2.format(Calendar.getInstance().getTime())
                    val egress = HashMap<String,Any>()
                    egress["name"] = description.text.toString()
                    egress["date_hour"]= currentDateandTime
                    egress["date"]= currentDateandTime2
                    egress["total"]=total.text.toString().toDouble()
                    egress["user_nit"]= nit_user!!
                    if (it.result!!["cash"].toString().toDouble()>=total.text.toString().toDouble()){
                        val box=it.result!!["cash"].toString().toDouble()
                        db.collection("databases").document(database).update("cash",(box-total.text.toString().toDouble())).addOnSuccessListener {
                            db.collection("databases").document(database).collection("expenses")
                                .document(egress["date_hour"].toString()).set(egress).addOnCompleteListener {
                                    Toast.makeText(this,getString(R.string.egress_successfull), Toast.LENGTH_LONG).show()
                                    getLists()
                                    progressDialog.dismiss()
                                    dialog.dismiss()
                                }
                        }
                    }
                    else{
                        progressDialog.dismiss()
                        Toast.makeText(this,getString(R.string.egress_low), Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
        return dialog
    }
    fun createInfoDialg(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val nit_user=prefs!!.getString("email", "")
        val dialog: Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val v = inflater.inflate(R.layout.dialog_select_client, null)
        builder.setView(v)
        dialog=builder.create()
        val top= v.findViewById(R.id.top) as LinearLayout
        val recycler= v.findViewById(R.id.recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        top.visibility=View.GONE
        var lists = ArrayList<UserInfoClass>()
        recycler.adapter = AdapterUserInfo(lists, this)
        db.collection("users").whereEqualTo("type","employee").get().addOnCompleteListener {
            if (!it.result!!.isEmpty){
                var cash=0.0
                var expenses=0.0
                var credit=0.0
                for (document in it.result!!){
                    cash=0.0
                    expenses=0.0
                    credit=0.0
                    for (item in ListsCharges){
                        if (item.user_nit==document["nit"].toString()){
                            cash+=item.total
                        }
                    }
                    for (item in ListsExpenses){
                        if (item.user_nit==document["nit"].toString()){
                            if (item.loand.equals("null"))
                                expenses+= item.total!!
                                else
                                    credit+= item.total!!

                        }
                    }
                    lists.add(UserInfoClass(document["name"].toString(), cash, expenses, credit, cash-expenses))
                }
                recycler!!.adapter!!.notifyDataSetChanged()
            }
        }
        return dialog
    }
    fun createAddEntryDialog(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val nit_user=prefs!!.getString("email", "")

        val dialog: Dialog

        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater

        val v = inflater.inflate(R.layout.dialog_add_entry, null)

        builder.setView(v)
        dialog=builder.create()
        val description = v.findViewById(R.id.egress_description) as EditText
        val total = v.findViewById(R.id.total) as EditText
        val btnok = v.findViewById(R.id.pay) as Button
        val btncancel = v.findViewById(R.id.cancel) as Button

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Guardando...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

        })
        total.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnok.isEnabled = !(description.text.isNullOrEmpty()||total.text.isNullOrEmpty())

            }

        })
        btncancel.setOnClickListener {
            dialog.dismiss()
        }
        btnok.setOnClickListener {
            progressDialog.show()
            db.collection("databases").document(database!!).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                    val currentDateandTime = sdf.format(Calendar.getInstance().getTime())
                    val currentDateandTime2 = sdf2.format(Calendar.getInstance().getTime())
                    val egress = HashMap<String,Any>()
                    egress["user_nit"]= nit_user!!
                    egress["date_hour"]= currentDateandTime
                    egress["date"]= currentDateandTime2
                    egress["total"]=total.text.toString().toDouble()
                    egress["num"]=0
                    egress["due"]="0"
                    egress["client"]= "0"
                    egress["loand"]="0"
                    egress["description"]= description.text.toString()
                    val box=it.result!!["cash"].toString().toDouble()
                    db.collection("databases").document(database).update("cash",(box+total.text.toString().toDouble())).addOnSuccessListener {
                        db.collection("databases").document(database).collection("charges")
                            .document(egress["date_hour"].toString()).set(egress).addOnCompleteListener {
                                Toast.makeText(this,getString(R.string.entry_successfull),Toast.LENGTH_LONG).show()
                                getLists()
                                progressDialog.dismiss()
                                dialog.dismiss()
                            }
                    }



                }
            }
        }
        return dialog
    }
}
