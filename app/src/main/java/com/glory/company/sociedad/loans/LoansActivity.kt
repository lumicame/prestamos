package com.glory.company.sociedad.loans

import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import com.glory.company.sociedad.R
import com.glory.company.sociedad.RecyclerTouchListener
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.glory.company.sociedad.utils.ViewAnimation
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_loans.*
import kotlinx.android.synthetic.main.dialog_move_loand.*
import kotlinx.android.synthetic.main.recycler_view_route_dialog.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LoansActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var listLoan = ArrayList<loand>()
    var listsSearch = ArrayList<loand>()
    var route=""
    val storage = FirebaseStorage.getInstance()
    var indexList=0
    companion object{
        var listLoanOrder = ArrayList<loand>()
        var activity =LoansActivity()
        var update=false
        var cardID=""
        var card_delete="0"
        var recycle: RecyclerView? =null
        var add_index=-1
        var loan_add:loand?=null
    }
    fun calcular() {
        numdues=0
        numduespoidout=0
        capital=0
        ganancia=0.0
        estimado=0.0
        pending=0.0
        paidout=0.0
        numloands= 0
        for (loan in listLoanOrder){
            numloands++
            numdues += loan.due
            numduespoidout += loan.slopes
            capital += loan.capital
            val total = loan.total
            val g = loan.total - loan.capital
            paidout += loan.paidout
            pending += loan.total - loan.paidout
            ganancia += g
            estimado += total
        }
        try{
            num_dues_paidout.text=""+numduespoidout
            num_loands.text=""+numloands
            num_dues.text=""+numdues
            total_capital_initial.text="$"+nformat.format(capital)
            total_gain_initial.text="$"+nformat.format(ganancia)
            pending_.text="$"+nformat.format(pending)
            paidout_.text="$"+nformat.format(paidout)

        }catch (e:Exception){}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loans)
        back.setOnClickListener {
            onBackPressed()
        }
        recycle=recycler
        activity= this
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type= prefs.getString("type", "")
        route =intent.getStringExtra("route")!!
        var route_name = intent.getStringExtra("route_name")!!
        fab_add.setOnClickListener {
            val intent_= Intent(applicationContext, AddLoandActivity::class.java)
            intent_.putExtra("cash",true)
            intent_.putExtra("route",route)
            intent_.putExtra("route_name",route_name)
            intent_.putExtra("next","-1")
            startActivity(intent_)
        }
        recycle!!.layoutManager = LinearLayoutManager(this)
        recycle!!.setHasFixedSize(true)
        recycler_search.layoutManager = LinearLayoutManager(this)
        recycler_search.setHasFixedSize(true)
        recycler_search.adapter = LoandAdapter(listsSearch, this)
        //recycler.setItemViewCacheSize(20);
        //recycler.adapter!!.setHasStableIds(true)
        recycle!!.adapter = LoandAdapter(listLoanOrder, this)
        recycle!!.addOnItemTouchListener(RecyclerTouchListener(this,
            recycle!!,
            object : RecyclerTouchListener.ClickListener {
                override fun onClick(view: View, position: Int) {
                    val toggle = view.findViewById<ImageButton>(R.id.bt_toggle_items)
                    val container = view.findViewById<LinearLayout>(R.id.container)
                    val lty = view.findViewById<LinearLayout>(R.id.lyt_expand_items)


                    toggle.setOnClickListener(View.OnClickListener { view ->
                        toggleSection(
                            view,
                            lty
                        )
                    })
                }

                override fun onLongClick(view: View?, position: Int) {
                    val builder = AlertDialog.Builder(LoansActivity.activity!!)
                    builder.setTitle("Selecciona")
                    val animals = arrayOf("Mover Cobro", "Agregar cobro despues de este")
                    builder.setItems(animals) { dialog, which ->
                        when (which) {
                            0 -> {
                                createMoveCardDialg(listLoanOrder[position],position).show()
                            }
                            1 -> {
                                val intent= Intent(applicationContext, AddLoandActivity::class.java)
                                val item=listLoanOrder[position]
                                Log.e("INDEX","$position")
                                indexList=1
                                intent.putExtra("cash",true)
                                intent.putExtra("route",item.route)
                                intent.putExtra("route_name",item.name_route)
                                intent.putExtra("next",item.next_card)
                                intent.putExtra("previous",item.id)
                                intent.putExtra("position",position)
                                startActivity(intent)
                            }
                        }
                    }

                    val dialog = builder.create()
                    dialog.show()
                }

            }
        ))
        recycler_search!!.addOnItemTouchListener(RecyclerTouchListener(this,
            recycle!!,
            object : RecyclerTouchListener.ClickListener {
                override fun onClick(view: View, position: Int) {
                    val toggle = view.findViewById<ImageButton>(R.id.bt_toggle_items)
                    val lty = view.findViewById<LinearLayout>(R.id.lyt_expand_items)
                    toggle.setOnClickListener(View.OnClickListener { view ->
                        toggleSection(
                            view,
                            lty
                        )
                    })
                }

                override fun onLongClick(view: View?, position: Int) {
                    /*val builder = AlertDialog.Builder(LoansActivity.activity!!)
                    builder.setTitle("Selecciona")
                    val animals = arrayOf("Mover Cobro", "Agregar cobro despues de este")
                    builder.setItems(animals) { dialog, which ->
                        when (which) {
                            0 -> {

                            }
                            1 -> {*/
                    val intent= Intent(applicationContext, AddLoandActivity::class.java)
                    val item=listLoanOrder[position]
                    Log.e("INDEX","$position")
                    indexList=1
                    intent.putExtra("cash",true)
                    intent.putExtra("route",item.route)
                    intent.putExtra("route_name",item.name_route)
                    intent.putExtra("next",item.next_card)
                    intent.putExtra("previous",item.id)
                    startActivity(intent)
                    /*}
                }
            }

            val dialog = builder.create()
            dialog.show()*/
                }

            }
        ))

        if (route.isNullOrEmpty()){
            //Load()
        }else{
            LoadRoute()
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!search.text.isNullOrEmpty()){
                    var prefs = getSharedPreferences(PREFS_FILENAME, 0)
                    val type=prefs!!.getString("type", "")
                    if (listLoanOrder.size>0){
                        listsSearch.clear()
                        for (document in listLoanOrder){
                            if (document.client!!.name.toLowerCase().contains(search.text.toString().toLowerCase())
                                || document.id.toLowerCase().contains(search.text.toString().toLowerCase())
                                || document.client!!.nit.toLowerCase().contains(search.text.toString().toLowerCase())
                                || document.name_route.toLowerCase().contains(search.text.toString().toLowerCase())){
                                listsSearch.add(
                                    document
                                )
                            }
                        }
                        if (listsSearch.size > 0) {
                            try {
                                recycler_search.visibility=View.VISIBLE
                                recycle!!.visibility=View.GONE
                                recycler_search!!.adapter!!.notifyDataSetChanged()
                            } catch (e: Exception) {
                            }
                        } else {
                            try {
                                recycler_search.visibility=View.VISIBLE
                                recycle!!.visibility=View.GONE
                                recycler_search!!.adapter!!.notifyDataSetChanged()
                                Toast.makeText(applicationContext,getString(R.string.not_results),Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                            }

                        }


                    }
                }else{
                    recycler_search.visibility=View.GONE
                    recycle!!.visibility=View.VISIBLE
                }


            }

        })
    }

    val nformat = DecimalFormat("##,###,###.##")
    var numloands=0
    var numdues=0
    var numduespoidout=0
    var capital=0
    var ganancia=0.0
    var estimado=0.0
    var pending=0.0
    var paidout=0.0
    var move=false
    /*fun Load() {
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        listLoanOrder.clear()
        numloands=0
        numdues=0
        numduespoidout=0
        capital=0
        ganancia=0.0
        estimado=0.0
        pending=0.0
        paidout=0.0
        db.collection("databases").document(database!!).collection("loands")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                if (!querySnapshot!!.isEmpty ) {
                    for (document in querySnapshot.documentChanges) {
                        val loan=document.document.toObject(loand::class.java)
                        if (document.type==DocumentChange.Type.ADDED){
                            if (indexList!=0){
                                try {
                                    var ind=0
                                    for (l in listLoanOrder){
                                        if (l.id==loan.previous_card){
                                            listLoanOrder.add(ind+1,loan)
                                            numloands++
                                            numdues += loan.due
                                            numduespoidout += loan.slopes
                                            capital += loan.capital
                                            val total = loan.total
                                            val g = loan.total - loan.capital
                                            paidout += loan.paidout
                                            pending += loan.total - loan.paidout
                                            ganancia += g
                                            estimado += total

                                        }
                                        ind++
                                    }
                                    indexList=0
                                }catch (e:Exception){}
                            }
                            else{
                                listLoanOrder.add(loan)
                                numloands++
                                numdues += loan.due
                                numduespoidout += loan.slopes
                                capital += loan.capital
                                val total = loan.total
                                val g = loan.total - loan.capital
                                paidout += loan.paidout
                                pending += loan.total - loan.paidout
                                ganancia += g
                                estimado += total
                            }

                        }
                        if (document.type==DocumentChange.Type.MODIFIED){
                            if(!move){
                                for ((ind, l) in listLoanOrder.withIndex()){
                                    if (l.id==loan.id){
                                        listLoanOrder[ind] = loan
                                    }
                                }
                            }
                            else{

                            }

                        }

                    }

                    recycle!!.adapter!!.notifyDataSetChanged()


                }else{
                    Toast.makeText(this,getString(R.string.not_results),Toast.LENGTH_LONG).show()
                    recycle!!.adapter!!.notifyDataSetChanged()
                }
                try{
                    num_dues_paidout.text=""+numduespoidout
                    num_loands.text=""+numloands
                    num_dues.text=""+numdues
                    total_capital_initial.text="$"+nformat.format(capital)
                    total_gain_initial.text="$"+nformat.format(ganancia)
                    pending_.text="$"+nformat.format(pending)
                    paidout_.text="$"+nformat.format(paidout)

                }catch (e:Exception){}
            }

    }*/

    private fun toggleSection(bt: View, lyt: View) {
        val show = toggleArrow(bt)
        if (show) {
            ViewAnimation.expand(lyt, object : ViewAnimation.AnimListener {
                override fun onFinish() {
                    //Tools.nestedScrollTo(nested_scroll_view, lyt)
                }
            })
        } else {
            ViewAnimation.collapse(lyt)
        }
    }

    fun toggleArrow(view: View): Boolean {
        return if (view.rotation == 0f) {
            view.animate().setDuration(200).rotation(180f)
            true
        } else {
            view.animate().setDuration(200).rotation(0f)
            false
        }
    }

    var type_add=false
    fun LoadRoute() {
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        listLoan.clear()
        listLoanOrder.clear()
        numdues=0
        numduespoidout=0
        capital=0
        ganancia=0.0
        estimado=0.0
        pending=0.0
        paidout=0.0
        numloands= 0
        var next_card="0"
        if (type=="admin"){
            for (loan in AdminActivity.ListsLoans){
                if (route.equals(loan.route))
                listLoan.add(loan)
            }

        }
        else{
            for (loan in EmployeActivity.ListsLoans){
                if (route.equals(loan.route))
                listLoan.add(loan)
            }
        }
        if (listLoan.isNotEmpty()){
            for(num in 0..listLoan.size) {
                if (num==0){
                    for (loan in listLoan) {
                        if (loan.previous_card=="0"){
                            listLoanOrder.add(loan)
                            numloands++
                            numdues += loan.due
                            numduespoidout += loan.slopes
                            capital += loan.capital
                            val total = loan.total
                            val g = loan.total - loan.capital
                            paidout += loan.paidout
                            pending += loan.total - loan.paidout
                            ganancia += g
                            estimado += total
                            next_card=loan.next_card
                        }
                    }
                }
                else{
                    for (loan in listLoan) {
                        if (loan.id==next_card){
                            listLoanOrder.add(loan)
                            numloands++
                            numdues += loan.due
                            numduespoidout += loan.slopes
                            capital += loan.capital
                            val total = loan.total
                            val g = loan.total - loan.capital
                            paidout += loan.paidout
                            pending += loan.total - loan.paidout
                            ganancia += g
                            estimado += total
                            next_card=loan.next_card
                        }
                    }
                }
            }
        }
        recycle!!.adapter!!.notifyDataSetChanged()
        try{
            num_dues_paidout.text=""+numduespoidout
            num_loands.text=""+numloands
            num_dues.text=""+numdues
            total_capital_initial.text="$"+nformat.format(capital)
            total_gain_initial.text="$"+nformat.format(ganancia)
            pending_.text="$"+nformat.format(pending)
            paidout_.text="$"+nformat.format(paidout)

        }catch (e:Exception){}
      /*  db.collection("databases").document(database!!).collection("loands")
            .whereEqualTo("route",route)
            .get().addOnCompleteListener {

                if (!it!!.result!!.isEmpty) {
                    for (document in it.result!!) {
                        val loan=document.toObject(loand::class.java)
                                listLoan.add(loan)
                                //type_add=true
                    }
                    if (listLoan.isNotEmpty()){
                        for(num in 0..listLoan.size) {
                            if (num==0){
                                for (loan in listLoan) {
                                    if (loan.previous_card=="0"){
                                        listLoanOrder.add(loan)
                                        numloands++
                                        numdues += loan.due
                                        numduespoidout += loan.slopes
                                        capital += loan.capital
                                        val total = loan.total
                                        val g = loan.total - loan.capital
                                        paidout += loan.paidout
                                        pending += loan.total - loan.paidout
                                        ganancia += g
                                        estimado += total
                                        next_card=loan.next_card
                                    }
                                }
                            }
                            else{
                                for (loan in listLoan) {
                                    if (loan.id==next_card){
                                        listLoanOrder.add(loan)
                                        numloands++
                                        numdues += loan.due
                                        numduespoidout += loan.slopes
                                        capital += loan.capital
                                        val total = loan.total
                                        val g = loan.total - loan.capital
                                        paidout += loan.paidout
                                        pending += loan.total - loan.paidout
                                        ganancia += g
                                        estimado += total
                                        next_card=loan.next_card
                                    }
                                }
                            }
                        }
                    }
                    recycle!!.adapter!!.notifyDataSetChanged()
                    try{
                        num_dues_paidout.text=""+numduespoidout
                        num_loands.text=""+numloands
                        num_dues.text=""+numdues
                        total_capital_initial.text="$"+nformat.format(capital)
                        total_gain_initial.text="$"+nformat.format(ganancia)
                        pending_.text="$"+nformat.format(pending)
                        paidout_.text="$"+nformat.format(paidout)

                    }catch (e:Exception){}
                    /*try {
                        var ind=0
                        if (listLoanOrder.size>0){
                            for (l in listLoanOrder){
                                if (loan.previous_card==l.id){
                                    listLoanOrder.add((ind+1),loan)
                                    numloands++
                                    numdues += loan.due
                                    numduespoidout += loan.slopes
                                    capital += loan.capital
                                    val total = loan.total
                                    val g = loan.total - loan.capital
                                    paidout += loan.paidout
                                    pending += loan.total - loan.paidout
                                    ganancia += g
                                    estimado += total
                                }
                                ind++
                            }
                            type_add=false
                        }
                        else{
                            listLoanOrder.add(loan)
                            numloands++
                            numdues += loan.due
                            numduespoidout += loan.slopes
                            capital += loan.capital
                            val total = loan.total
                            val g = loan.total - loan.capital
                            paidout += loan.paidout
                            pending += loan.total - loan.paidout
                            ganancia += g
                            estimado += total
                            type_add=false
                        }


                    }catch (e:Exception){}*/

                }
            }*/


    }
    fun createMoveCardDialg(loan:loand,position:Int): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val dialog: Dialog
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val v = inflater.inflate(R.layout.dialog_move_loand, null)
        var loan_: loand? =null
        builder.setView(v)
        dialog=builder.create()
        val letra = ArrayList<String>()
        val avatar = v.findViewById(R.id.client_avatar) as CircleImageView
        val avatar_ = v.findViewById(R.id.client_avatar_) as CircleImageView
        val client_name=v.findViewById(R.id.client_name) as TextView
        val client_name_=v.findViewById(R.id.client_name_) as TextView
        val container_=v.findViewById(R.id.container_)as LinearLayout
        val client_address=v.findViewById(R.id.client_address) as TextView
        val client_address_=v.findViewById(R.id.client_address_) as TextView
        val num_count=v.findViewById(R.id.num_count) as TextView
        val num_count_=v.findViewById(R.id.num_count_) as TextView
        val btn=v.findViewById(R.id.Btn_save) as Button
        val check_down =v.findViewById(R.id.down) as RadioButton
        val check_up =v.findViewById(R.id.up) as RadioButton
        val spinner =v.findViewById(R.id.spinner) as Spinner
        client_name.text= loan.client!!.name
        client_address.text="#"+loan.id+ " - "+loan.client!!.address
        num_count.text=""+(position+1)
        for (l in listLoanOrder){
            if (l.id!=loan.id)
                letra.add(l.id)
        }
        spinner.adapter = ArrayAdapter(
            applicationContext,
            R.layout.spinner_item,
            letra
        )
        if (letra.isNullOrEmpty()) {
            btn.isEnabled=false
        } else {
            var i=0
            for (loan in listLoanOrder) {
                if (loan.id == letra.first()) {
                    loan_=loan
                    client_name_.text= loan_.client!!.name
                    client_address_.text="#"+loan_.id+ " - "+ loan_.client!!.address
                    num_count_.text=""+(i+1)
                }
                i++
            }
            btn.isEnabled=true
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
                    var i=0
                    for (loan in listLoanOrder) {
                        if (loan.id == letra[position]) {
                            loan_=loan
                            client_name_.text= loan_!!.client!!.name
                            client_address_.text="#"+ loan_!!.id+ " - "+loan_!!.client!!.address
                            num_count_.text=""+(i+1)
                        }
                        i++
                    }

                }


            }
        btn.setOnClickListener {
            if ((loan.id==loan_!!.next_card && !check_up.isChecked) || (loan.id==loan_!!.previous_card && check_up.isChecked)){
                Toast.makeText(this, "Quedara en la misma posicion",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.e("Actual", "#ID"+loan.id+" => "+position.toString() +" Siguiente => "+loan.next_card+" - Anterior => "+loan.previous_card )
            for ((i,l) in listLoanOrder.withIndex()) {
                if (l.id == loan.previous_card) {
                    l.next_card=loan.next_card
                    db.collection("databases").document(database!!).collection("loands")
                        .document(loan.previous_card).update("next_card", loan.next_card)
                        .addOnCompleteListener {
                        }
                    Log.e("#ID"+l.id, " Siguiente => "+loan.next_card )
                }
            }
            for ((i,l) in listLoanOrder.withIndex()) {
                if (l.id == loan.next_card) {
                    l.previous_card=loan.previous_card
                    db.collection("databases").document(database!!).collection("loands")
                        .document(loan.next_card).update("previous_card", loan.previous_card)
                        .addOnCompleteListener {
                        }
                    Log.e("#ID"+l.id, " Anterior => "+loan.previous_card )
                }
            }
            Log.e("Cambio", "#ID"+loan_!!.id+" => "+(num_count_.text.toString().toInt()-1).toString() +" Siguiente => "+loan_!!.next_card+" - Anterior => "+loan_!!.previous_card )
            if (check_up.isChecked){
                Log.e("Movimiento", "Mover arriba de."+" #ID"+loan_!!.id)
                for ((i,l) in listLoanOrder.withIndex()) {
                    if (l.id == loan_!!.previous_card) {
                        l.next_card=loan.id
                        db.collection("databases").document(database!!).collection("loands")
                            .document(l.id).update("next_card", loan.id)
                            .addOnCompleteListener {
                            }
                        Log.e("#ID"+l.id, " Siguiente => "+loan.id )
                    }
                }

                loan.next_card=loan_!!.id
                loan.previous_card=loan_!!.previous_card
                val map= HashMap<String,Any>()
                map["next_card"]=loan_!!.id
                map["previous_card"]=loan_!!.previous_card
                db.collection("databases").document(database!!).collection("loands")
                    .document(loan.id).update(map)
                    .addOnCompleteListener {
                    }
                Log.e("#ID"+loan.id, " Anterior => "+loan_!!.previous_card )
                Log.e("#ID"+loan.id, " Siguiente => "+loan_!!.id )
                Log.e("#ID"+loan_!!.id, " Anterior => "+loan!!.id )
                loan_!!.previous_card=loan.id
                db.collection("databases").document(database!!).collection("loands")
                    .document(loan_!!.id).update("previous_card",loan.id)
                    .addOnCompleteListener {
                    }
                listLoanOrder.removeAt(num_count.text.toString().toInt()-1)
                listLoanOrder.add((num_count_.text.toString().toInt()-1),loan)
            }
            else{
                Log.e("Movimiento", "Mover abajo de." +"#ID"+loan_!!.id)
                for ((i,l) in listLoanOrder.withIndex()) {
                    if (l.id == loan_!!.next_card) {
                        l.previous_card=loan.id
                        db.collection("databases").document(database!!).collection("loands")
                            .document(l.id).update("previous_card", loan.id)
                            .addOnCompleteListener {
                            }
                        //Log.e("#ID"+l.id, " Anterior => "+loan.id )
                    }
                }
                loan.next_card=loan_!!.next_card
                loan.previous_card=loan_!!.id
                val map= HashMap<String,Any>()
                map["next_card"]=loan_!!.next_card
                map["previous_card"]=loan_!!.id
                db.collection("databases").document(database!!).collection("loands")
                    .document(loan.id).update(map)
                    .addOnCompleteListener {
                        loan_!!.next_card=loan.id
                        db.collection("databases").document(database!!).collection("loands")
                            .document(loan_!!.id).update("next_card",loan.id)
                            .addOnCompleteListener {
                                LoadRoute()
                                dialog.dismiss()
                            }
                    }
                /*Log.e("#ID"+loan.id, " Anterior => "+loan_!!.id )
                Log.e("#ID"+loan.id, " Siguiente => "+loan_!!.next_card )
                Log.e("#ID"+loan_!!.id, " Siguiente => "+loan.id )*/


            }

            //recycle!!.adapter!!.notifyDataSetChanged()

        }

        return dialog
    }
    fun updatedRecyclerAdd(){
        LoadRoute()
        add_index=-1
        loan_add=null
    }


    override fun toString(): String {
        if (update){
            updatedRecyclerAdd()
            calcular()
            update=false
        }
        if (add_index!=-1){
            updatedRecyclerAdd()
            calcular()
        }
        return super<AppCompatActivity>.toString()
    }
}
