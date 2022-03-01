package com.glory.company.sociedad.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.glory.company.sociedad.R
import com.glory.company.sociedad.loans.ViewLoandActivity
import com.glory.company.sociedad.loans.loand
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.back
import kotlinx.android.synthetic.main.activity_view_loand.*
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class Settings : AppCompatActivity() {
    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    var db = FirebaseFirestore.getInstance()
    var cap_actualy="0"
    var cap_init="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        LoadStores()

        back.setOnClickListener {
            onBackPressed()
        }
        excel.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    // Aquí muestras confirmación explicativa al usuario
                    // por si rechazó los permisos anteriormente
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        123
                    );
                }
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // Aquí muestras confirmación explicativa al usuario
                    // por si rechazó los permisos anteriormente
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        321
                    );
                }
            }
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentDateandTime = sdf.format(Calendar.getInstance().getTime())

                saveExcelFile(this,"backup $currentDateandTime.xls")


        }
        add_capital.setOnClickListener {
            createEditDueDialog(this,settings_cap_initial.text.toString().toInt()).show()
        }
    }
    fun createEditDueDialog(context: Context,capital: Int): AlertDialog {
        val nformat = DecimalFormat("##,###,###.##")
        var prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val nit=prefs!!.getString("email", "")
        val type=prefs!!.getString("type", "")
        val dialog: Dialog
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.dialog_add_capital_init, null)
        builder.setView(v)
        dialog=builder.create()
        val actual = v.findViewById(R.id.add_cash_actual) as TextView
        val new = v.findViewById(R.id.add_cash_valor) as EditText
        val btn = v.findViewById(R.id.Btn_addcash) as Button
        actual.text="Actual: "+nformat.format(capital)
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
            val capital_ = HashMap<String,Any>()
            capital_["cash"]= (cap_actualy.toDouble()+new.text.toString().toDouble()).toInt()
            capital_["cash_initial"]=(cap_init.toDouble()+new.text.toString().toDouble()).toInt()
            db.collection("databases").document(database!!).update(capital_).addOnCompleteListener {
                settings_cap_initial.setText(capital_["cash_initial"].toString())
                dialog.dismiss()
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Capital agregado",Toast.LENGTH_SHORT).show()
            }
        }

        return dialog
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 123) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentDateandTime = sdf.format(Calendar.getInstance().getTime())
                saveExcelFile(this,"backup $currentDateandTime.xls")
            } else
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show()

        }
        if (requestCode == 321) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentDateandTime = sdf.format(Calendar.getInstance().getTime())
                saveExcelFile(this,"backup $currentDateandTime.xls")
            } else
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show()

        }

    }

    fun save(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setMessage(getString(R.string.saving))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        val user =  HashMap<String,Any>()
        user["name"] = settings_name.text.toString()
        user["pass"] = settings_pass.text.toString()

        val store =  HashMap<String,Any>()
        store["name"] = settings_name_store.text.toString()
        val editor = prefs.edit()
        editor.putString("pass",user["pass"].toString())
        editor.putString("name",user["name"].toString())
        editor.apply()
        db.collection("users").document(settings_nit.text.toString()).update(user).addOnCompleteListener {
            db.collection("databases").document(database!!).update(store).addOnCompleteListener {
             progressDialog.dismiss()
            }
        }
    }
    fun LoadStores(){
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")
        val type=prefs!!.getString("type", "")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        progressDialog.show()
        settings_nit.setText(prefs!!.getString("email", ""))
        settings_name.setText(prefs!!.getString("name", ""))
        settings_pass.setText(prefs!!.getString("pass", ""))
        db.collection("databases").document(database!!).get().addOnCompleteListener {
            if (!it.result!!.exists()){

            }else{
                if (type!="admin"){
                    content_store.visibility=View.GONE
                    excel.visibility=View.GONE
                }
                cap_actualy=it.result!!["cash"].toString()
                cap_init=it.result!!["cash_initial"].toString()
                settings_name_store.setText(it.result!!["name"].toString())
                info_other.text=getString(R.string.other_options)+" "+ getString(R.string.forb)+" ("+it.result!!["name"].toString()+")"
                settings_cap_initial.setText(""+it.result!!["cash_initial"].toString())
                progressDialog.dismiss()
                val mySnackbar = Snackbar.make(
                    settings_name,
                    getString(R.string.save_changes), Snackbar.LENGTH_INDEFINITE)
                mySnackbar.setActionTextColor(resources.getColor(R.color.colorPrimary))
                mySnackbar.setAction(R.string.save) { save() }
                settings_name_store.addTextChangedListener(object :TextWatcher{
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!mySnackbar.isShown){
                            mySnackbar.show()
                        }
                    }

                })
                settings_name.addTextChangedListener(object :TextWatcher{
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!mySnackbar.isShown){
                            mySnackbar.show()
                        }
                    }

                })
                settings_pass.addTextChangedListener(object :TextWatcher{
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!mySnackbar.isShown){
                            mySnackbar.show()
                        }
                    }

                })


            }
        }

    }
    private fun saveExcelFile(context: Context, fileName: String): Boolean {
        var prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val database=prefs!!.getString("database", "")

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("AVAILABLE", "Storage not available or read only")
            return false
        }
        val nformat = DecimalFormat("##,###,###.##")
        var success = false

        //New Workbook
        val wb = HSSFWorkbook()

        var c: Cell? = null

        //Cell style for header row
        val cs = wb.createCellStyle()
        cs.fillForegroundColor = HSSFColor.LIME.index
        cs.fillPattern = HSSFCellStyle.SOLID_FOREGROUND

        //New Sheet
        var sheet1: Sheet? = null
        sheet1 = wb.createSheet("Cartulinas")

        // Generate column headings
        val row = sheet1!!.createRow(0)

        c = row.createCell(0)
        c!!.setCellValue("Id Cartulina")
        c!!.cellStyle = cs

        c = row.createCell(1)
        c!!.setCellValue("Nombre")
        c!!.cellStyle = cs

        c = row.createCell(2)
        c!!.setCellValue("Fecha prestamo")
        c!!.cellStyle = cs

        c = row.createCell(3)
        c!!.setCellValue("Proximo pago")
        c!!.cellStyle = cs

        c = row.createCell(4)
        c!!.setCellValue("Cuota pago")
        c!!.cellStyle = cs

        c = row.createCell(5)
        c!!.setCellValue("Capital prestado")
        c!!.cellStyle = cs

        c = row.createCell(6)
        c!!.setCellValue("Total a pagar")
        c!!.cellStyle = cs

        c = row.createCell(7)
        c!!.setCellValue("Pagado")
        c!!.cellStyle = cs

        c = row.createCell(8)
        c!!.setCellValue("Debe")
        c!!.cellStyle = cs

        c = row.createCell(9)
        c!!.setCellValue("Ruta")
        c!!.cellStyle = cs

        db.collection("databases").document(database!!).collection("loands").orderBy("route")
            .get().addOnCompleteListener {
                if (!it!!.result!!.isEmpty ) {
                    var num=0
                    for (document in it.result!!) {
                        val loand=document.toObject(loand::class.java)
                        num+=1
                        val row = sheet1!!.createRow(num)
                        c = row.createCell(0)
                        c!!.setCellValue(loand.id)

                        c = row.createCell(1)
                        c!!.setCellValue(loand.client!!.name)

                        c = row.createCell(2)
                        c!!.setCellValue(loand.date)
                        if (loand.dues!!.isNotEmpty()){
                            var count=0
                            for (document in loand.dues!!){
                                if (document.num.toString().toInt()==loand.next){
                                    c = row.createCell(3)
                                c!!.setCellValue(document.date)
                                    c = row.createCell(4)
                                    c!!.setCellValue("$"+nformat.format(document.due))
                                }
                            }
                        }
                        c = row.createCell(5)
                        c!!.setCellValue("$"+nformat.format(loand.capital))

                        c = row.createCell(6)
                        c!!.setCellValue("$"+nformat.format(loand.total))

                        c = row.createCell(7)
                        c!!.setCellValue("$"+nformat.format(loand.paidout))

                        c = row.createCell(8)
                        c!!.setCellValue(""+(loand.total-loand.paidout))

                        c = row.createCell(9)
                        c!!.setCellValue(loand.name_route)

                    }
                    sheet1!!.setColumnWidth(0, 15 * 300)
                    sheet1!!.setColumnWidth(1, 15 * 300)
                    sheet1!!.setColumnWidth(2, 15 * 300)
                    sheet1!!.setColumnWidth(3, 15 * 300)
                    sheet1!!.setColumnWidth(4, 15 * 300)
                    sheet1!!.setColumnWidth(5, 15 * 300)
                    sheet1!!.setColumnWidth(6, 15 * 300)
                    sheet1!!.setColumnWidth(7, 15 * 300)
                    sheet1!!.setColumnWidth(8, 15 * 300)
                    sheet1!!.setColumnWidth(9, 15 * 300)
                    // Create a path where we will place our List of objects on external storage
                    val file = File(Environment.getExternalStorageDirectory(), fileName)
                    var os: FileOutputStream? = null

                    try {
                        os = FileOutputStream(file)
                        wb.write(os)
                        Snackbar.make(excel,"Guardado en $file",Snackbar.LENGTH_LONG).show()
                        Log.w("FileUtils", "Writing file$file")
                        success = true
                    } catch (e: IOException) {
                        Log.w("FileUtils", "Error writing $file", e)
                    } catch (e: Exception) {
                        Log.w("FileUtils", "Failed to save file", e)
                    } finally {
                        try {
                            if (null != os)
                                os!!.close()
                        } catch (ex: Exception) {
                        }

                    }
                }else{
                    Snackbar.make(excel,"Tu base de datos esta vacia",Snackbar.LENGTH_LONG).show()

                }

            }

        return success
    }
    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }
    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }
}
