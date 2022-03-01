package com.glory.company.sociedad.home.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Entry.Egress
import com.glory.company.sociedad.R
import com.glory.company.sociedad.RecyclerTouchListener
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.glory.company.sociedad.home.SlopesAdapter
import com.glory.company.sociedad.home.slopes
import com.glory.company.sociedad.loans.loand
import com.glory.company.sociedad.trans.Trans
import com.glory.company.sociedad.utils.ViewAnimation
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.content_admin_content.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class HomeFragment : Fragment() {
    var db = FirebaseFirestore.getInstance()
    var ListsCharges= java.util.ArrayList<Trans>()
    var ListsExpenses= java.util.ArrayList<Egress>()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val nformat = DecimalFormat("##,###,###.##")
    var recycler_slopes_today:RecyclerView?=null
    var recycler_slopes_previous:RecyclerView?=null
    var listsLoan_today = ArrayList<slopes>()
    var listsLoan_previous = ArrayList<slopes>()
    var listsSearch = ArrayList<slopes>()
    var cash:TextView?=null
    var caja:CardView?=null
    var egress:TextView?=null
    var search:EditText?=null
    var credit:TextView?=null
    var info_slopes:TextView?=null
    var box_cash:TextView?=null
    var btn_today:TextView?=null
    var btn_previous:TextView?=null
    var text_today:TextView?=null
    var text_previous:TextView?=null
    var box_init:TextView?=null
    var box_balance:TextView?=null
    var today=true
    private val nested_scroll_view: NestedScrollView? = null
    @SuppressLint("UnsafeExperimentalUsageError")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view=inflater.inflate(R.layout.content_admin_content, null)
        var prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs!!.getString("type", "")
        recycler_slopes_today=view.findViewById(R.id.recycler_slopes_today)
        recycler_slopes_previous=view.findViewById(R.id.recycler_slopes_previous)
        cash=view.findViewById(R.id.cash)
        search=view.findViewById(R.id.search)
        egress=view.findViewById(R.id.egress)
        btn_today=view.findViewById(R.id.btn_today)
        btn_previous=view.findViewById(R.id.btn_previous)
        text_today=view.findViewById(R.id.text_today)
        text_previous=view.findViewById(R.id.text_previous)
        //info_slopes=view.findViewById(R.id.info_slopes)
        box_init=view.findViewById(R.id.box_init)
        box_balance=view.findViewById(R.id.box_balance)
        credit=view.findViewById(R.id.credit)
        caja=view.findViewById(R.id.caja)
        box_cash=view.findViewById(R.id.box_cash)
        recycler_slopes_today!!.layoutManager = LinearLayoutManager(context)
        recycler_slopes_today!!.hasFixedSize()
        recycler_slopes_today!!.adapter=SlopesAdapter(listsLoan_today, context!!)
        recycler_slopes_previous!!.layoutManager = LinearLayoutManager(context)
        recycler_slopes_previous!!.hasFixedSize()
        recycler_slopes_previous!!.adapter=SlopesAdapter(listsLoan_previous, context!!)
        btn_today!!.setOnClickListener {
            if (!today){
                today=true
                btn_today!!.setBackgroundResource(R.drawable.buttonok)
                btn_previous!!.setBackgroundResource(R.drawable.gray)
                recycler_slopes_today!!.visibility=View.VISIBLE
                recycler_slopes_previous!!.visibility=View.GONE
                recycler_slopes_today!!.adapter=SlopesAdapter(listsLoan_today, context!!)
                recycler_slopes_today!!.adapter!!.notifyDataSetChanged()
            }
        }
        btn_previous!!.setOnClickListener {
            if (today){
                today=false
                btn_today!!.setBackgroundResource(R.drawable.gray)
                btn_previous!!.setBackgroundResource(R.drawable.buttonok)
                recycler_slopes_today!!.visibility=View.GONE
                recycler_slopes_previous!!.visibility=View.VISIBLE
                recycler_slopes_previous!!.adapter=SlopesAdapter(listsLoan_previous, context!!)
                recycler_slopes_previous!!.adapter!!.notifyDataSetChanged()
            }
        }
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                if (type=="admin"){
                   if (AdminActivity.reload_home){
                       try {
                           slopes()
                           LoadBox()
                       }catch (e:Exception){}
                       AdminActivity.reload_home=false
                   }
                }
                else{
                    if (EmployeActivity.reload_home){
                        try{
                        slopes()
                        LoadBoxEmployee()
                        }catch (e:Exception){}
                        EmployeActivity.reload_home=false
                    }
                    }
                mainHandler.postDelayed(this, 1000)
            }
        })
        if (type=="admin"){
            LoadToday()
            LoadBox()
        }else{
            caja!!.visibility=View.GONE
            LoadBoxEmployee()
        }
        slopes()

        search!!.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!search!!.text.isNullOrEmpty()){
                    if (today){
                        if (listsLoan_today.size>0){
                            listsSearch.clear()
                            for (document in listsLoan_today){
                                if (document.client!!.name.toLowerCase().contains(search!!.text.toString().toLowerCase())
                                    || document.id.toLowerCase().contains(search!!.text.toString().toLowerCase())
                                    || document.client!!.nit.toLowerCase().contains(search!!.text.toString().toLowerCase())){
                                    listsSearch.add(
                                        document
                                    )
                                }
                            }
                            if (listsSearch.size > 0) {
                                try {
                                    recycler_slopes_today!!.adapter=SlopesAdapter(listsSearch, context!!)
                                    recycler_slopes_today!!.adapter!!.notifyDataSetChanged()
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                    else{
                        if (listsLoan_previous.size>0){
                            listsSearch.clear()
                            for (document in listsLoan_previous){
                                if (document.client!!.name.toLowerCase().contains(search!!.text.toString().toLowerCase())
                                    || document.id.toLowerCase().contains(search!!.text.toString().toLowerCase())
                                    || document.client!!.nit.toLowerCase().contains(search!!.text.toString().toLowerCase())){
                                    listsSearch.add(
                                        document
                                    )
                                }
                            }
                            if (listsSearch.size > 0) {
                                try {
                                    recycler_slopes_previous!!.adapter=SlopesAdapter(listsSearch, context!!)
                                    recycler_slopes_previous!!.adapter!!.notifyDataSetChanged()
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }

                }else{
                    if (today){

                        recycler_slopes_today!!.adapter=SlopesAdapter(listsLoan_today, context!!)
                        recycler_slopes_today!!.adapter!!.notifyDataSetChanged()
                    }
                    else{

                        recycler_slopes_previous!!.adapter=SlopesAdapter(listsLoan_previous, context!!)
                        recycler_slopes_previous!!.adapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        recycler_slopes_today!!.addOnItemTouchListener(RecyclerTouchListener(context!!,
            recycler_slopes_today!!,
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

                }

            }
        ))
        recycler_slopes_previous!!.addOnItemTouchListener(RecyclerTouchListener(context!!,
            recycler_slopes_previous!!,
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

                }

            }
        ))
        return view
    }
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
    fun LoadBox(){
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "FALSE")
        val nit=prefs!!.getString("email", "FALSE")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())
        var cash_=0.0
        var egres=0.0
        var credit_=0.0
        ListsCharges.clear()
        ListsExpenses.clear()

        if (AdminActivity.ListsCharges.size>0){
            cash_=0.0
            for (document in AdminActivity.ListsCharges){
                cash_+=document.total
            }
        }
        if (AdminActivity.ListsExpenses.size>0){
            egres=0.0
            for (document in AdminActivity.ListsExpenses){
                if (document.loand.equals("null")) egres+= document.total!!
                else credit_+= document.total!!
            }
        }
        egress!!.text="$"+nformat.format(egres)
        credit!!.text="$"+nformat.format(credit_)
        cash!!.text="$"+nformat.format(cash_)
    }
    fun LoadBoxEmployee(){
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "FALSE")
        val nit=prefs!!.getString("email", "FALSE")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())
        var cash_=0.0
        var egres=0.0
        var credit_=0.0

        if (EmployeActivity.ListsCharges.size>0){
            cash_=0.0
            for (document in EmployeActivity.ListsCharges){
                cash_+=document.total
            }
        }
        if (EmployeActivity.ListsExpenses.size>0){
            egres=0.0
            for (document in EmployeActivity.ListsExpenses){
                if (document.loand.equals("null")) egres+= document.total!!
                else credit_+= document.total!!
            }
        }
        egress!!.text="$"+nformat.format(egres)
        credit!!.text="$"+nformat.format(credit_)
        cash!!.text="$"+nformat.format(cash_)
    }
    fun slopes(){
        var prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        var lists2 = ArrayList<slopes>()
        val sdf = SimpleDateFormat("yyyy-M-d")
        val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        val currentDateandTime = sdf.format(Calendar.getInstance().time)
       // recycler_slopes!!.adapter = SlopesAdapter(lists2, context!!)
        lists2.clear()

        if (type=="admin"){
            if (AdminActivity.ListsLoans.isNotEmpty()){
                listsLoan_today.clear()
                listsLoan_previous.clear()
                for (loan in AdminActivity.ListsLoans){
                    Log.e("ADDED",loan.id)
                    var previous=0
                    var today=false
                    for (d in loan.dues!!){
                        var dat: Date? =null
                        if (d.date!!.contains("/"))
                            dat=sdf2.parse(d.date)
                        else
                            dat=sdf.parse(d.date)
                        if((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<=0 && !d.state!!){
                            if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<0)
                                previous++
                            if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))==0)
                                today=true
                        }
                    }
                    if (previous>0){
                        listsLoan_previous.add( slopes(
                            loan.id,
                            loan.slopes,
                            loan.route,
                            loan.name_route,
                            previous,
                            (loan.value_pay),
                            loan.client!!))
                    }else{
                        if (today){
                        listsLoan_today.add( slopes(
                            loan.id,
                            loan.slopes,
                            loan.route,
                            loan.name_route,
                            previous,
                            (loan.value_pay),
                            loan.client!!))
                    }

                    }

                }
                    text_today!!.text=""+listsLoan_today.size
                    recycler_slopes_today!!.adapter!!.notifyDataSetChanged()
                    text_previous!!.text=""+listsLoan_previous.size
                    recycler_slopes_previous!!.adapter!!.notifyDataSetChanged()


            }
        }
        else{
            if (EmployeActivity.ListsLoans.isNotEmpty()){
                listsLoan_today.clear()
                listsLoan_previous.clear()
                for (loan in EmployeActivity.ListsLoans){
                    Log.e("ADDED",loan.id)
                    var previous=0
                    var today=false
                    for (d in loan.dues!!){
                        var dat: Date? =null
                        if (d.date!!.contains("/"))
                            dat=sdf2.parse(d.date)
                        else
                            dat=sdf.parse(d.date)
                        if((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<=0 && !d.state!!){
                            if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<0)
                                previous++
                            if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))==0)
                                today=true
                        }
                    }
                    if (previous>0){
                        listsLoan_previous.add( slopes(
                            loan.id,
                            loan.slopes,
                            loan.route,
                            loan.name_route,
                            previous,
                            (loan.value_pay),
                            loan.client!!))
                    }else{
                        if (today){
                            listsLoan_today.add( slopes(
                                loan.id,
                                loan.slopes,
                                loan.route,
                                loan.name_route,
                                previous,
                                (loan.value_pay),
                                loan.client!!))
                        }

                    }

                }
                text_today!!.text=""+listsLoan_today.size
                recycler_slopes_today!!.adapter!!.notifyDataSetChanged()
                text_previous!!.text=""+listsLoan_previous.size
                recycler_slopes_previous!!.adapter!!.notifyDataSetChanged()


            }
        }
        /*db.collection("databases").document(database!!).collection("loands")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

                if (!querySnapshot!!.isEmpty ) {
                    listsLoan.clear()
                    for (document in querySnapshot!!.documentChanges) {
                        val loan=document.document.toObject(loand::class.java)

                        if (document.type== DocumentChange.Type.ADDED){
                            listsLoan.add(slopes(
                            loan.id,
                            loan.slopes,
                            loan.route,
                            loan.name_route,
                            10000,
                            (loan.total / loan.due),
                            loan.client!!))
                            Log.e("ADDED",loan.id)
                            var previous=0
                            var today=false
                            for (d in loan.dues!!){
                                var dat: Date? =null
                                if (d.date!!.contains("/"))
                                 dat=sdf2.parse(d.date)
                                else
                                    dat=sdf.parse(d.date)
                                if((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<=0 && !d.state!!){
                                    if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<0)
                                        previous++
                                    if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))==0)
                                        today=true
                                }
                            }
                            if (previous>0 || today){
                                lists2.add( slopes(
                                    loan.id,
                                    loan.slopes,
                                    loan.route,
                                    loan.name_route,
                                    previous,
                                    (loan.total / loan.due),
                                    loan.client!!))
                            }
                        }
                        if (document.type== DocumentChange.Type.MODIFIED){
                            var previous=0
                            var today=false

                            for (d in loan.dues!!){
                                var dat: Date? =null
                                if (d.date!!.contains("/"))
                                    dat=sdf2.parse(d.date)
                                else
                                    dat=sdf.parse(d.date)
                                if((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<=0 && !d.state!!){
                                    if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))<0)
                                        previous++
                                    if ((sdf.parse(sdf.format(dat)).compareTo(sdf.parse(currentDateandTime)))==0)
                                        today=true
                                }
                            }
                            if (previous>0 || today){
                                try{
                                    var i=0
                                for (s in lists2){
                                    if (s.id==loan.id){
                                        s.client= loan.client!!
                                        s.pass=previous
                                        s.due= (loan.total / loan.due)
                                        Log.e("MODDIFI",s.id+ " "+s.client!!.name)
                                        i++
                                    }
                                }
                              if(i==0){
                                  lists2.add( slopes(
                                      loan.id,
                                      loan.slopes,
                                      loan.route,
                                      loan.name_route,
                                      previous,
                                      (loan.total / loan.due),
                                      loan.client!!))
                              }
                                }catch (e:java.lang.Exception){}
                            }else{
                                var ind=0
                                try{
                                    if (lists2.size>0){
                                        for (s in lists2){
                                            if (s.id==loan.id){
                                                lists2.removeAt(ind)
                                            }
                                            ind++
                                        }
                                    }
                                }catch (e:Exception){}


                            }
                        }
                    }
                    if (lists2.size>0){
                        info_slopes!!.visibility=View.GONE
                        recycler_slopes!!.adapter!!.notifyDataSetChanged()

                    }else{
                        info_slopes!!.visibility=View.VISIBLE
                        recycler_slopes!!.adapter!!.notifyDataSetChanged()
                    }

                }

            }*/

    }
    fun LoadToday(){
        val nformat = DecimalFormat("##,###,###.##")
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "FALSE")
        db.collection("databases").document(database!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot!!.exists()){
                try {
                    box_init!!.text= "$" + nformat.format(documentSnapshot.getLong("cash_initial"))
                    box_cash!!.text = "$" + nformat.format(documentSnapshot.getLong("cash"))
                    val op=(documentSnapshot.getLong("cash")!! - documentSnapshot.getLong("cash_initial")!!)
                    if (op>=0){
                    box_balance!!.text="+$" + nformat.format(abs(op))
                        box_balance!!.setTextColor(resources.getColor(R.color.green_500))
                    }
                    else{
                        box_balance!!.text="-$" + nformat.format(abs(op))
                        box_balance!!.setTextColor(resources.getColor(R.color.red_500))}
                }catch (e: Exception){}
            }
        }
    }
}