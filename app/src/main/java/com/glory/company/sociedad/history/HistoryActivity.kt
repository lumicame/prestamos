package com.glory.company.sociedad.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.glory.company.sociedad.R
import com.glory.company.sociedad.loans.LoandAdapter
import com.glory.company.sociedad.loans.loand
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.back
import kotlinx.android.synthetic.main.activity_history.num_dues
import kotlinx.android.synthetic.main.activity_history.num_loands
import kotlinx.android.synthetic.main.activity_history.recycler
import kotlinx.android.synthetic.main.activity_history.search
import kotlinx.android.synthetic.main.activity_history.total_gain_initial
import kotlinx.android.synthetic.main.activity_history.total_total
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val c = Calendar.getInstance()
    val mes = c.get(Calendar.MONTH)
    val dia = c.get(Calendar.DAY_OF_MONTH)
    val anio = c.get(Calendar.YEAR)
    private val CERO = "0"
    private val BARRA = "-"

    var lists = ArrayList<loand>()
    var listsSearch = ArrayList<loand>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        back.setOnClickListener {
            onBackPressed()
        }
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")

        db.collection("databases").document(database!!).collection("history")
            .get().addOnCompleteListener{
                lists.clear()
                if (!it.result!!.isEmpty){
                    for (item in it.result!!){
                        val loan=item.toObject(loand::class.java)
                        lists.add(loan)
                    }
                }
            }
        search.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            search()
            }
        })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()

    }
    fun search(){
        var numloands=0
        var numdues=0
        var capital=0
        var ganancia=0.0
        var estimado=0.0
        val nformat = DecimalFormat("##,###,###.##")
        if (search.text.isNullOrEmpty()){
            Snackbar.make(search,"Utiliza el buscardor para encontrar la cartulina que buscas.",
                Snackbar.LENGTH_LONG).show()
        }
        else{
            listsSearch.clear()
            for (item in lists){
                if (item.client!!.name.toLowerCase().contains(search.text.toString().toLowerCase())
                    || item.id.toLowerCase().contains(search.text.toString().toLowerCase())
                    || item.client!!.nit.toLowerCase().contains(search.text.toString().toLowerCase())
                    || item.name_route.toLowerCase().contains(search.text.toString().toLowerCase())){
                    numdues += item.due
                    capital+= item.capital
                    val total=item.capital+(item.capital * item.percentage.toDouble()/100).toFloat()
                    val g=(item.capital * (item.percentage.toDouble()/100).toFloat())
                    ganancia+=g
                    estimado+=total
                }
                listsSearch.add(item)
                }
            if (listsSearch.size>0){
                info_search.visibility= View.GONE
                container.visibility= View.VISIBLE

                num_loands.text=""+numloands
                num_dues.text=""+numdues
                total_capital.text="$"+nformat.format(capital)
                total_gain_initial.text="$"+nformat.format(ganancia)
                total_total.text="$"+nformat.format(estimado)
                recycler.adapter = LoandAdapter(lists, this)
            }else{
                info_search.text="No hay resultados para esta busqueda."
                info_search.visibility= View.VISIBLE
                num_loands.text=""+numloands
                num_dues.text=""+numdues
                total_capital.text="$"+nformat.format(capital)
                total_gain_initial.text="$"+nformat.format(ganancia)
                total_total.text="$"+nformat.format(estimado)
                recycler.adapter = LoandAdapter(lists, this)
            }

        }

    }

}
