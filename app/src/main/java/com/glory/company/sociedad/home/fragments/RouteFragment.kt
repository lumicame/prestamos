package com.glory.company.sociedad.home.fragments

import android.annotation.SuppressLint
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
import com.glory.company.sociedad.R
import com.glory.company.sociedad.RecyclerTouchListener
import com.glory.company.sociedad.home.AdminActivity
import com.glory.company.sociedad.home.EmployeActivity
import com.glory.company.sociedad.loans.loand
import com.glory.company.sociedad.utils.ViewAnimation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DecimalFormat
import java.util.HashMap

class RouteFragment : Fragment() {
    var db = FirebaseFirestore.getInstance()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    //var ListsRoute= java.util.ArrayList<Route>()
    val nformat = DecimalFormat("##,###,###.##")
    var recycler:RecyclerView?=null
    var total_routes:TextView?=null
    var total_loands:TextView?=null
    var total_cap:TextView?=null
    var total_gain_initial:TextView?=null
    var total_total:TextView?=null
    var fab_add:FloatingActionButton?=null
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view =inflater.inflate(R.layout.activity_route, null)
        var prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        recycler=view.findViewById(R.id.recycler)
        total_routes=view.findViewById(R.id.total_routes)
        total_loands=view.findViewById(R.id.total_loands)
        total_cap=view.findViewById(R.id.total_cap)
        total_gain_initial=view.findViewById(R.id.total_gain_initial)
        total_total=view.findViewById(R.id.total_total)
        fab_add=view.findViewById(R.id.fab_add)
        recycler!!.layoutManager = LinearLayoutManager(context)
        recycler!!.hasFixedSize()

        //load()

        fab_add!!.setOnClickListener {
            //createAddRouteDialogo().show()
        }
        recycler!!.addOnItemTouchListener(RecyclerTouchListener(context!!,
            recycler!!,
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
    /*private fun load(){
        var prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        if (type=="admin"){
            if (AdminActivity.ListsRoute.isNotEmpty()){
                ListsRoute.clear()
                for (document in AdminActivity.ListsRoute) {
                    ListsRoute.add(document)
                }
                // recycler!!.adapter!!.notifyDataSetChanged()
                recycler!!.adapter= MyAdapter(ListsRoute,context!!)
                total_routes!!.text=""+ListsRoute.size
            }
            var capital=0
            var ganancia=0.0
            var estimado=0.0
            if (AdminActivity.ListsLoans.isNotEmpty()){

                for (loan in AdminActivity.ListsLoans) {
                    //val loan=document.toObject(loand::class.java)
                    capital+= (loan.capital)
                    val total=loan.total
                    val g=loan.total-loan.capital
                    ganancia+=g
                    estimado+=total
                }
                total_loands!!.text=""+ AdminActivity.ListsLoans.size
                total_cap!!.text="$" +nformat.format(capital)
                total_gain_initial!!.text="$"+nformat.format(ganancia)
                total_total!!.text="$"+nformat.format(estimado)
               // recycler!!.adapter= MyAdapter(ListsRoute,context!!)
            }else{
                total_loands!!.text=""+ AdminActivity.ListsLoans.size
                total_cap!!.text="$" +nformat.format(capital)
                total_gain_initial!!.text="$"+nformat.format(ganancia)
                total_total!!.text="$"+nformat.format(estimado)
            }
        }else{
            fab_add!!.hide()
            if (EmployeActivity.ListsRoute.isNotEmpty()){
                ListsRoute.clear()
                for (document in EmployeActivity.ListsRoute) {
                    ListsRoute.add(document)
                }
                // recycler!!.adapter!!.notifyDataSetChanged()
                recycler!!.adapter= MyAdapter(ListsRoute,context!!)
                total_routes!!.text=""+ListsRoute.size
            }
            var capital=0
            var ganancia=0.0
            var estimado=0.0
            if (EmployeActivity.ListsLoans.isNotEmpty()){
                for (loan in EmployeActivity.ListsLoans) {
                    //val loan=document.toObject(loand::class.java)
                    capital+= (loan.capital)
                    val total=loan.total
                    val g=loan.total-loan.capital
                    ganancia+=g
                    estimado+=total
                }
                total_loands!!.text=""+ EmployeActivity.ListsLoans.size
                total_cap!!.text="$" +nformat.format(capital)
                total_gain_initial!!.text="$"+nformat.format(ganancia)
                total_total!!.text="$"+nformat.format(estimado)
                //recycler!!.adapter= MyAdapter(ListsRoute,context!!)
            }else{
                total_loands!!.text=""+ EmployeActivity.ListsLoans.size
                total_cap!!.text="$" +nformat.format(capital)
                total_gain_initial!!.text="$"+nformat.format(ganancia)
                total_total!!.text="$"+nformat.format(estimado)
            }
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

   /* fun createAddRouteDialogo(): AlertDialog {
        val prefs = context!!.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val dialog: Dialog

        val builder = AlertDialog.Builder(context!!)

        val inflater = this.getLayoutInflater()

        val v = inflater.inflate(R.layout.dialog_addroute, null)

        builder.setView(v)
        dialog=builder.create()
        val title = v.findViewById(R.id.title) as TextView
        val name = v.findViewById(R.id.route_add_name) as EditText
        val description = v.findViewById(R.id.route_add_description) as EditText
        val btn = v.findViewById(R.id.Btn_register) as Button
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Guardando...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||description.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||description.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||description.text.isNullOrEmpty())

            }

        })
        description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||description.text.isNullOrEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||description.text.isNullOrEmpty())

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn.isEnabled = !(name.text.isNullOrEmpty()||description.text.isNullOrEmpty())

            }

        })
        btn.setOnClickListener {
            progressDialog.show()
            val id=db.collection("databases").document(database!!).collection("routes").document().id
            val route = HashMap<String,Any>()
            route["name"] = name.text.toString()
            route["description"] = description.text.toString()
            route["id"]=id
            db.collection("databases").document(database!!).collection("routes").document(id).set(route).addOnCompleteListener {
                progressDialog.dismiss()
                load()
                dialog.dismiss()
                Snackbar.make(fab_add!!.rootView, R.string.register_success_route, Snackbar.LENGTH_LONG)
                    .setAction("OK", null).show()
            }


        }
        return dialog
    }
*/}