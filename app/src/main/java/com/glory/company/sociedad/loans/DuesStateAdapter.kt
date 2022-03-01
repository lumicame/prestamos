package com.glory.company.sociedad.loans

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.recycler_view_dues_charge.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class DuesStateAdapter (private val dues: ArrayList<duestate>, private val context: Context) : RecyclerView.Adapter<DuesStateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_dues_charge, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(dues[p1],context)
    }

    override fun getItemCount()=dues.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nformat = DecimalFormat("##,###,###.##")
        var db = FirebaseFirestore.getInstance()
        val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
        val c = Calendar.getInstance()
        var mes = c.get(Calendar.MONTH)
        var dia = c.get(Calendar.DAY_OF_MONTH)
        var anio = c.get(Calendar.YEAR)
        private val CERO = "0"
        private val BARRA = "/"

        fun load(item: duestate, context: Context){

        }

        fun bindItems(item: duestate, context: Context) {
            var prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            val nit=prefs!!.getString("email", "")
            val type=prefs!!.getString("type", "")
            if (type=="client"){
                itemView.state.isEnabled=false
                itemView.date.isEnabled=false
                itemView.due.isEnabled=false
            }
            itemView.num.text=""+item.num
            itemView.date.text=""+item.date
            itemView.due.text="$"+nformat.format(item.due)
           if (item.state!!){
               itemView.state.text = context.getString(R.string.paidout)
               itemView.state.setTextColor(ContextCompat.getColor(context, R.color.green))
               itemView.state.isChecked=true
               itemView.state.isEnabled=false
           }
            else{
               itemView.state.text = context.getString(R.string.pending)
               itemView.state.setTextColor(ContextCompat.getColor(context, R.color.red))
               itemView.state.isChecked=false
               itemView.due.setOnClickListener {
                createEditDueDialog(context,item.num).show()
               }
               itemView.date.setOnClickListener {
                   /*val formato = SimpleDateFormat("dd/MM/yyyy")
                   val fecha = formato.parse(item.date)
                   anio=fecha.
                   mes=fecha.month
                   dia=fecha.day*/
                   val recogerFecha = DatePickerDialog(
                       context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                           //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                           val mesActual = month + 1
                           //Formateo el día obtenido: antepone el 0 si son menores de 10
                           val diaFormateado = if (dayOfMonth < 10) CERO + dayOfMonth.toString() else dayOfMonth.toString()
                           //Formateo el mes obtenido: antepone el 0 si son menores de 10
                           val mesFormateado = if (mesActual < 10) CERO + mesActual.toString() else mesActual.toString()
                           //Muestro la fecha con el formato deseado
                           for (item_ in ViewLoandActivity.Loan!!.dues!!){
                               if (item_.num==item.num){
                                   item_.date=  diaFormateado+ BARRA + mesFormateado + BARRA + year.toString()
                               }
                           }
                           db.collection("databases").document(database!!).collection("loands")
                               .document(item.id_loand)
                               .update("dues", ViewLoandActivity.Loan!!.dues!!).addOnCompleteListener{
                                   Toast.makeText(context,"Se ha actualizado la fecha de pago para la cartulina",Toast.LENGTH_LONG).show()
                               }
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
           }
            itemView.state.setOnClickListener {
                if (item.state){

                }else{
                    val progressDialog = ProgressDialog(context)
                    progressDialog.setTitle(context.getString(R.string.wait))
                    progressDialog.setMessage(context.getString(R.string.saving))
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    val sdf = SimpleDateFormat("yyyy-M-d")
                    val currentDateandTime = sdf.format(Calendar.getInstance().time)

                    db.collection("databases").document(database!!)
                        .get().addOnCompleteListener {store->

                            val loanupdate = HashMap<String,Any>()
                            loanupdate["next"]= ViewLoandActivity.Loan!!.next + 1
                            loanupdate["slopes"]= ViewLoandActivity.Loan!!.slopes - 1
                            loanupdate["paidout"]= ViewLoandActivity.Loan!!.paidout!!+item.due
                            //Se suman las cuotas pendientes y la siguiente
                                    db.collection("users").document(ViewLoandActivity.Loan!!.client!!.id.toString())
                                        .get().addOnCompleteListener{client->
                                           // try {
                                            val formato = SimpleDateFormat("dd/MM/yyyy")
                                            val dias = ((formato.parse(item.date).time - sdf.parse(currentDateandTime).time) / 86400000)
                                            var points=0
                                            if (dias >=0)
                                                points=5

                                            if (dias<0&&dias>=-5)
                                                points= (5+dias).toInt()

                                            val user = HashMap<String,Any>()
                                            user["points"]= client.result!!["points"].toString().toInt()+points
                                            user["dues"]= client.result!!["dues"].toString().toInt()+1
                                            db.collection("users").document(ViewLoandActivity.Loan!!.client!!.id.toString()).update(user)
                                                .addOnCompleteListener {}

                                              // }catch (e:Exception){}
                                        }
                                    //Se cambia el estado de la cuota a pagado
                                    for ((i,item_) in ViewLoandActivity.Loan!!.dues!!.withIndex()){
                                        if (item_.num==item.num){
                                            ViewLoandActivity.Loan!!.dues!![i]!!.state=true
                                            ViewLoandActivity.Loan!!.dues!![i]!!.due=0
                                            loanupdate["dues"]=ViewLoandActivity.Loan!!.dues!!
                                            ViewLoandActivity.Loan!!.paidout= ViewLoandActivity.Loan!!.paidout+ ViewLoandActivity.Loan!!.dues!![i].due!!
                                            Log.d("IGUALIITITITITITITITI",
                                                ViewLoandActivity.Loan!!.dues!![i].date!!
                                            )

                                        }
                                    }
                            db.collection("databases").document(database!!).collection("loands")
                                .document(item.id_loand)
                                .update(loanupdate).addOnCompleteListener {
                                            //Se acctualiza la caja y se agrega el cobro
                                            db.collection("databases").document(database).update("cash",
                                                store.result!!["cash"].toString().toDouble()+item.due).addOnCompleteListener {
                                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                                                val currentDateandTime = sdf.format(Calendar.getInstance().time)
                                                val currentDateandTime2 = sdf2.format(Calendar.getInstance().time)
                                                val charge = HashMap<String,Any>()
                                                charge["user_nit"]= nit!!
                                                charge["date_hour"]= currentDateandTime
                                                charge["date"]= currentDateandTime2
                                                charge["total"]=item.due
                                                charge["num"]=item.num
                                                charge["due"]=item.id
                                                charge["client"]= ViewLoandActivity.Loan!!.client!!.nit
                                                charge["loand"]=item.id_loand
                                                charge["description"]= context.getString(R.string.charge_to)+" - "+context.getString(R.string.charge_due_num)+" "+item.num
                                                //Se agrega el cobro al historial
                                                db.collection("databases").document(database)
                                                    .collection("charges").document(charge["date_hour"].toString()).set(charge).addOnCompleteListener {
                                                        Toast.makeText(context,context.getString(R.string.charge_successfull),Toast.LENGTH_LONG).show()
                                                        progressDialog.dismiss()
                                                    }
                                            }


                                }

                        }

                }
            }

        }

        fun createEditDueDialog(context: Context,num: Int): AlertDialog {
            var prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            val nit=prefs!!.getString("email", "")
            val type=prefs!!.getString("type", "")
            val dialog: Dialog
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val v = inflater.inflate(R.layout.dialog_add_cash_preview, null)
            builder.setView(v)
            dialog=builder.create()
            val actual = v.findViewById(R.id.add_cash_actual) as TextView
            val new = v.findViewById(R.id.add_cash_valor) as EditText
            val btn = v.findViewById(R.id.Btn_addcash) as Button
            val due_=ViewLoandActivity.Loan!!.total/ViewLoandActivity.Loan!!.due
            for (item_ in ViewLoandActivity.Loan!!.dues!!){
                if (item_.num==num){
                    actual.text="Cuota: $"+nformat.format(item_.due)
                }
            }
            new.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    btn.isEnabled = !(new.text.isNullOrEmpty())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    btn.isEnabled = !(new.text.isNullOrEmpty())
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btn.isEnabled = !(new.text.isNullOrEmpty())
                }

            })

            btn.setOnClickListener {
                val progressDialog = ProgressDialog(context)
                progressDialog.setTitle(context.getString(R.string.wait))
                progressDialog.setMessage(context.getString(R.string.saving))
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false)
                progressDialog.show()
                // Create a new user with a first and last name
                for ((i,item_) in ViewLoandActivity.Loan!!.dues!!.withIndex()){
                    if (item_.num==num){
                                if (item_.due!! >new.text.toString().toInt()){
                                        val dif= item_.due!! -new.text.toString().toInt()
                                        ViewLoandActivity.Loan!!.dues!![i].due =dif
                                        ViewLoandActivity.Loan!!.paidout= (ViewLoandActivity.Loan!!.paidout!!+new.text.toString().toDouble()).toInt()
                                    if (ViewLoandActivity.Loan!!.dues!!.isNotEmpty()){
                                                    val loanupdate = HashMap<String,Any>()
                                                    loanupdate["paidout"]= ViewLoandActivity.Loan!!.paidout!!
                                                    loanupdate["dues"]=ViewLoandActivity.Loan!!.dues!!
                                                    db.collection("databases").document(database!!).collection("loands")
                                                        .document(ViewLoandActivity.Loan!!.id)
                                                        .update(loanupdate).addOnCompleteListener {
                                                                    //Se actualiza la caja y se agrega el cobro
                                                                    db.collection("databases").document(database!!)
                                                                        .get().addOnCompleteListener {store->
                                                                            db.collection("databases").document(database).update("cash",
                                                                                store.result!!["cash"].toString().toDouble()+new.text.toString().toDouble()).addOnCompleteListener {
                                                                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                                                val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                                                                                val currentDateandTime = sdf.format(Calendar.getInstance().time)
                                                                                val currentDateandTime2 = sdf2.format(Calendar.getInstance().time)
                                                                                val charge = HashMap<String,Any>()
                                                                                charge["user_nit"]= nit!!
                                                                                charge["date_hour"]= currentDateandTime
                                                                                charge["date"]= currentDateandTime2
                                                                                charge["total"]=new.text.toString().toDouble()
                                                                                charge["num"]=item_!!.num!!
                                                                                charge["due"]=""+i
                                                                                charge["client"]= ViewLoandActivity.Loan!!.client!!.nit
                                                                                charge["loand"]=ViewLoandActivity.Loan!!.id
                                                                                charge["description"]= "Abono a la deuda"
                                                                                //Se agrega el cobro al historial
                                                                                db.collection("databases").document(database)
                                                                                    .collection("charges").document(charge["date_hour"].toString()).set(charge).addOnCompleteListener {
                                                                                        db.collection("users").document(
                                                                                            ViewLoandActivity.Loan!!.client!!.id.toString()
                                                                                        )
                                                                                            .get().addOnCompleteListener{client->
                                                                                                val user = HashMap<String,Any>()
                                                                                                user["points"]= client.result!!["points"].toString().toInt()+3
                                                                                                user["dues"]= client.result!!["dues"].toString().toInt()+1
                                                                                                db.collection("users").document(
                                                                                                    ViewLoandActivity.Loan!!.client!!.id.toString()
                                                                                                ).update(user)
                                                                                                    .addOnCompleteListener {
                                                                                                        progressDialog.dismiss()
                                                                                                        dialog.dismiss()
                                                                                                        Toast.makeText(context,context.getString(R.string.abono_successfull),Toast.LENGTH_LONG).show()

                                                                                                    }
                                                                                                  }

                                                                                    }
                                                                            }

                                                                        }
                                                    dialog.dismiss()
                                                }
                                        dialog.dismiss()
                                    }
                                }else{
                                    Toast.makeText(context,"El abono no puede superar la cuota",Toast.LENGTH_SHORT).show()
                                    progressDialog.dismiss()
                                    dialog.dismiss()
                                }




                    }
                }
            }

            return dialog
        }

    }
}