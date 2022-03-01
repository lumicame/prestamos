package com.glory.company.sociedad.loans

import android.Manifest
import android.R.attr.phoneNumber
import android.R.id.message
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Entry.AdapterExpense
import com.glory.company.sociedad.Entry.Egress
import com.glory.company.sociedad.R
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.glory.company.sociedad.trans.Trans
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_loand.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs


class ViewLoandActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    var db = FirebaseFirestore.getInstance()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var listcharges: ArrayList<Trans> = ArrayList()
    var listexpenses: ArrayList<Egress> = ArrayList()
    private var mGoogleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 255

    companion object {
        var recycler_due: RecyclerView? = null
        var Loan: loand? = null
        var lists_dues = ArrayList<duestate>()
    }


    fun enviaMensajeWhatsApp(tel: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enviar mensaje por whatsapp.")
        val flowers = arrayOf(
            "Detalles del credito",
            "Recordatorio de cuota",
            "Ultimo pago realizado"
        )
        var text=""
        builder.setSingleChoiceItems(
            flowers,  // Items list
            -1)  // Index of checked item (-1 = no selection)
        { dialogInterface, i ->  // Item click listener

            when(i){
                0->{
                    var next_date=""
                    if (Loan!!.dues!!.isNotEmpty()){
                        for (document in Loan!!.dues!!){
                            if (document!!.num== Loan!!.next)
                                next_date=document.date!!
                        }
                    }
                    text= "Hola ${Loan!!.client!!.name}." +
                            "\nEste es la información de tu credito (ID: ${Loan!!.id}): " +
                            "\nValor credito: $${nformat.format(Loan!!.capital)}"+
                            "\nTotal a pagar: $${nformat.format(Loan!!.total)}"+
                            "\nModo de pago: ${Loan!!.type}"+
                            "\nNumero de cuotas: ${Loan!!.due}"+
                            "\nValor de cuota: $${nformat.format(Loan!!.value_pay)}"+
                            "\nProximo pago: $next_date"+
                            "\nTotal pagado: $${nformat.format(Loan!!.paidout)}"+
                            "\nSaldo restante $${nformat.format(Loan!!.total- Loan!!.paidout)}"
                    //Log.e("MESSAGE",text)
                    //Toast.makeText(applicationContext,text,Toast.LENGTH_SHORT).show
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("http://api.whatsapp.com/send?phone=57$tel&text=$text")
                    startActivity(intent)
                    dialogInterface.dismiss()
                }
                1->{
                    var next_date=""
                    if (Loan!!.dues!!.isNotEmpty()){
                        for (document in Loan!!.dues!!){
                            if (document!!.num== Loan!!.next)
                                next_date=document.date!!
                        }
                        if (next_date==""){
                            Toast.makeText(applicationContext,"No hay pagos por cobrar",Toast.LENGTH_SHORT).show()
                            dialogInterface.dismiss()
                            return@setSingleChoiceItems
                        }
                        text= "Hola ${Loan!!.client!!.name}.\nTe recuerdo el pago de tu cuota por: $"+ nformat.format(Loan!!.value_pay)+" el dia $next_date"
                        Log.e("MESSAGE",text)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("http://api.whatsapp.com/send?phone=57$tel&text=$text")
                        startActivity(intent)
                        dialogInterface.dismiss()
                    }
                }
                2->{
                    if (listcharges.size<=0){
                        Toast.makeText(applicationContext,"No hay pagos realizados",Toast.LENGTH_SHORT).show()
                        dialogInterface.dismiss()
                        return@setSingleChoiceItems
                    }
                    var next_date=""
                    if (Loan!!.dues!!.isNotEmpty()){
                        for (document in Loan!!.dues!!){
                            if (document!!.num== Loan!!.next)
                                next_date=document.date!!
                        }
                        text= "Hola ${Loan!!.client!!.name}." +
                                "\nEste es la información sobre tu ultimo pago (ID:${Loan!!.id}): " +
                                "\nFecha pago: ${listcharges.last().date_hour}"+
                                "\nDescripción: ${listcharges.last().description}"+
                                "\nValor pagado: $${nformat.format(listcharges.last().total)}"+
                                "\nProximo pago: $next_date"+
                        "\nTotal pagado: $${nformat.format(Loan!!.paidout)}"+
                                "\nSaldo restante $${nformat.format(Loan!!.total- Loan!!.paidout)}"
                        Log.e("MESSAGE",text)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("http://api.whatsapp.com/send?phone=57$tel&text=$text")
                        startActivity(intent)
                        dialogInterface.dismiss()
                    }

                }
            }

        }
        val dialog = builder.create()
        dialog.show()

    }
     fun sendSMSMessage(tel:String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.SEND_SMS)
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.SEND_SMS),
                    2
                )
            }
        }else {
            val builder = AlertDialog.Builder(this)
            val phoneNo = tel
            builder.setTitle("Enviar mensaje por SMS.")
            val flowers = arrayOf(
                "Detalles del credito",
                "Recordatorio de cuota",
                "Ultimo pago realizado"
            )
            var text=""
            builder.setSingleChoiceItems(
                flowers,  // Items list
                -1)  // Index of checked item (-1 = no selection)
            { dialogInterface, i ->  // Item click listener


                when(i){
                    0->{
                        var next_date=""
                        if (Loan!!.dues!!.isNotEmpty()){
                            for (document in Loan!!.dues!!){
                                if (document!!.num== Loan!!.next)
                                    next_date=document.date!!
                            }
                            text= "Hola ${Loan!!.client!!.name}." +
                                    "\nEste es la información de tu credito (ID: ${Loan!!.id}): " +
                                    "\nValor credito: $${nformat.format(Loan!!.capital)}"+
                                    "\nTotal a pagar: $${nformat.format(Loan!!.total)}"+
                                    "\nModo de pago: ${Loan!!.type}"+
                                    "\nNumero de cuotas: ${Loan!!.due}"+
                                    "\nValor de cuota: $${nformat.format(Loan!!.value_pay)}"+
                                    "\nProximo pago: $next_date"+
                            "\nTotal pagado: $${nformat.format(Loan!!.paidout)}"+
                                    "\nSaldo restante $${nformat.format(Loan!!.total- Loan!!.paidout)}"
                            val smsManager: SmsManager = SmsManager.getDefault()
                            val parts = smsManager.divideMessage(text)
                            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null)
                            Snackbar.make(sms, "Mensaje de texto enviado", Snackbar.LENGTH_SHORT).show()
                            dialogInterface.dismiss()
                        }

                    }
                    1->{
                        var next_date=""
                        if (Loan!!.dues!!.isNotEmpty()){
                            for (document in Loan!!.dues!!){
                                if (document!!.num== Loan!!.next)
                                    next_date=document.date!!
                            }
                            if (next_date==""){
                                Toast.makeText(applicationContext,"No hay pagos por cobrar",Toast.LENGTH_SHORT).show()
                                dialogInterface.dismiss()
                                return@setSingleChoiceItems
                            }
                            text= "Hola ${Loan!!.client!!.name}.\nte recuerdo el pago de tu cuota de: $"+ nformat.format(Loan!!.value_pay)+" el dia "+next_date
                            val smsManager: SmsManager = SmsManager.getDefault()
                            val parts = smsManager.divideMessage(text)
                            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null)
                            Snackbar.make(sms, "Mensaje de texto enviado", Snackbar.LENGTH_SHORT).show()
                            dialogInterface.dismiss()
                        }
                    }
                    2->{
                        if (listcharges.size<=0){
                            Toast.makeText(applicationContext,"No hay pagos realizados",Toast.LENGTH_SHORT).show()
                            dialogInterface.dismiss()
                            return@setSingleChoiceItems
                        }
                        var next_date=""
                        if (Loan!!.dues!!.isNotEmpty()){
                            for (document in Loan!!.dues!!){
                                if (document!!.num== Loan!!.next)
                                    next_date=document.date!!
                            }
                            text= "Hola ${Loan!!.client!!.name}." +
                                    "\nEste es la información sobre tu ultimo pago (ID: ${Loan!!.id}): " +
                                    "\nFecha pago: ${listcharges.last().date_hour}"+
                                    "\nDescripción: ${listcharges.last().description}"+
                                    "\nValor pagado: $${nformat.format(listcharges.last().total)}"+
                                    "\nProximo pago: $next_date"+
                            "\nTotal pagado: $${nformat.format(Loan!!.paidout)}"+
                                    "\nSaldo restante $${nformat.format(Loan!!.total- Loan!!.paidout)}"
                            val smsManager: SmsManager = SmsManager.getDefault()
                            val parts = smsManager.divideMessage(text)
                            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null)
                            Snackbar.make(sms, "Mensaje de texto enviado", Snackbar.LENGTH_SHORT).show()
                            dialogInterface.dismiss()
                        }

                    }
                }

            }
            val dialog = builder.create()
            dialog.show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_loand)
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database = prefs!!.getString("database", "")
        val type = prefs!!.getString("type", "")
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        back.setOnClickListener {
            onBackPressed()
        }
        val id = intent.getStringExtra("id")


        recycler_products.layoutManager = LinearLayoutManager(this)
        recycler_products.hasFixedSize()
        recycler_due = findViewById(R.id.recycler_dues)
        recycler_due!!.layoutManager = LinearLayoutManager(this)
        recycler_due!!.hasFixedSize()
        recycler_charges.layoutManager = LinearLayoutManager(this)
        recycler_charges.hasFixedSize()
        recycler_expenses.layoutManager = LinearLayoutManager(this)
        recycler_expenses.hasFixedSize()
        db.collection("databases").document(database!!).collection("loands").document(id!!).addSnapshotListener{ documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (documentSnapshot!!.exists()) {
                    val item= documentSnapshot!!.toObject(loand::class.java)
                    Loan = item
                    Log.d("actualizar", Loan!!.id)
                    load()
            }else{
                db.collection("databases").document(database!!).collection("history").document(id!!).get().addOnCompleteListener {
                if (it.isSuccessful){
                    val item= it.result!!.toObject(loand::class.java)
                    Loan = item
                    //Log.d("actualizar", Loan!!.id)
                    loadhistory()
                }
                }
            }

        }
        db.collection("databases").document(database).collection("charges").whereEqualTo(
            "loand", id).addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            listcharges.clear()
            if (!querySnapshot!!.isEmpty) {
                for (document in querySnapshot!!) {
                    val trans = document.toObject(Trans::class.java)
                    listcharges.add(trans)
                }
                try {
                    recycler_charges.adapter = AdapterCharges(listcharges, this)
                } catch (e: Exception) {
                }
            }else{
                try {
                    recycler_charges.adapter = AdapterCharges(listcharges, this)
                } catch (e: Exception) {
                }
            }
        }
        db.collection("databases").document(database).collection("expenses").whereEqualTo(
            "loand", id).addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

            listexpenses.clear()
            if (!querySnapshot!!.isEmpty) {
                for (document in querySnapshot!!) {
                    listexpenses.add(
                        Egress(
                            document["name"].toString(),
                            document["date_hour"].toString(),
                            document["total"].toString().toDouble(),
                            document["user_nit"].toString(),
                            document["loand"].toString()
                        )
                    )
                }
                try {

                    recycler_expenses.adapter = AdapterExpense(listexpenses, this)
                } catch (e: Exception) {
                }
            }else{
                try {

                    recycler_expenses.adapter = AdapterExpense(listexpenses, this)
                } catch (e: Exception) {
                }
            }
        }
        client_ubication.setOnClickListener {
            createConfirmDialog().show()
        }
        Btn_finish.setOnClickListener {
            finish_loand()
        }
    }


    fun enable() {
        var count = 0
        for (item in lists_dues) {
            if (!item.state!!)
                count++
        }
        Btn_finish.isEnabled = count <= 0
    }
    fun finish_loand() {
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database = prefs!!.getString("database", "")
        val type = prefs!!.getString("type", "")
        val id = intent.getStringExtra("id")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        db.collection("databases").document(database!!)
            .collection("history").document(Loan!!.id).set(Loan!!).addOnCompleteListener {
                db.collection("databases").document(database!!).collection("loands")
                    .document(Loan!!.next_card).update("previous_card", Loan!!.previous_card)
                    .addOnCompleteListener {
                        db.collection("databases").document(database!!).collection("loands")
                            .document(Loan!!.previous_card).update("next_card", Loan!!.next_card)
                            .addOnCompleteListener {
                                db.collection("databases").document(database!!)
                                    .collection("loands").document(id!!).delete().addOnCompleteListener {
                                        progressDialog.dismiss()
                                            LoansActivity.update=true
                                            LoansActivity.toString()
                                            Toast.makeText(this, getString(R.string.finish_cardboard), Toast.LENGTH_LONG).show()
                                            finish()

                                    }
                            }
                    }


            }
    }


    fun load() {
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type = prefs!!.getString("type", "")
            content_products.visibility=View.GONE

        client_name.text = Loan!!.client!!.name
        client_nit.text = Loan!!.client!!.nit
        client_address.text = Loan!!.client!!.address
        client_tel.text = Loan!!.client!!.tel
        client_latitude.text = Loan!!.latitude
        client_longitude.text = Loan!!.longitude
        client_call.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL)
            i.data = Uri.parse("tel:" + Loan!!.client!!.tel)
            startActivity(i)
        }
        whapsapp.setOnClickListener {
            enviaMensajeWhatsApp(Loan!!.client!!.tel)
        }
        sms.setOnClickListener {
            sendSMSMessage(Loan!!.client!!.tel)
        }
        if (Loan!!.client!!.avatar != "")
           Picasso.get().load(Loan!!.client!!.avatar).placeholder(R.drawable.progress_animation ).into(client_avatar)



        val nformat = DecimalFormat("##,###,###.##")
        val database = prefs!!.getString("database", "")
        val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        val sdf = SimpleDateFormat("yyyy-M-d")
        val currentDateandTime = sdf.format(Calendar.getInstance().time)
        if (type == "client") {

            listcharges.clear()
            text_gain.visibility = View.GONE
            text_percentage.visibility = View.GONE
            Btn_finish.visibility = View.GONE
            client_call.visibility = View.GONE
            client_ubication.visibility = View.GONE
            whapsapp.visibility=View.GONE
            up_capital.visibility = View.GONE
            next_card.visibility = View.GONE
            previous_card.visibility = View.GONE
        }
        up_capital.setOnClickListener {
            createEditCapitalDialog().show()
        }
        up_interes.setOnClickListener {
            createAddInteresDialog().show()
        }
        if (Loan != null) {
            val loan = Loan
            consecutive.text = "#" + loan!!.id+" - "+loan.name_route
            client_date.text = loan!!.date
            client_total.text = "$" + nformat.format(loan!!.total)
            client_capital.text="$" + nformat.format(loan.capital)
            client_percentage.text = "%" + loan.percentage
            client_gain.text = "$" + nformat.format(loan.total - loan.capital)
            client_type.text = loan.type
            client_price_due.text = "$" + nformat.format(loan.value_pay)
            client_num_due.text = loan.due.toString()
            client_observation.text = loan.note
            previous_card.text = " " + getString(R.string.previous_cardboard) + " (${loan.previous_card})"
            next_card.text = " " + getString(R.string.next_cardboard) + " (${loan.next_card})"
            previous_card.setOnClickListener {
                if (loan.previous_card != "0") {
                    try {
                        val intent = Intent(this, ViewLoandActivity::class.java)
                        intent.putExtra("id", loan.previous_card)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                    }
                } else {
                    Toast.makeText(this, "No hay cartulina previa a esta.", Toast.LENGTH_SHORT).show()
                }
            }
            next_card.setOnClickListener {
                if (loan.next_card != "0") {
                    try {
                        val intent = Intent(this, ViewLoandActivity::class.java)
                        intent.putExtra("id", loan.next_card)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                    }
                } else {
                    Toast.makeText(this, "No hay cartulina siguiente a esta.", Toast.LENGTH_SHORT).show()
                }
            }

            if (loan.dues!!.isNotEmpty()) {
                enable()
                var count_due = 0
                var count_pay = 0
                var total_pay = 0
                var total_due = 0
                var previous = 0
                var dias: Long = 0

                for (document in loan.dues!!) {
                    var dat: Date? =null
                    if (document.date!!.contains("/"))
                        dat=sdf2.parse(document.date)
                    else
                        dat=sdf.parse(document.date)
                    val diff = sdf.parse(sdf.format(dat)).time - sdf.parse(currentDateandTime).time
                    if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime))) <= 0 && !document.state!!) {
                        dias = ((sdf.parse(sdf.format(dat)).time - sdf.parse(currentDateandTime).time) / 86400000)
                        if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime))) < 0)
                            previous++
                    }
                    if (document.state!!) {
                        count_pay++
                        total_pay += document.due!!
                    } else {
                        count_due++
                        total_due += document.due!!
                    }
                }
                if (previous > 0) {
                    if (dias <= 0)
                        client_state.text = getString(R.string.late) + " " + 1 + " " + getString(R.string.days)
                    else
                        client_state.text = getString(R.string.late) + " " + abs(dias) + " " + getString(R.string.days)
                } else
                    client_state.text = getString(R.string.up_to_date)
                client_paidout_due.text = "" + count_pay
                client_slope_due.text = "" + count_due
                client_total_paidout.text = "$" + nformat.format(loan.paidout)
                client_total_due.text = "$" + nformat.format(total_due)
            }


            loandues()

        }


    }
    fun loandues() {
        if (Loan!!.dues!!.isNotEmpty()) {
            lists_dues.clear()
            var indice = 0
            for (document in Loan!!.dues!!) {
                lists_dues.add(
                    duestate(
                        Loan!!.id,
                        indice.toString(),
                        document.num!!,
                        document.date!!,
                        document.due!!,
                        document.state
                    )
                )
                indice++
            }
            enable()
            recycler_due!!.adapter = DuesStateAdapter(lists_dues, this)
        }
    }
    fun loadhistory() {
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type = prefs!!.getString("type", "")


            content_products.visibility=View.GONE

        client_name.text = Loan!!.client!!.name
        client_nit.text = Loan!!.client!!.nit
        client_address.text = Loan!!.client!!.address
        client_tel.text = Loan!!.client!!.tel
        client_latitude.text = Loan!!.latitude
        client_longitude.text = Loan!!.longitude
        client_call.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL)
            i.data = Uri.parse("tel:" + Loan!!.client!!.tel)
            startActivity(i)
        }
        whapsapp.setOnClickListener {
            enviaMensajeWhatsApp(Loan!!.client!!.tel)
        }
        sms.setOnClickListener {
            sendSMSMessage(Loan!!.client!!.tel)
        }
        if (Loan!!.client!!.avatar == "default")
        else{
            storageRef.child(Loan!!.client!!.avatar).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it.toString()).placeholder(R.drawable.progress_animation ).into(client_avatar)
            }.addOnFailureListener {
                // Handle any errors
            }
        }

        val nformat = DecimalFormat("##,###,###.##")
        val database = prefs!!.getString("database", "")
        val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        val sdf = SimpleDateFormat("yyyy-M-d")
        val currentDateandTime = sdf.format(Calendar.getInstance().time)
        if (type == "client") {

            listcharges.clear()
            text_gain.visibility = View.GONE
            text_percentage.visibility = View.GONE
            Btn_finish.visibility = View.GONE
            client_call.visibility = View.GONE
            client_ubication.visibility = View.GONE
            whapsapp.visibility=View.GONE
            up_capital.visibility = View.GONE
            next_card.visibility = View.GONE
            previous_card.visibility = View.GONE
        }
        up_capital.visibility=View.GONE
        if (Loan != null) {
            val loan = Loan
            consecutive.text = "#" + loan!!.id+" - "+loan.name_route
            client_date.text = loan!!.date
            client_capital.text="$" + nformat.format(loan.capital)
            client_total.text = "$" + nformat.format(loan!!.total)
            client_percentage.text = "%" + loan.percentage
            client_gain.text = "$" + nformat.format(loan.total - loan.capital)
            client_type.text = loan.type
            client_price_due.text = "$" + nformat.format(loan.value_pay)
            client_num_due.text = loan.due.toString()
            client_observation.text = loan.note
            previous_card.visibility=View.GONE
            next_card.visibility=View.GONE
            Btn_finish.visibility=View.GONE
            if (loan.dues!!.isNotEmpty()) {
                enable()
                var count_due = 0
                var count_pay = 0
                var total_pay = 0
                var total_due = 0
                var previous = 0
                var dias: Long = 0

                for (document in loan.dues!!) {
                    var dat: Date? =null
                    if (document.date!!.contains("/"))
                        dat=sdf2.parse(document.date)
                    else
                        dat=sdf.parse(document.date)
                    val diff = sdf.parse(sdf.format(dat)).time - sdf.parse(currentDateandTime).time
                    if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime))) <= 0 && !document.state!!) {
                        dias = ((sdf.parse(sdf.format(dat)).time - sdf.parse(currentDateandTime).time) / 86400000)
                        if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime))) < 0)
                            previous++
                    }
                    if (document.state!!) {
                        count_pay++
                        total_pay += document.due!!
                    } else {
                        count_due++
                        total_due += document.due!!
                    }
                }
                if (previous > 0) {
                    if (dias <= 0)
                        client_state.text = getString(R.string.late) + " " + 1 + " " + getString(R.string.days)
                    else
                        client_state.text = getString(R.string.late) + " " + abs(dias) + " " + getString(R.string.days)
                } else
                    client_state.text = getString(R.string.up_to_date)
                client_paidout_due.text = "" + count_pay
                client_slope_due.text = "" + count_due
                client_total_paidout.text = "$" + nformat.format(loan.paidout)
                client_total_due.text = "$" + nformat.format(total_due)
            }

            loandues()

        }


    }

    override fun onStop() {
        super.onStop()
        mGoogleApiClient!!.disconnect()
    }
    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Aquí muestras confirmación explicativa al usuario
                // por si rechazó los permisos anteriormente
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION
                );
            }
        } else {
            try {
                var prefs = getSharedPreferences(PREFS_FILENAME, 0)
                val database = prefs!!.getString("database", "")
                var mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                val user = HashMap<String, Any>()
                user["latitude"] = mLastLocation.latitude.toString()
                user["longitude"] = mLastLocation.longitude.toString()
                client_longitude.text = mLastLocation.longitude.toString()
                client_latitude.text = mLastLocation.latitude.toString()
                db.collection("databases").document(database!!).collection("loands").document(Loan!!.id).update(user)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Ubicación Actualizada", Toast.LENGTH_LONG).show();
                    }
            }
            catch (e:Exception){}


        }
    }
    override fun onConnectionSuspended(p0: Int) {
    }
    override fun onConnectionFailed(p0: ConnectionResult) {
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    var prefs = getSharedPreferences(PREFS_FILENAME, 0)
                    val database = prefs!!.getString("database", "")
                    var mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                    val user = HashMap<String, Any>()
                    user["latitude"] = mLastLocation.latitude.toString()
                    user["longitude"] = mLastLocation.longitude.toString()
                    client_longitude.text = mLastLocation.longitude.toString()
                    client_latitude.text = mLastLocation.latitude.toString()
                    db.collection("databases").document(database!!).collection("loands").document(Loan!!.id).update(user)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Ubicación Actualizada", Toast.LENGTH_LONG).show();
                        }
                } catch (e: Exception) {
                }
            } else
                    Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show()

            }

    }
    override fun onDestroy() {
        super.onDestroy()
        LoansActivity.update=true
        LoansActivity.activity.toString()
    }
    fun createConfirmDialog(): AlertDialog {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Obteniendo...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Si estas en la casa del cliente puedes guardar sus coordenadas para luego acceder a ellas.")
            .setPositiveButton(
                "Ver Ubicación"
            ) { dialog, which ->
                val gmmIntentUri =
                    Uri.parse("geo:0,0?q=" + client_latitude.text + "," + client_longitude.text)
                val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                intent.setPackage("com.google.android.apps.maps")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }

            }
            .setNegativeButton(
                "Actualizar"
            ) { dialog, which ->
                progressDialog.show()
                mGoogleApiClient!!.connect()
                dialog.dismiss()
                progressDialog.dismiss()
                dialog.dismiss() }

        return builder.create()
    }

    //FUNCIONES PARA AGREGAR CAPITAL
    var lists = ArrayList<due>()
    var client_capital_ = 0
    var client_persentage_ = 0.0
    var client_total_ = 0
    var client_type_ = 0
    var client_dues_ = 0
    var client_date_ = ""
    var client_day_ = ""
    var client_interes=0
    var client_percentage_interer=0.0
    var client_pending_=0
    var loand_add_total_:TextView?=null
    var loand_add_month_:TextView?=null

    var recycler_: RecyclerView? = null
    val nformat = DecimalFormat("##,###,###.##")
    var domingo=0
    var count_domingo=0
    fun plusDay(date: Date, days: Int): Date {
        if (days == 0) return date
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, days)
        if (calendar.get(Calendar.DAY_OF_WEEK)==1){
            domingo=1
            count_domingo++
        }
        val string = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" +calendar.get(Calendar.YEAR)
        val format = SimpleDateFormat("dd/MM/yyyy")
        val f = format.parse(string)
        return f
    }
    fun daysBetween(d1: Date, d2: Date): Int {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt()
    }
    fun dues() {

        if (client_type_ == 0) {
            try {
                val string = client_date_
                var string_domingo=client_date_
                val format = SimpleDateFormat("dd/MM/yyyy")
                var date: Date = format.parse(string)
                var i = 0
                count_domingo=0
                lists.clear()
                var datefinal=""
                for (num in client_day_.toInt()..(client_dues_ * client_day_.toInt()) step client_day_.toInt()){
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
                //val difA = fin[Calendar.YEAR] - inicio[Calendar.YEAR]
                val difM = daysBetween(inicio.time,fin.time)/31
                loand_add_month_!!.text=""+(difM+1)
                Log.e("DIAS", ""+daysBetween(inicio.time,fin.time))
                val interes=((client_persentage_*(difM+1))/100).toFloat()
                client_percentage_interer= interes.toDouble()
                Log.e("INTERES", ""+interes)
                client_interes=(client_capital_ * interes).toInt()
                val total=client_capital_+(client_capital_ * interes)
                Log.e("Total", ""+total)
                client_total_= (total+client_pending_).toInt()
                Log.e("Cliente total", ""+client_total_)
                loand_add_total_!!.text ="$" +nformat.format(client_total_)
                val due=client_total_/lists.size
                for (l in lists)
                {
                    l.due=due
                }
                recycler_!!.adapter = DuesAdapter(lists, this)
            } catch (e: Exception) {
            }

        } else {
            try {
                val string = client_date_
                val format = SimpleDateFormat("dd/MM/yyyy")
                val format2 = SimpleDateFormat("d")
                var date: Date = format.parse(string)
                var i = 1
                var j = 0
                var days = 1
                lists.clear()
                val cadena = client_day_ + ","
                val fechas = cadena.split(",")
                if (!client_day_.isNullOrEmpty()) {
                    var datefinal=""
                    while (i <= client_dues_) {
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
                    //val difA = fin[Calendar.YEAR] - inicio[Calendar.YEAR]
                    val difM = daysBetween(inicio.time,fin.time)/31
                    loand_add_month_!!.text=""+(difM+1)
                    Log.e("DIAS", ""+daysBetween(inicio.time,fin.time))
                    val interes=((client_persentage_*(difM+1))/100).toFloat()
                    client_percentage_interer= interes.toDouble()
                    Log.e("INTERES", ""+interes)
                    client_interes=(client_capital_ * interes).toInt()
                    val total=client_capital_+(client_capital_ * interes)
                    Log.e("Total", ""+total)
                    client_total_= (total+client_pending_).toInt()
                    Log.e("Cliente total", ""+client_total_)
                    loand_add_total_!!.text ="$" +nformat.format(client_total_)
                    val due=client_total_/client_dues_
                    i=1
                    days=1
                    while (i <= client_dues_) {
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
                Log.d("INFO", lists.toString())
                recycler_!!.adapter = DuesAdapter(lists, this)
            } catch (e: Exception) {
            }
        }

    }

    fun createAddInteresDialog(): AlertDialog {

        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database = prefs!!.getString("database", "")
        val nit = prefs!!.getString("email", "")
        val type = prefs!!.getString("type", "")
        val dialog: Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.dialog_add_interes_extra, null)
        builder.setView(v)
        dialog = builder.create()
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pending = v.findViewById(R.id.loand_add_pending) as TextView
        val capital = v.findViewById(R.id.loand_add_capital) as EditText
        loand_add_total_ = v.findViewById(R.id.loand_add_total) as TextView
        loand_add_month_ = v.findViewById(R.id.loand_add_month) as TextView
        val add_type = v.findViewById(R.id.loand_add_type) as TextView
        val add_day = v.findViewById(R.id.loand_add_day) as EditText
        val add_dues = v.findViewById(R.id.loand_add_dues) as EditText
        val add_date = v.findViewById(R.id.loand_add_date) as TextView
        recycler_ = v.findViewById(R.id.recycler) as RecyclerView
        recycler_!!.layoutManager = LinearLayoutManager(this)
        recycler_!!.hasFixedSize()
        val btn = v.findViewById(R.id.Btn_addcash) as Button
        client_pending_ = Loan!!.total - Loan!!.paidout
        pending.text = "$" + nformat.format(client_pending_)
        capital.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    client_capital_=capital.text.toString().toInt()
                    client_persentage_=0.0
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()

                    dues()
                } catch (e: Exception) {
                }
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"
                        /*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_day.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()
                    dues()
                } catch (e: Exception) {
                }
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_dues.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()
                    dues()
                } catch (e: Exception) {
                }
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_date.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (add_dues.text.isNotEmpty()) {
                    client_dues_ = add_dues.text.toString().toInt()
                }
                client_date_ = add_date.text.toString()
                client_day_ = add_day.text.toString()
                dues()
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_type.setOnClickListener {
            // setup the alert builder
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.select_option))
// add a list
            val animals = arrayOf(getString(R.string.every_days), getString(R.string.every_date))
            builder.setItems(animals) { dialog, which ->
                when (which) {
                    0 -> {
                        client_type_ = 0
                        add_type.text = getString(R.string.every_days)
                        add_day.inputType = InputType.TYPE_CLASS_NUMBER
                        add_day.hint = getString(R.string.every_days_example)
                        add_day.setText("1")
                    }
                    1 -> {
                        client_type_ = 1
                        add_type.text = getString(R.string.every_date)
                        add_day.inputType = InputType.TYPE_CLASS_TEXT
                        add_day.hint = getString(R.string.every_date_example)
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()
        }
        add_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val startYear = calendar.get(Calendar.YEAR)
            val starthMonth = calendar.get(Calendar.MONTH)
            val startDay = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                    add_date.text = "" + day + "/" + (month + 1) + "/"+year
                }, startYear, starthMonth, startDay
            )
            datePickerDialog.show()
        }
        btn.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Espere")
            progressDialog.setMessage(getString(R.string.load))
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false)
            progressDialog.show()
            var prefs = getSharedPreferences(PREFS_FILENAME, 0)
            val database = prefs!!.getString("database", "")
            val t = prefs!!.getString("type", "")
            val user = prefs!!.getString("email", "")

            val loan = HashMap<String, Any>()
            val capital_sum=capital.text.toString().toInt() + Loan!!.capital
            val percentage_sum=((client_percentage_interer*100)+ Loan!!.percentage)/2
            loan["capital"] = capital_sum
            //loan["percentage"] = percentage_sum
            loan["total"] = Loan!!.total+capital.text.toString().toInt()+client_interes
            loan["due"] = lists.size
            loan["slopes"] = lists.size
            loan["date"] = add_date.text.toString()
            loan["state"] = false
            loan["next"] = 1
            loan["value_pay"]=client_total_/client_dues_
            if (client_type_ == 0) {
                loan["type"] = "Cada " + add_day.text.toString() + " dia(s)"
            } else {
                loan["type"] = "Todos los " + add_day.text.toString() + " de cada mes"
            }
            loan["dues"] = lists

            db.collection("databases").document(database!!).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.exists()) {

                                    db.collection("databases").document(database!!).collection("loands").document(
                                        Loan!!.id
                                    ).update(loan).addOnCompleteListener {

                                        /*val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                                        val currentDateandTime = sdf.format(Calendar.getInstance().getTime())
                                        val currentDateandTime2 = sdf2.format(Calendar.getInstance().getTime())
                                        val egress = java.util.HashMap<String, Any>()
                                        egress["name"] = "Interes agregado ${Loan!!.client!!.name}, Cartulina #${Loan!!.id}"
                                        egress["date_hour"]= currentDateandTime
                                        egress["date"]= currentDateandTime2
                                        egress["total"]=capital.text.toString().toInt()
                                        egress["user_nit"]= user!!
                                        egress["loand"]= Loan!!.id
                                        egress["interes"]=true*/
                                        Toast.makeText(
                                                    this,
                                                    getString(R.string.loand_succesful),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                LoansActivity.activity.toString()
                                                load()
                                                progressDialog.dismiss()
                                                dialog.dismiss()

                                    }


                    }
                }
            }


        }
        return dialog
    }
    fun createEditCapitalDialog(): AlertDialog {

        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database = prefs!!.getString("database", "")
        val nit = prefs!!.getString("email", "")
        val type = prefs!!.getString("type", "")
        val dialog: Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.dialog_add_cash_extra, null)
        builder.setView(v)
        dialog = builder.create()
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pending = v.findViewById(R.id.loand_add_pending) as TextView
        val capital = v.findViewById(R.id.loand_add_capital) as EditText
        val percentage = v.findViewById(R.id.loand_add_percentage) as EditText
        loand_add_total_ = v.findViewById(R.id.loand_add_total) as TextView
        loand_add_month_ = v.findViewById(R.id.loand_add_month) as TextView
        val add_type = v.findViewById(R.id.loand_add_type) as TextView
        val add_day = v.findViewById(R.id.loand_add_day) as EditText
        val add_dues = v.findViewById(R.id.loand_add_dues) as EditText
        val add_date = v.findViewById(R.id.loand_add_date) as TextView
        recycler_ = v.findViewById(R.id.recycler) as RecyclerView
        recycler_!!.layoutManager = LinearLayoutManager(this)
        recycler_!!.hasFixedSize()
        val btn = v.findViewById(R.id.Btn_addcash) as Button
        client_pending_ = Loan!!.total - Loan!!.paidout
        pending.text = "$" + nformat.format(client_pending_)
        capital.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    client_capital_=capital.text.toString().toInt()
                    client_persentage_=percentage.text.toString().toDouble()
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()

                    dues()
                } catch (e: Exception) {
                }
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                percentage.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"
                        /*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        percentage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    client_capital_=capital.text.toString().toInt()
                    client_persentage_=percentage.text.toString().toDouble()
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()
                    dues()
                } catch (e: Exception) {
                }

                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                percentage.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_day.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()
                    dues()
                } catch (e: Exception) {
                }
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                percentage.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_dues.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (add_dues.text.isNotEmpty()) {
                        client_dues_ = add_dues.text.toString().toInt()
                    }
                    client_date_ = add_date.text.toString()
                    client_day_ = add_day.text.toString()
                    dues()
                } catch (e: Exception) {
                }
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                percentage.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_date.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (add_dues.text.isNotEmpty()) {
                    client_dues_ = add_dues.text.toString().toInt()
                }
                client_date_ = add_date.text.toString()
                client_day_ = add_day.text.toString()
                dues()
                btn.isEnabled = !(
                        capital.text.isNullOrEmpty() ||
                                percentage.text.isNullOrEmpty() ||
                                loand_add_total_!!.text.isNullOrEmpty() ||
                                add_day.text.isNullOrEmpty() ||
                                add_dues.text.isNullOrEmpty() ||
                                add_date.text.isNullOrEmpty() ||
                                add_dues.text.toString() == "0" ||
                                add_day.text.toString() == "0"/*||spinner.selectedItem.toString().isNullOrEmpty()*/)

            }
        })
        add_type.setOnClickListener {
            // setup the alert builder
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.select_option))
// add a list
            val animals = arrayOf(getString(R.string.every_days), getString(R.string.every_date))
            builder.setItems(animals) { dialog, which ->
                when (which) {
                    0 -> {
                        client_type_ = 0
                        add_type.text = getString(R.string.every_days)
                        add_day.inputType = InputType.TYPE_CLASS_NUMBER
                        add_day.hint = getString(R.string.every_days_example)
                        add_day.setText("1")
                    }
                    1 -> {
                        client_type_ = 1
                        add_type.text = getString(R.string.every_date)
                        add_day.inputType = InputType.TYPE_CLASS_TEXT
                        add_day.hint = getString(R.string.every_date_example)
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()
        }
        add_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val startYear = calendar.get(Calendar.YEAR)
            val starthMonth = calendar.get(Calendar.MONTH)
            val startDay = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                    add_date.text = "" + day + "/" + (month + 1) + "/"+year
                }, startYear, starthMonth, startDay
            )
            datePickerDialog.show()
        }
        btn.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Espere")
            progressDialog.setMessage(getString(R.string.load))
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false)
            progressDialog.show()
            var prefs = getSharedPreferences(PREFS_FILENAME, 0)
            val database = prefs!!.getString("database", "")
            val t = prefs!!.getString("type", "")
            val user = prefs!!.getString("email", "")

            val loan = HashMap<String, Any>()
            val capital_sum=capital.text.toString().toInt() + Loan!!.capital
            val percentage_sum=((client_percentage_interer*100)+ Loan!!.percentage)/2
            loan["capital"] = capital_sum
            loan["percentage"] = percentage_sum
            loan["total"] = Loan!!.total+capital.text.toString().toInt()+client_interes
            loan["due"] = lists.size
            loan["slopes"] = lists.size
            loan["date"] = add_date.text.toString()
            loan["state"] = false
            loan["next"] = 1
            loan["value_pay"]=client_total_/client_dues_
            if (client_type_ == 0) {
                loan["type"] = "Cada " + add_day.text.toString() + " dia(s)"
            } else {
                loan["type"] = "Todos los " + add_day.text.toString() + " de cada mes"
            }
            loan["dues"] = lists

            db.collection("databases").document(database!!).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.exists()) {
                        if (it.result!!["cash"].toString().toDouble() >= capital.text.toString().toDouble()) {
                            db.collection("databases").document(database!!)
                                .update(
                                    "cash",
                                    (it.result!!["cash"].toString().toDouble() - capital.text.toString().toDouble())
                                ).addOnCompleteListener {
                                    db.collection("databases").document(database!!).collection("loands").document(
                                        Loan!!.id
                                    ).update(loan).addOnCompleteListener {

                                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                                        val currentDateandTime = sdf.format(Calendar.getInstance().getTime())
                                        val currentDateandTime2 = sdf2.format(Calendar.getInstance().getTime())
                                        val egress = java.util.HashMap<String, Any>()
                                        egress["name"] = "Dinero extra a ${Loan!!.client!!.name}, Cartulina #${Loan!!.id}"
                                        egress["date_hour"]= currentDateandTime
                                        egress["date"]= currentDateandTime2
                                        egress["total"]=capital.text.toString().toInt()
                                        egress["user_nit"]= user!!
                                        egress["loand"]= Loan!!.id
                                        db.collection("databases").document(database).collection("expenses")
                                            .document(egress["date_hour"].toString()).set(egress).addOnCompleteListener {
                                                Toast.makeText(
                                                    this,
                                                    getString(R.string.loand_succesful),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                LoansActivity.activity.toString()
                                                load()
                                                progressDialog.dismiss()
                                                dialog.dismiss()
                                            }
                                    }
                                }
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this, R.string.add_incorrect, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


        }
        return dialog
        }
    }

