package com.glory.company.sociedad.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.Entry.AdapterExpense
import com.glory.company.sociedad.Entry.Egress
import com.glory.company.sociedad.R
import com.glory.company.sociedad.trans.MyAdapter
import com.glory.company.sociedad.trans.Trans
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_history_charge.*
import java.text.DecimalFormat
import java.util.*

class HistoryChargeActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val c = Calendar.getInstance()
    val mes = c.get(Calendar.MONTH)
    val dia = c.get(Calendar.DAY_OF_MONTH)
    val anio = c.get(Calendar.YEAR)
    private val CERO = "0"
    private val BARRA = "-"
    var cash_=0.0
    var egres=0.0
    var credit_=0.0
    var lists_charges = ArrayList<Trans>()
    var lists_expenses = ArrayList<Egress>()
    var entry=true
    val nformat = DecimalFormat("##,###,###.##")

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_charge)
        back.setOnClickListener {
            onBackPressed()
        }
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs!!.getString("type", "")


        if (type!="admin"){
            fab_info.visibility=View.GONE
        }

        date_initial.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                show()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                show()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                show()
            }

        })
        date_final.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                show()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                show()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                show()
            }

        })
        date_initial.setOnClickListener {
            val recogerFecha = DatePickerDialog(
                this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                    val mesActual = month + 1
                    //Formateo el día obtenido: antepone el 0 si son menores de 10
                    val diaFormateado = if (dayOfMonth < 10) CERO + dayOfMonth.toString() else dayOfMonth.toString()
                    //Formateo el mes obtenido: antepone el 0 si son menores de 10
                    val mesFormateado = if (mesActual < 10) CERO + mesActual.toString() else mesActual.toString()
                    //Muestro la fecha con el formato deseado
                    date_initial.text = year.toString()  + BARRA + mesFormateado + BARRA + diaFormateado
                },
                //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                /**
                 * También puede cargar los valores que usted desee
                 */
                anio, mes, dia
            )
            //Muestro el widget
            recogerFecha.show()
        }
        date_final.setOnClickListener {
            val recogerFecha = DatePickerDialog(
                this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                    val mesActual = month + 1
                    //Formateo el día obtenido: antepone el 0 si son menores de 10
                    val diaFormateado = if (dayOfMonth < 10) CERO + dayOfMonth.toString() else dayOfMonth.toString()
                    //Formateo el mes obtenido: antepone el 0 si son menores de 10
                    val mesFormateado = if (mesActual < 10) CERO + mesActual.toString() else mesActual.toString()
                    //Muestro la fecha con el formato deseado
                    date_final.text = year.toString()  + BARRA + mesFormateado + BARRA + diaFormateado
                },
                //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                /**
                 * También puede cargar los valores que usted desee
                 */
                anio, mes, dia
            )
            //Muestro el widget
            recogerFecha.show()
        }
        fab_info.setOnClickListener {
            createInfoDialg().show()
        }
        recycler_entry.layoutManager = LinearLayoutManager(this)
        recycler_entry.hasFixedSize()
        recycler_expenses.layoutManager = LinearLayoutManager(this)
        recycler_expenses.hasFixedSize()
        recycler_entry.adapter = MyAdapter(lists_charges, this)
        recycler_expenses.adapter = AdapterExpense(lists_expenses, this)
        btn_entry!!.setOnClickListener {
            if (!entry){
                entry=true
                btn_entry!!.setBackgroundResource(R.drawable.buttonok)
                btn_expenses!!.setBackgroundResource(R.drawable.gray)
                recycler_entry!!.visibility=View.VISIBLE
                recycler_expenses!!.visibility=View.GONE
                recycler_entry!!.adapter= MyAdapter(lists_charges, this)
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
                recycler_expenses!!.adapter= AdapterExpense(lists_expenses, this)
                //recycler_expenses!!.adapter!!.notifyDataSetChanged()
            }
        }
    }
    fun show(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs!!.getString("type", "")

        if (date_initial.text.isNullOrEmpty()||date_final.text.isNullOrEmpty()){
            container.visibility= View.GONE
            info_search.text=getString(R.string.select_date_info)
            info_search.visibility= View.VISIBLE
        }else{
            info_search.visibility= View.GONE
            container.visibility= View.VISIBLE
            if (type=="admin"){
                LoadEntry(date_initial.text.toString(),date_final.text.toString())
                LoadExpenses(date_initial.text.toString(),date_final.text.toString())
            }
            else{
                LoadEntryEmployee(date_initial.text.toString(),date_final.text.toString())
                LoadExpensesEmployee(date_initial.text.toString(),date_final.text.toString())
            }
        }
    }
    fun calculate(){
        val balance=cash_-egres
        credit.text="$"+nformat.format(credit_)
        egress.text="$"+nformat.format(egres)
        box_credit.text="$"+nformat.format(credit_)
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
        info_search.visibility= View.GONE
        container.visibility= View.VISIBLE
    }
    fun LoadEntry(dateinitial:String,datefinal:String) {
        val nformat = DecimalFormat("##,###,###.##")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        db.collection("databases").document(database!!).collection("charges")
            .whereGreaterThanOrEqualTo("date",dateinitial)
            .whereLessThanOrEqualTo("date",datefinal)
            .get().addOnCompleteListener {
                if (!it.result!!.isEmpty){
                    cash_=0.0
                    lists_charges.clear()
                    for (document in it.result!!) {
                        lists_charges.add(
                            Trans(
                                document["loand"].toString(),
                                document["due"].toString(),
                                document["date_hour"].toString(),
                                document["description"].toString(),
                                document["user_nit"].toString(),
                                document["total"].toString().toDouble()
                            )
                        )
                        cash_+=document["total"].toString().toDouble()
                    }
                    try{
                        progressDialog.dismiss()
                        calculate()
                    }catch (e:Exception){}
                }else{
                    try{
                        progressDialog.dismiss()
                        calculate()
                    }catch (e:Exception){}
                }
            }
    }
    fun LoadExpenses(dateinitial:String,datefinal:String) {
        val nformat = DecimalFormat("##,###,###.##")
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        db.collection("databases").document(database!!)
            .collection("expenses").whereGreaterThanOrEqualTo("date",dateinitial)
            .whereLessThanOrEqualTo("date",datefinal)
            .get().addOnCompleteListener {
            if (!it.result!!.isEmpty){
                egres=0.0
                credit_=0.0
                lists_expenses.clear()
                for (document in it.result!!) {
                    val egress=Egress(
                        document["name"].toString(),
                        document["date_hour"].toString(),
                        document["total"].toString().toDouble(),
                        document["user_nit"].toString(),
                        document["loand"].toString()
                    )
                    lists_expenses.add(egress)
                    if (egress.loand.equals("null"))
                    egres+= egress.total!!
                    else
                        credit_+= egress.total!!
                }
                calculate()
            }
        }

    }
    fun LoadEntryEmployee(dateinitial:String,datefinal:String) {
        val nformat = DecimalFormat("##,###,###.##")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val user=prefs!!.getString("email", "")
        db.collection("databases").document(database!!).collection("charges")
            .whereGreaterThanOrEqualTo("date",dateinitial)
            .whereLessThanOrEqualTo("date",datefinal)
            .get().addOnCompleteListener {
                if (!it.result!!.isEmpty){
                    lists_charges.clear()
                    cash_=0.0
                    for (document in it.result!!) {
                        if (document["user_nit"].toString().equals(user)) {
                            lists_charges.add(
                                Trans(
                                    document["loand"].toString(),
                                    document["due"].toString(),
                                    document["date_hour"].toString(),
                                    document["description"].toString(),
                                    document["user_nit"].toString(),
                                    document["total"].toString().toDouble()
                                )
                            )
                            cash_ += document["total"].toString().toDouble()
                            /* if (user==document["user_nit"].toString()){
                        }*/
                        }
                    }
                    try{
                        calculate()
                        progressDialog.dismiss()
                    }catch (e:Exception){}
                }
                else{
                    try{
                        calculate()
                        progressDialog.dismiss()
                    }catch (e:Exception){}
                }
            }
    }
    fun LoadExpensesEmployee(dateinitial:String,datefinal:String) {
        val nformat = DecimalFormat("##,###,###.##")
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val user=prefs!!.getString("email", "")
        db.collection("databases").document(database!!)
            .collection("expenses")
            .whereGreaterThanOrEqualTo("date",dateinitial)
            .whereLessThanOrEqualTo("date",datefinal)
            .get().addOnCompleteListener {
                if (!it.result!!.isEmpty){
                    lists_expenses.clear()
                    egres=0.0
                    credit_=0.0
                    for (document in it.result!!) {
                        if (document["user_nit"].toString().equals(user)) {
                            val egress = Egress(
                                document["name"].toString(),
                                document["date_hour"].toString(),
                                document["total"].toString().toDouble(),
                                document["user_nit"].toString(),
                                document["loand"].toString()
                            )
                            lists_expenses.add(egress)
                            if (egress.loand.equals("null"))
                                egres += egress.total!!
                            else
                                credit_ += egress.total!!
                        }

                    }
                    try{
                        calculate()
                    }catch (e:Exception){}
                }
            }

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
                    for (item in lists_charges){
                        if (item.user_nit==document["nit"].toString()){
                            cash+=item.total
                        }
                    }
                    for (item in lists_expenses){
                        if (item.user_nit==document["nit"].toString()){
                            if (item.loand.equals("null"))
                            expenses+= item.total!!
                            else
                                credit+= item.total!!
                        }
                    }
                    lists.add(UserInfoClass(
                        document["name"].toString(),
                        cash,
                        expenses,
                        credit,
                        cash-expenses
                        ))
                }
                recycler.adapter!!.notifyDataSetChanged()
            }
        }
        return dialog
    }
}
