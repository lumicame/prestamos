package com.glory.company.sociedad.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.glory.company.sociedad.Entry.Egress
import com.glory.company.sociedad.MainActivity
import com.glory.company.sociedad.Models.Route
import com.glory.company.sociedad.Models.Store
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.R
import com.glory.company.sociedad.Retrofit.ApiServices
import com.glory.company.sociedad.Retrofit.ApiUtils
import com.glory.company.sociedad.client.ClientActivity
import com.glory.company.sociedad.employee.Employee
import com.glory.company.sociedad.history.HistoryActivity
import com.glory.company.sociedad.history.HistoryChargeActivity
import com.glory.company.sociedad.home.fragments.HomeFragment
import com.glory.company.sociedad.home.fragments.NoteFragment
import com.glory.company.sociedad.home.fragments.RouteFragment
import com.glory.company.sociedad.loans.LoansActivity
import com.glory.company.sociedad.loans.loand
import com.glory.company.sociedad.settings.Settings
import com.glory.company.sociedad.trans.Trans
import com.glory.company.sociedad.trans.TransActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.app_bar_admin.*
import kotlinx.android.synthetic.main.content_admin.*
import kotlinx.android.synthetic.main.nav_header_admin.*
import retrofit2.Call
import retrofit2.Callback
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var db = FirebaseFirestore.getInstance()
    var mAPIService: ApiServices? = null

    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val nformat = DecimalFormat("##,###,###.##")
    private var viewIsAtHome = true
    private var tag_fragment="0"
    companion object{

        var ListsLoans = ArrayList<loand>()
        //var ListsStock = ArrayList<Products>()
        var ListsSeach=ArrayList<loand>()
        var ListsRoute= ArrayList<Route>()
        //var ListsNotes=ArrayList<Note>()
        var ListsCharges=ArrayList<Trans>()
        var ListsExpenses=ArrayList<Egress>()
        var consecutive=0
        var reload_home=false
        var card_delete="0"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setSupportActionBar(toolbar)
        mAPIService= ApiUtils.getAPIService()
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val user=prefs!!.getString("email", "")

        val snackbar= Snackbar.make(drawer_layout, R.string.active, Snackbar.LENGTH_INDEFINITE)
            .setAction("OK", null)
        mAPIService!!.verifyStore(prefs!!.getInt("store_id", 0))!!.enqueue(object : Callback<Store?> {
            override fun onFailure(call: Call<Store?>, t: Throwable) {
                Toast.makeText(this@AdminActivity,t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Store?>, response: retrofit2.Response<Store?>) {
                if (response.isSuccessful){
                    val store=response.body()
                    toolbar.title=store!!.name
                    if (!store.active){
                        val navigationView:NavigationView= findViewById(R.id.nav_view)
                        val menuNav=navigationView.menu
                        menuNav.findItem(R.id.nav_clients).isEnabled=false
                        menuNav.findItem(R.id.nav_employee).isEnabled=false
                       // menuNav.findItem(R.id.nav_history).isEnabled=false
                        //menuNav.findItem(R.id.nav_loands).isEnabled=false
                        menuNav.findItem(R.id.nav_settings).isEnabled=false
                        menuNav.findItem(R.id.nav_trans).isEnabled=false
                        snackbar.show()
                    }
                    else{
                        val navigationView:NavigationView= findViewById(R.id.nav_view)
                        val menuNav=navigationView.menu
                        menuNav.findItem(R.id.nav_clients).isEnabled=true
                        menuNav.findItem(R.id.nav_employee).isEnabled=true
                        menuNav.findItem(R.id.nav_history).isEnabled=true
                        menuNav.findItem(R.id.nav_loands).isEnabled=true
                        menuNav.findItem(R.id.nav_settings).isEnabled=true
                        menuNav.findItem(R.id.nav_trans).isEnabled=true
                       // LoadProfile()
                        //slopes()
                        //LoadBox()
                        //LoadToday()
                        //LoadBox()
                        if (tag_fragment=="0"){
                            addFragment(HomeFragment())
                            tag_fragment="1"
                        }
                        snackbar.dismiss()
                    }

                }
            }


        })


       /* db.collection("users").document(user!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (!documentSnapshot!!.exists()){
                val prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
                val editor = prefs!!.edit()
                editor.remove("login")
                editor.remove("type")
                editor.remove("email")
                editor.remove("database")
                editor.remove("name")
                editor.apply()
                val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                if (prefs!!.getString("pass", "")!=documentSnapshot["pass"].toString()){
                    val prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
                    val editor = prefs!!.edit()
                    editor.remove("login")
                    editor.remove("type")
                    editor.remove("email")
                    editor.remove("database")
                    editor.remove("name")
                    editor.apply()
                    val intent= Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
*/
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_recent -> {
                    toolbar.title=resources.getString(R.string.app_name)
                    addFragment(HomeFragment())
                    viewIsAtHome = true
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorites -> {
                    toolbar.title="Rutas"
                    addFragment(RouteFragment())
                    viewIsAtHome = false
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_nearby -> {
                    toolbar.title="Notas"
                    addFragment(NoteFragment())
                    viewIsAtHome = false
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        nav_view.setNavigationItemSelectedListener(this)
    }
    fun LoadBox(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "FALSE")
        val nit=prefs!!.getString("email", "FALSE")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val DateandTime = sdf2.format(Calendar.getInstance().getTime())


        db.collection("databases").document(database!!).collection("charges").whereEqualTo(
            "date",
            DateandTime
        ).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            ListsCharges.clear()
            if (!querySnapshot!!.isEmpty){

                for (document in querySnapshot) {
                    Log.e("ADDED CHARGES",document["date_hour"].toString())
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
                reload_home=true
            }

        }
        db.collection("databases").document(database).collection("expenses").whereEqualTo(
            "date",
            DateandTime
        ).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            ListsExpenses.clear()
            if (!querySnapshot!!.isEmpty){
                for (document in querySnapshot) {
                   // val loan = document.document.toObject(loand::class.java)
                    Log.e("ADDED EXPENSES",document["date_hour"].toString())
                    ListsExpenses.add(
                        Egress(
                            document["name"].toString(),
                            document["date_hour"].toString(),
                            document["total"].toString().toDouble(),
                            document["user_nit"].toString(),
                            document["loand"].toString()
                        )
                    )
                }
                reload_home=true
            }

        }
    }

    override fun toString(): String {
        return super<AppCompatActivity>.toString()

    }
    fun slopes(){
        var prefs =getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        ListsLoans.clear()
        db.collection("databases").document(database!!).collection("loands")
            .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

                if (!querySnapshot!!.isEmpty) {
                    for (document in querySnapshot!!.documentChanges) {
                        val loan = document.document.toObject(loand::class.java)
                        if (document.type==DocumentChange.Type.ADDED){
                            Log.e("ADDED",document.document["id"].toString())
                            ListsLoans.add(loan)
                        }
                        if (document.type==DocumentChange.Type.MODIFIED){
                            for ((i,loan_) in ListsLoans.withIndex()){
                                if (loan_.id==loan.id){
                                    ListsLoans[i]=loan
                                }
                            }
                            Log.e("MODIFIED",document.document["id"].toString())
                        }
                        if (document.type==DocumentChange.Type.REMOVED){
                            Log.e("REMOVED",document.document["id"].toString())
                            var index=-1
                            for ((i,loan_) in ListsLoans.withIndex()){
                                if (loan_.id==document.document["id"].toString()){
                                    index=i
                                }
                            }
                            if (index!=-1)
                                ListsLoans.removeAt(index)
                        }
                    }
                    reload_home=true
                }
                else{
                    ListsLoans.clear()
                    reload_home=true
                }
            }
    }
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }
    override fun onResume() {
        super.onResume()
//        recycler_route.adapter = RouteAdapter(ListsRoute, this)
        if (viewIsAtHome){
            if (tag_fragment!="0"){
        toolbar.title=resources.getString(R.string.app_name)
        addFragment(HomeFragment())
        viewIsAtHome = true}
        }
        else{
            toolbar.title="Rutas"
            addFragment(RouteFragment())
            viewIsAtHome = false
        }
    }
    fun LoadProfile(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val name=prefs!!.getString("name", "")
        val docRef = db.collection("databases").document(database!!)
        val nformat = DecimalFormat("##,###,###.##")
        docRef.addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (documentSnapshot!!.exists()) {
                try {
                    profile_name.text=name
                    profile_store.text=documentSnapshot["name"].toString()
                }catch (e: Exception){

                }
            }
        }
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (!viewIsAtHome) { //if the current view is not the News fragment
                navigation.selectedItemId = R.id.navigation_recent //display the News fragment
                addFragment(HomeFragment())
            } else {
                moveTaskToBack(true) //If view is in News fragment, exit application
            }
            super.onBackPressed()
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_clients -> {
                val intent = Intent(this, ClientActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_employee -> {
                val intent = Intent(this, Employee::class.java)
                startActivity(intent)
            }

            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_trans -> {
                val intent = Intent(this, TransActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_history_charges -> {
                val intent = Intent(this, HistoryChargeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_loands -> {
                val intent = Intent(this, LoansActivity::class.java)
                intent.putExtra("route", "")
                startActivity(intent)
            }

            R.id.nav_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }

            R.id.nav_exit -> {
                val prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
                val editor = prefs!!.edit()
                editor.remove("login")
                editor.remove("type")
                editor.remove("pass")
                editor.remove("email")
                editor.remove("database")
                editor.remove("name")
                editor.clear()
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
