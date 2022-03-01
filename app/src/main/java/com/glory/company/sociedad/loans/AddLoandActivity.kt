package com.glory.company.sociedad.loans

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.R
import com.glory.company.sociedad.RecyclerTouchListener
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_loand.*
import kotlinx.android.synthetic.main.activity_add_loand.back
import kotlinx.android.synthetic.main.activity_add_loand.recycler
import kotlinx.android.synthetic.main.activity_add_loand.search
import kotlinx.android.synthetic.main.activity_client.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddLoandActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var client_id:Long=0
    var client_nit=""
    var client_name=""
    var client_address=""
    var client_tel=""
    var client_avatar=""
    var client_capital=0
    var client_interes=0
    var client_persentage=0
    var client_total=0
    var client_type=0
    var client_route=""
    var client_route_name=""
    var lists = ArrayList<due>()
    var next_card="0"
    var previous_card="0"
    var id=0
    val nformat = DecimalFormat("##,###,###.##")
    var position=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loand)
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=intent.getBooleanExtra("cash", false)
        if (intent.getStringExtra("route")!=null)
        client_route= intent.getStringExtra("route")!!
        val database=prefs!!.getString("database", "")

        if (type){
            db.collection("databases").document(database!!).get().addOnCompleteListener {
                id = it.result!!["consecutive"].toString().toInt()
                loand_add_capital.isEnabled=true
                container_resume.visibility=View.GONE
                loand_add_consecutive.text=(id+1).toString()
                Btn_register.setOnClickListener {
                    saveCash()
                }
            }

        }else{
            recycler_p.layoutManager = LinearLayoutManager(this)
            recycler_p.hasFixedSize()

        }
        add_client.setOnClickListener {
            createAddClientDialogo().show()
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        back.setOnClickListener {
            onBackPressed()
        }
        loand_add_client.setOnClickListener {
           // createSelectClientDialog().show()
        }
        loand_add_client.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enable()
            }

        })
        loand_add_capital.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    // val total=loand_add_capital.text.toString().toInt()+(loand_add_capital.text.toString().toInt() * (loand_add_percentage.text.toString().toDouble()/100).toFloat())
                    //client_total=total.toInt()
                    //loand_add_total.text ="$" +nformat.format (client_total)
                    dues()
                } catch (e: Exception) {
                }
                enable()
            }

        })
        loand_add_percentage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    //val total=loand_add_capital.text.toString().toInt()+(loand_add_capital.text.toString().toInt() * (loand_add_percentage.text.toString().toDouble()/100).toFloat())
                    //client_total=total.toInt()
                    // loand_add_total.text ="$" +nformat.format (client_total)
                    dues()
                } catch (e: Exception) {
                }
                enable()
            }
        })
        loand_add_type.setOnClickListener {
            // setup the alert builder
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.select_option))
// add a list
            val animals = arrayOf(getString(R.string.every_days), getString(R.string.every_date))
            builder.setItems(animals) { dialog, which ->
                when (which) {
                    0 -> {
                        client_type = 0
                        loand_add_type.text = getString(R.string.every_days)
                        loand_add_day.inputType = InputType.TYPE_CLASS_NUMBER
                        loand_add_day.hint = getString(R.string.every_days_example)
                        loand_add_day.setText("1")
                        Log.d("type", "" + client_type)
                    }
                    1 -> {
                        client_type = 1
                        loand_add_type.text = getString(R.string.every_date)
                        loand_add_day.inputType = InputType.TYPE_CLASS_TEXT
                        loand_add_day.hint = getString(R.string.every_date_example)
                        Log.d("type", "" + client_type)
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()
        }
        loand_add_type.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dues()
                enable()
            }

        })
        loand_add_day.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dues()
                enable()
            }

        })
        loand_add_dues.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dues()
                enable()
            }

        })
        loand_add_date.setOnClickListener {
            val calendar=Calendar.getInstance()
            val startYear=calendar.get(Calendar.YEAR)
            val starthMonth=calendar.get(Calendar.MONTH)
            val startDay=calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog =  DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                    loand_add_date.text = "" + day + "/" + (month + 1) + "/" + year
                }, startYear, starthMonth, startDay
            )
            datePickerDialog.show()
        }
        loand_add_date.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dues()
                enable()
            }
        })

        search.setOnClickListener {
            //createSearchProductDialg().show()
        }
        if (client_route=="") {
            loand_add_route.setOnClickListener {
                //createSelectRouteDialog().show()
            }
            loand_add_route.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    enable()
                }

            })
        }else{
            if (intent.getStringExtra("next")!! =="-1"){
                val prefs = getSharedPreferences(PREFS_FILENAME, 0)
                val type_=prefs!!.getString("type", "")
                client_route_name=intent.getStringExtra("route_name")!!
                loand_add_route.text=client_route_name
                val letra = ArrayList<String>()
                    if (LoansActivity.listLoanOrder.isNotEmpty()) {
                        for (loan in LoansActivity.listLoanOrder) {
                                letra.add(loan.id)
                        }
                        spinner.adapter = ArrayAdapter(
                            applicationContext,
                            R.layout.spinner_item,
                            letra
                        )
                        if (letra.isNullOrEmpty()) {
                            next_card = "0"
                            previous_card = "0"
                            Log.e("LETRAS", next_card + " - " + previous_card)
                        } else {
                            for ((i,loan) in LoansActivity.listLoanOrder.withIndex()) {
                                if (loan.id == letra.last()) {
                                    next_card = loan.next_card
                                    previous_card = loan.id
                                    position=i
                                    Log.e("LETRAS", next_card + " - " + previous_card)
                                }
                            }
                        }

                    }
                    spinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position_: Int,
                                id: Long
                            ) {
                                if (letra[position_]==LoansActivity.listLoanOrder[position_].id){
                                    val loan =LoansActivity.listLoanOrder[position_]
                                    next_card = loan.next_card
                                    previous_card = loan.id
                                    position=position_
                                }

                                Log.e(
                                    "SPINNER",
                                    next_card + " - " + previous_card
                                )
                            }


                        }


            }
            else{
            val letra = ArrayList<String>()
            letra.add(intent.getStringExtra("previous")!!)
            spinner.adapter = ArrayAdapter(applicationContext, R.layout.spinner_item, letra)
            spinner.isEnabled=false
            next_card= intent.getStringExtra("next")!!
            previous_card=intent.getStringExtra("previous")!!
            client_route_name=intent.getStringExtra("route_name")!!
                position=intent.getIntExtra("position",0)!!
            loand_add_route.text=client_route_name
            enable()
            }
        }
       /* recycler_p.addOnItemTouchListener(
            RecyclerTouchListener(applicationContext, recycler_p,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val route = lists_p[position]
                        val mySnackbar = Snackbar.make(
                            loand_add_client,
                            getString(R.string.delete) + " " + route.name + " x " + route.quatity,
                            Snackbar.LENGTH_LONG
                        )
                        mySnackbar.setAction(R.string.delete, View.OnClickListener {
                            lists.removeAt(position)
                            //load()
                        })

                        mySnackbar.show()

                    }

                    override fun onLongClick(view: View?, position: Int) {
                        val route = lists_p[position]
                        //createAddQuantityDialg(route.id.toString(), position).show()
                        true
                    }
                })
        )*/
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelableArrayList("list", lists)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lists = savedInstanceState!!.getParcelableArrayList("list")!!
        recycler.adapter= DuesAdapter(lists, this)
        //load()
    }

       /* fun save(){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Espere")
            progressDialog.setMessage(getString(R.string.load))
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false)
            progressDialog.show()
            var prefs = getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            val t=prefs!!.getString("type", "")
            val user=prefs!!.getString("name", "")

            val loan = loand()
            loan.id = (id+1).toString()
            loan.client = User(client_id,client_name, client_nit, client_tel, client_address, client_avatar)
            loan.capital = loand_add_capital.text.toString().toInt()
            loan.percentage = loand_add_percentage.text.toString().toInt()
            loan.total = client_total
            loan.route = client_route
            loan.name_route = client_route_name
            loan.due = loand_add_dues.text.toString().toInt()
            loan.slopes = loand_add_dues.text.toString().toInt()
            loan.date = loand_add_date.text.toString()
            loan.state = false
            loan.next=1
            loan.paidout=0
            if (client_type==0){
                loan.type = "Cada "+loand_add_day.text.toString()+" dia(s)"
            }
            else{
                loan.type = "Todos los "+loand_add_day.text.toString()+" de cada mes"
            }
                loan.user = user!!
            if(loand_add_note.text.toString().isNullOrEmpty()){
                loan.note = "..."
            }
            else{
                loan.note = loand_add_note.text.toString()
            }
            loan.products=lists_p
            loan.dues=lists
            if (t=="admin"){
                db.collection("databases").document(database!!).collection("loands").document(loan.id).set(
                    loan
                ).addOnCompleteListener {
                    for (item in lists_p){
                            db.collection("databases").document(database!!).collection("products").document(
                                item.id!!
                            ).update("cellar", (item.cellar!! - item.quatity!!)).addOnCompleteListener {
                            }
                    }

                    Toast.makeText(this, getString(R.string.loand_succesful), Toast.LENGTH_LONG).show()
                    LoansActivity.activity.toString()
                    progressDialog.dismiss()
                    finish()
                }

            }
            else{
                db.collection("databases").document(database!!).collection("loands").document(loan.id).set(
                    loan
                ).addOnCompleteListener {
                    for (item in lists_p){
                        db.collection("databases").document(database!!).collection("products").document(
                            item.id!!
                        ).update("cellar", (item.cellar!! - item.quatity!!)).addOnCompleteListener {
                            }
                    }
                    Toast.makeText(this, getString(R.string.loand_succesful), Toast.LENGTH_LONG).show()
                    LoansActivity.activity.toString()
                    progressDialog.dismiss()
                    finish()
                }
            }

        }*/
    fun createAddClientDialogo(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val dialog:Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
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
           val container = v.findViewById(R.id.content_image) as RelativeLayout
container.visibility=View.GONE
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setMessage(getString(R.string.saving))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        db.collection("databases").document(database!!)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()){
                        val max=document["client"].toString().toInt()
                        db.collection("users").whereEqualTo("type","client")
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    title.text ="("+document!!.count()+ ") "+getString(R.string.title_add_client_num)+" $max"
                                    if (document!!.count()>=max){
                                        name.isEnabled=false
                                        tel.isEnabled=false
                                        addrres.isEnabled=false
                                        nit.isEnabled=false
                                        dialog.dismiss()
                                        Snackbar.make(fab_add, R.string.clients_max, Snackbar.LENGTH_LONG)
                                            .setAction("OK", null).show()
                                    }
                                }
                            }
                    }
                }
            }
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
            val client = HashMap<String, Any>()
            client["name"] = name.text.toString()
            client["tel"] = tel.text.toString()
            client["address"] = addrres.text.toString()
            client["nit"] = nit.text.toString()
            client["avatar"] = "default"
            client["type"] = "client"
            client["pass"] = tel.text.toString()
            client["points"] = 0
            client["dues"] = 0
            client["type"] = "client"
            client["latitude"] = "0"
            client["longitude"] = "0"
            client["database"] = database
            client["url"]=""
            client["id"] = db.collection("users").document().id
            db.collection("users").document(client["id"].toString()).set(client).addOnCompleteListener {
                        name.text.clear()
                        addrres.text.clear()
                        tel.text.clear()
                        nit.text.clear()
                client_id=client["id"].toString().toLong()
                client_nit = client["nit"].toString()
                client_name = client["name"].toString()
                client_address = client["address"].toString()
                client_avatar = client["url"].toString()
                client_tel = client["tel"].toString()
                loand_add_client.text = client["name"].toString() + " - " + client["nit"].toString()
                        progressDialog.dismiss()
                        dialog.dismiss()
                    }
            }
        return dialog
    }
    fun saveCash(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val user=prefs!!.getString("email", "")
        val loan =loand()
        loan.id = (id+1).toString()
        loan.user_id = client_id
        loan.capital = loand_add_capital.text.toString().toInt()
        loan.percentage = client_interes
        loan.total = client_total
        loan.value_pay=client_total/loand_add_dues.text.toString().toInt()
        loan.route = client_route
        loan.name_route = client_route_name
        loan.due = lists.size
        loan.slopes = lists.size
        loan.date = loand_add_date.text.toString()
        loan.state = false
        loan.next_card=next_card
        loan.previous_card=previous_card
        loan.next=1
        if (client_type==0){
            loan.type = "Cada "+loand_add_day.text.toString()+" dia(s)"
        }
        else{
            loan.type = "Todos los "+loand_add_day.text.toString()+" de cada mes"
        }
        loan.user = user!!
        if(loand_add_note.text.toString().isNullOrEmpty()){
            loan.note = "..."
        }
        else{
            loan.note = loand_add_note.text.toString()
        }

        loan.dues=lists
        db.collection("databases").document(database!!).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result!!.exists()){

                    if (it.result!!["cash"].toString().toDouble()>=loand_add_capital.text.toString().toDouble()){
                        db.collection("databases").document(database).collection("loands")
                            .document(next_card).update("previous_card", loan.id)
                            .addOnCompleteListener {
                                if (next_card!="0")
                            LoansActivity.listLoanOrder[position+1].previous_card=loan.id
                            }
                        db.collection("databases").document(database).collection("loands")
                            .document(previous_card).update("next_card", loan.id)
                            .addOnCompleteListener {
                                if (previous_card!="0")
                                    LoansActivity.listLoanOrder[position].next_card=loan.id
                            }
                        db.collection("databases").document(database)
                            .update(
                                "cash",
                                (it.result!!["cash"].toString()
                                    .toDouble() - loand_add_capital.text.toString().toDouble())
                            ).addOnCompleteListener {
                                db.collection("databases").document(database).collection("loands").document(
                                    loan.id
                                ).set(loan).addOnCompleteListener {


                                    db.collection("databases").document(database)
                                        .update("consecutive", loan.id)
                                        .addOnCompleteListener {
                                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                            val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                                            val currentDateandTime = sdf.format(
                                                Calendar.getInstance().getTime()
                                            )
                                            val currentDateandTime2 = sdf2.format(
                                                Calendar.getInstance().getTime()
                                            )
                                            val egress = HashMap<String, Any>()
                                            egress["name"] = "Dinero prestado a ${loan.name_route}, Cartulina #${loan.id}"
                                            egress["date_hour"]= currentDateandTime
                                            egress["date"]= currentDateandTime2
                                            egress["total"]=loan.capital
                                            egress["user_nit"]= user
                                            egress["loand"]= loan.id
                                            db.collection("databases").document(database).collection(
                                                "expenses"
                                            )
                                                .document(egress["date_hour"].toString()).set(egress).addOnCompleteListener {
                                                    Toast.makeText(
                                                        this,
                                                        getString(R.string.loand_succesful),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    progressDialog.dismiss()
                                                    LoansActivity.loan_add=loan
                                                    LoansActivity.add_index=position+1
                                                    LoansActivity.activity.toString();
                                                    finish()
                                                }

                                        }

                                }
                            }
                    } else{
                        progressDialog.dismiss()
                        Snackbar.make(back, R.string.add_incorrect, Snackbar.LENGTH_LONG)
                            .setAction("OK", null).show()
                    }
                }
            }
        }

    }
    var domingo=0
    var count_domingo=0

    fun plusDay(date: Date, days: Int):Date{
            if (days==0) return date
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DATE, days)
            if (calendar.get(Calendar.DAY_OF_WEEK)==1){
                domingo=1
                count_domingo++
            }

            val string = ""+calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(
                Calendar.YEAR
            )

            val format = SimpleDateFormat("dd/MM/yyyy")
            val f=format.parse(string)
            return f
        }
    fun enable(){
        Btn_register.isEnabled = !(loand_add_client.text.isNullOrEmpty()||loand_add_capital.text.isNullOrEmpty()||loand_add_percentage.text.isNullOrEmpty()||loand_add_day.text.isNullOrEmpty()||loand_add_dues.text.isNullOrEmpty()||loand_add_date.text.isNullOrEmpty()||client_route.toString().isNullOrEmpty())
    }
    fun daysBetween(d1: Date, d2: Date): Int {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt()
    }
    fun dues(){
        if (client_type==0){

            try{
                val string = loand_add_date.text.toString()
                val format = SimpleDateFormat("dd/MM/yyyy")
                var date = format.parse(string)
            var i=0
                var count=0
                count_domingo=0
            lists.clear()
            var datefinal=""
            for (num in loand_add_day.text.toString().toInt()..(loand_add_dues.text.toString().toInt()*loand_add_day.text.toString().toInt()) step loand_add_day.text.toString().toInt()){
                val f=plusDay(date!!, num+count_domingo)
                if (domingo==1){
                    domingo=0

                    val date_d=plusDay(date,num+count_domingo)
                    //if (loand_add_day.text.toString().toInt()>1||(loand_add_dues.text.toString().toInt()==1&&loand_add_day.text.toString().toInt()==1)){
                        i+=1
                        lists.add(due(i, "" + format.format(date_d), 0, false))
                    //}
                }
                else{
                    i+=1
                    lists.add(due(i, "" + format.format(f), 0, false))
                }
            }

                val inicio: Calendar = GregorianCalendar()
                val fin: Calendar = GregorianCalendar()
                inicio.time = SimpleDateFormat("dd/MM/yyyy").parse(format.format(date))
                fin.time = SimpleDateFormat("dd/MM/yyyy").parse(lists[lists.size-1].date)
                Log.e("DATE", format.format(date) + "" + datefinal)
                val difA = fin[Calendar.YEAR] - inicio[Calendar.YEAR]
                val difM = daysBetween(inicio.time,fin.time)/31
                Log.e("DIAS", ""+daysBetween(inicio.time,fin.time))
                loand_add_month!!.text=""+(difM+1)
                val interes=((loand_add_percentage.text.toString().toDouble()*(difM+1))/100).toFloat()
                client_interes=(interes*100).toInt()
                val total=loand_add_capital.text.toString().toInt()+(loand_add_capital.text.toString().toInt() * interes)
                client_total=total.toInt()
            loand_add_total.text ="$" +nformat.format(client_total)

            val due=client_total/lists.size
                for (l in lists)
                {
                    l.due=due
                }
            recycler.adapter = DuesAdapter(lists, this)
        }catch (e: Exception){}
        }
        else{
                try{

                    val string = loand_add_date.text.toString()
                    val format = SimpleDateFormat("dd/MM/yyyy")
                    val format2 = SimpleDateFormat("d")
                    var date:Date = format.parse(string)
                    var i=1
                    var j=0
                    var days=1
                    lists.clear()
                    val cadena=loand_add_day.text.toString()+","
                    val fechas = cadena.split(",")
                    if(!loand_add_day.text.toString().isNullOrEmpty()){
                        var datefinal=""
                        while (i<=loand_add_dues.text.toString().toInt()){
                            val f=plusDay(date, days)
                            for (palabra in fechas) {
                                if (format2.format(f) == palabra) {
                                    datefinal=format.format(f)
                                    i++
                                }

                            }
                            if (days>10000){
                                Snackbar.make(back, R.string.error_days, Snackbar.LENGTH_LONG)
                                    .setAction("OK", null).show()
                                return
                            }
                            days++
                        }
                        val inicio: Calendar = GregorianCalendar()
                        val fin: Calendar = GregorianCalendar()
                        inicio.time = SimpleDateFormat("dd/MM/yyyy").parse(format.format(date))
                        fin.time = SimpleDateFormat("dd/MM/yyyy").parse(datefinal)
                        Log.e("DATE", format.format(date) + "" + datefinal)
                        val difA = fin[Calendar.YEAR] - inicio[Calendar.YEAR]
                        val difM = daysBetween(inicio.time,fin.time)/31
                        Log.e("DIAS", ""+daysBetween(inicio.time,fin.time))
                        loand_add_month!!.text=""+(difM+1)
                        val interes=((loand_add_percentage.text.toString().toDouble()*(difM+1))/100).toFloat()
                        client_interes=(interes*100).toInt()
                        val total=loand_add_capital.text.toString().toInt()+(loand_add_capital.text.toString().toInt() * interes)
                        client_total=total.toInt()
                        loand_add_total.text ="$" +nformat.format(client_total)
                        val due=client_total/loand_add_dues.text.toString().toInt()
                         i=1
                         days=1
                    while (i<=loand_add_dues.text.toString().toInt()){
                        val f=plusDay(date, days)
                        for (palabra in fechas) {
                            if (format2.format(f) == palabra) {
                                lists.add(
                                    due(
                                        i, "" + format.format(f), due, false
                                    )
                                )
                                i++
                            }

                        }
                        if (days>10000){
                            Snackbar.make(back, R.string.error_days, Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show()
                           return
                        }
                        days++
                    }
                    }
                    recycler.adapter = DuesAdapter(lists, this)
                }catch (e: Exception){}
            }

    }
   /* fun createSelectClientDialog(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs!!.getString("type", "")
        val dialog: Dialog

        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater

        val v = inflater.inflate(R.layout.dialog_select_client, null)

        builder.setView(v)
        dialog=builder.create()

        val search = v.findViewById(R.id.search) as EditText
        val recycler = v.findViewById(R.id.recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
            var lists = ArrayList<User>()
            var listsSearch = ArrayList<User>()
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (type == "admin") {
                    lists.clear()
                    if (AdminActivity.ListsClient.isNotEmpty()) {
                        for (document in AdminActivity.ListsClient) {
                            if (document.name!!.toLowerCase().contains(
                                    search.text.toString().toLowerCase()
                                ) || document.nit!!.toLowerCase().contains(
                                    search.text.toString().toLowerCase()
                                )
                            ) {
                                lists.add(
                                    document
                                )
                            }
                        }
                        if (lists.isNotEmpty()) {
                            try {
                                recycler.adapter = AddLoandAdapter(lists, applicationContext)
                            } catch (e: Exception) {
                            }
                        } else {
                            try {
                                recycler.adapter = AddLoandAdapter(lists, applicationContext)
                            } catch (e: Exception) {
                            }
                        }
                        if (search.text.isNullOrEmpty()) {
                            lists.clear()
                            for (item in AdminActivity.ListsClient) {
                                lists.add(
                                    item
                                )
                            }
                            recycler.adapter = AddLoandAdapter(lists, applicationContext)
                        }
                    }
                } else {
                    lists.clear()
                    if (EmployeActivity.ListsClient.isNotEmpty()) {
                        for (document in EmployeActivity.ListsClient) {
                            if (document.name!!.toLowerCase().contains(
                                    search.text.toString().toLowerCase()
                                ) || document.nit!!.toLowerCase().contains(
                                    search.text.toString().toLowerCase()
                                )
                            ) {
                                lists.add(
                                    document
                                )
                            }
                        }
                        if (lists.isNotEmpty()) {
                            try {
                                recycler.adapter = AddLoandAdapter(lists, applicationContext)
                            } catch (e: Exception) {
                            }
                        }
                        if (search.text.isNullOrEmpty()) {
                            lists.clear()
                            for (item in EmployeActivity.ListsClient) {
                                lists.add(
                                    item
                                )
                            }
                            recycler.adapter = AddLoandAdapter(lists, applicationContext)
                        }
                    }
                }

            }

        })
            if (type=="admin"){
                lists.clear()
                for (item in AdminActivity.ListsClient){
                    lists.add(
                        item
                    )
                }
                recycler.adapter = AddLoandAdapter(lists, applicationContext)
            }
            else{
                lists.clear()
                for (item in EmployeActivity.ListsClient){
                    lists.add(
                        item
                    )
                }
                recycler.adapter = AddLoandAdapter(lists, applicationContext)
            }
        recycler.addOnItemTouchListener(
            RecyclerTouchListener(applicationContext, recycler,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val client = lists.get(position)
                        client_id=client.id!!
                        client_nit = client.nit!!
                        client_name = client.name!!
                        client_address = client.address!!
                        client_avatar = client.url!!
                        client_tel = client.tel!!
                        loand_add_client.text = client.name + " - " + client.nit
                        dialog.dismiss()
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        true
                    }
                })
        )

        return dialog
    }*/
    /*fun createSelectRouteDialog(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
            val type=prefs!!.getString("type", "")

            val dialog: Dialog

        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater

        val v = inflater.inflate(R.layout.dialog_select_route, null)

        builder.setView(v)
        dialog=builder.create()

        val recycler = v.findViewById(R.id.recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
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
            else{
                lists.clear()

                for (item in EmployeActivity.ListsRoute){
                    lists.add(
                        item
                    )
                }
                recycler.adapter = RouteAdapter(lists, applicationContext)
            }

        recycler.addOnItemTouchListener(
            RecyclerTouchListener(applicationContext, recycler,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val route = lists.get(position)
                        client_route = route.id
                        client_route_name = route.name
                        loand_add_route.text = route.name
                        val letra = ArrayList<String>()
                        if (type == "admin") {
                            if (AdminActivity.ListsLoans.isNotEmpty()) {
                                for (loan in AdminActivity.ListsLoans) {
                                    Log.d("LOANDS", loan.route + " - " + route.id)
                                    if (loan.route == route.id) {
                                        Log.d("ADD", loan.route + " - " + route.id)
                                        letra.add(loan.id)
                                    }
                                }
                                spinner.adapter = ArrayAdapter(
                                    applicationContext,
                                    R.layout.spinner_item,
                                    letra
                                )
                                if (letra.isNullOrEmpty()) {
                                    next_card = "0"
                                    previous_card = "0"
                                } else {
                                    for (loan in AdminActivity.ListsLoans) {
                                        if (loan.id == letra.first()) {
                                            next_card = loan.next_card
                                            previous_card = loan.id
                                            Log.d("LETRAS", next_card + " - " + previous_card)
                                        }
                                    }
                                }

                            }
                            spinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                    }

                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        for (loan in AdminActivity.ListsLoans) {
                                            if (loan.id == letra[position]) {
                                                next_card = loan.next_card
                                                previous_card = loan.id
                                            }
                                        }
                                        Log.d(
                                            "SPINNER",
                                            spinner.selectedItem.toString() + " - " + spinner.selectedItemId + " - " + spinner.selectedItemPosition
                                        )
                                    }


                                }

                        } else {
                            if (EmployeActivity.ListsLoans.isNotEmpty()) {
                                for (loan in EmployeActivity.ListsLoans) {
                                    Log.d("LOANDS", loan.route + " - " + route.id)
                                    if (loan.route == route.id) {
                                        Log.d("ADD", loan.route + " - " + route.id)
                                        letra.add(loan.id)
                                    }
                                }

                                spinner.adapter = ArrayAdapter(
                                    applicationContext,
                                    R.layout.spinner_item,
                                    letra
                                )
                                if (letra.isNullOrEmpty()) {
                                    next_card = "0"
                                    previous_card = "0"
                                } else {
                                    for (loan in EmployeActivity.ListsLoans) {
                                        if (loan.id == letra.first()) {
                                            next_card = loan.next_card
                                            previous_card = loan.id
                                            Log.d("LETRAS", next_card + " - " + previous_card)
                                        }
                                    }

                                }

                            }
                            spinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {

                                    }

                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        for (loan in EmployeActivity.ListsLoans) {
                                            if (loan.id == letra[position]) {
                                                next_card = loan.next_card
                                                previous_card = loan.id
                                            }
                                        }
                                        Log.d(
                                            "SPINNER",
                                            spinner.selectedItem.toString() + " - " + spinner.selectedItemId + " - " + spinner.selectedItemPosition
                                        )
                                    }


                                }
                        }
                        spinner.adapter = ArrayAdapter(
                            applicationContext,
                            R.layout.spinner_item,
                            letra
                        )
                        dialog.dismiss()
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        true
                    }
                })
        )

        return dialog
    }*/
   /* fun load(){
        val nformat = DecimalFormat("##,###,###.##")
        var num=0
        var total=0
        for(item in lists_p){
            num+=(1* item.quatity!!)
            total+=(item.quatity!! * item.sell!!)
        }
        total_products.text="$num"
        total_total.text="$"+nformat.format(total)
        loand_add_capital.setText("$total")
        recycler_p.adapter = MyAdapterSell(lists_p, this)
        enable()
    }*/
   /* fun createAddQuantityDialg(product: String, position: Int): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        val dialog: Dialog
        val nformat = DecimalFormat("##,###,###.##")
        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater

        val v = inflater.inflate(R.layout.dialog_product_number, null)

        builder.setView(v)
        dialog=builder.create()

        val nameproduct= v.findViewById(R.id.name_product) as TextView
        val priceproduct= v.findViewById(R.id.price_sell_product) as TextView
        val available= v.findViewById(R.id.available) as TextView
        val total= v.findViewById(R.id.total) as TextView
        val quantity = v.findViewById(R.id.add_product_quality) as EditText
        val subtract = v.findViewById(R.id.subtract) as ImageButton
        val plus = v.findViewById(R.id.plus) as ImageButton
        val btnok = v.findViewById(R.id.add_product_btn) as Button
        val btncancel = v.findViewById(R.id.cancel_product_btn) as Button
        var sell=0;
        if (type=="admin"){
            var ex=false
            for (item in AdminActivity.ListsStock){
                if (item.id==product){
                    ex=true
                    nameproduct.text= item.name
                    priceproduct.text= "$"+nformat.format(item.sell)
                    sell=item.sell
                    available.text= item.cellar.toString()
                    total.text= "$"+nformat.format((item.sell * quantity.text.toString().toInt()))
                }
            }
            if (!ex){
                Toast.makeText(this, getString(R.string.product_no_exist), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }
        else{
                var ex=false
                for (item in EmployeActivity.ListsStock){
                    if (item.id==product){
                        ex=true
                        nameproduct.text= item.name
                        priceproduct.text= "$"+nformat.format(item.sell)
                        sell=item.sell
                        available.text= item.cellar.toString()
                        total.text= "$"+nformat.format(
                            (item.sell * quantity.text.toString().toInt())
                        )
                    }
                }
                if (!ex){
                    Toast.makeText(this, getString(R.string.product_no_exist), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

            }
        subtract.setOnLongClickListener(View.OnLongClickListener {
            if (quantity.text.toString().toInt() > 12)
                quantity.setText("" + (quantity.text.toString().toInt() - 12))
            false
        })
        plus.setOnLongClickListener(View.OnLongClickListener {
            if (quantity.text.toString().toInt() < (available.text.toString().toInt() - 12))
                quantity.setText("" + (quantity.text.toString().toInt() + 12))
            false
        })
        subtract.setOnClickListener {
            if (quantity.text.toString().toInt()>1)
                quantity.setText("" + (quantity.text.toString().toInt() - 1))
        }
        plus.setOnClickListener {
            if (quantity.text.toString().toInt()<available.text.toString().toInt())
                quantity.setText("" + (quantity.text.toString().toInt() + 1))
        }
        quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnok.isEnabled = !(quantity.text.isNullOrEmpty())
                try {

                    total.text = "$" + nformat.format((sell * quantity.text.toString().toInt()))
                } catch (e: java.lang.Exception) {
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnok.isEnabled = !(quantity.text.isNullOrEmpty())
                try {

                    total.text = "$" + nformat.format((sell * quantity.text.toString().toInt()))
                } catch (e: java.lang.Exception) {
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnok.isEnabled = !(quantity.text.isNullOrEmpty())
                try {

                    total.text = "$" + nformat.format((sell * quantity.text.toString().toInt()))
                } catch (e: java.lang.Exception) {
                }
            }

        })

        btnok.setOnClickListener {
            lists_p[position].quatity=quantity.text.toString().toInt()
            load()
            dialog.dismiss()
        }
        btncancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        return dialog
    }*/
   /* fun createSearchProductDialg(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        val dialog: Dialog
        val nformat = DecimalFormat("##,###,###.##")
        val builder = AlertDialog.Builder(this)
        var lists2 = ArrayList<searchproduct>()
        var listsSearch = ArrayList<searchproduct>()
        val inflater = this.getLayoutInflater()

        val v = inflater.inflate(R.layout.dialg_search_product_sell, null)

        builder.setView(v)
        dialog=builder.create()

        val search_info= v.findViewById(R.id.info_search) as TextView
        val search= v.findViewById(R.id.search) as EditText

        val recycler = v.findViewById(R.id.recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        if (type=="admin"){
            lists2.clear()
            for (item in AdminActivity.ListsStock){
                lists2.add(
                    searchproduct(
                        item.name,
                        item.id
                    )
                )
            }
            try {
                    search_info.visibility=View.VISIBLE
            }catch (e: Exception){}
        }
        else{
            lists2.clear()
            for (item in EmployeActivity.ListsStock){
                lists2.add(
                    searchproduct(
                        item.name,
                        item.id
                    )
                )
            }
            try {
                    search_info.visibility=View.VISIBLE
            }catch (e: Exception){}
        }
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (lists2.isNotEmpty()) {
                    listsSearch.clear()
                    for (document in lists2) {
                        if (document.name.toLowerCase().contains(
                                search.text.toString().toLowerCase()
                            ) || document.id.toLowerCase().contains(
                                search.text.toString().toLowerCase()
                            )
                        ) {
                            listsSearch.add(
                                document
                            )
                        }
                    }
                    if (listsSearch.isNotEmpty()) {
                        search_info.visibility = View.GONE
                        try {
                            recycler.adapter = AdapterSearchProduct(listsSearch, applicationContext)
                        } catch (e: Exception) {
                        }
                    } else {
                        search_info.visibility = View.VISIBLE
                        try {
                            recycler.adapter = AdapterSearchProduct(listsSearch, applicationContext)
                        } catch (e: Exception) {
                        }
                    }
                    if (search.text.isNullOrEmpty()) {
                        listsSearch.clear()
                        if (listsSearch.isEmpty())
                            search_info.visibility = View.VISIBLE
                        else
                            search_info.visibility = View.GONE
                        try {
                            recycler.adapter = AdapterSearchProduct(listsSearch, applicationContext)
                        } catch (e: Exception) {
                        }
                    }
                }
            }

        })

        recycler.addOnItemTouchListener(
            RecyclerTouchListener(applicationContext, recycler,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        var product: searchproduct?
                        product = listsSearch[position]
                        db.collection("databases").document(database!!).collection("products")
                            .document(
                                product.id
                            ).get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    if (it.result!!.exists()) {
                                        var state = false
                                        for (item in lists_p) {
                                            if (item.id == it.result!!["id"].toString()) {
                                                if (item.quatity!! > it.result!!["cellar"].toString()
                                                        .toInt()
                                                ) {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        getString(R.string.only_available) + " " + it.result!!["cellar"].toString(),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    item.quatity =
                                                        it.result!!["cellar"].toString().toInt()
                                                } else {
                                                    item.quatity = item.quatity?.plus(1)
                                                }
                                                state = true
                                            }
                                        }
                                        if (!state && (it.result!!["cellar"].toString()
                                                .toInt() > 1)
                                        ) {
                                            lists_p.add(
                                                productsell(
                                                    product.id,
                                                    it.result!!["name"].toString(),
                                                    it.result!!["sell"].toString().toInt(),
                                                    1,
                                                    it.result!!["cellar"].toString().toInt()
                                                )
                                            )
                                        } else
                                            Toast.makeText(
                                                applicationContext,
                                                getString(R.string.not_available),
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        dialog.dismiss()
                                        load()
                                    } else {
                                        dialog.dismiss()
                                        load()
                                    }
                                }

                            }
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        true
                    }
                })
        )
        return dialog
    }*/
}
