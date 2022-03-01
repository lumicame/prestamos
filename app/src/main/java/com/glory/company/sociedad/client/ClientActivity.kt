package com.glory.company.sociedad.client

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.glory.company.sociedad.Adapters.ClientAdapter
import com.glory.company.sociedad.Models.User
import com.glory.company.sociedad.R
import com.glory.company.sociedad.Retrofit.ApiServices
import com.glory.company.sociedad.Retrofit.ApiUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ClientActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
    val SELECT_FILE=123
    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var lists = ArrayList<User>()
    var listsSearch = ArrayList<User>()
    var mAPIService: ApiServices? = null

    companion object{
        var activity: Activity? =null
        var update=false
        var user: User? =null
        var delete=false
        var indice=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        mAPIService= ApiUtils.getAPIService()
        activity=this
        getLists()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.hasFixedSize()
        recycler.adapter= ClientAdapter(lists,this)
        fab_add.setOnClickListener {
            createAddClientDialogo().show()
        }
        back.setOnClickListener {
            onBackPressed()
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
    }

    fun search(){
        info_result.text=getString(R.string.searching)
        info_result.visibility=View.VISIBLE
        if (!search.text.isNullOrEmpty()){
            if (lists.size>0){
                listsSearch.clear()
                for (document in lists){
                    if (document.name!!.toLowerCase().contains(search.text.toString().toLowerCase())
                        || document.nit!!.toLowerCase().contains(search.text.toString().toLowerCase())
                        || document.tel!!.toLowerCase().contains(search.text.toString().toLowerCase())){
                        listsSearch.add(
                           document
                        )
                    }
                }
                if (listsSearch.size>0){
                    info_result.visibility=View.GONE
                    try {
                        recycler.adapter = ClientAdapter(listsSearch, this)
                    }catch (e:Exception){}

                }else{
                    info_result.text=getString(R.string.no_result)
                    info_result.visibility=View.VISIBLE
                }
            }
        }else{
            try{
                if (lists.size>0){
                    info_result.visibility=View.GONE
                    recycler.adapter = ClientAdapter(lists, this)
                }
                else{
                    info_result.text=getString(R.string.no_result)
                    info_result.visibility=View.VISIBLE
                    recycler.adapter = ClientAdapter(lists, this)
                }

            }catch (e:Exception){}
        }

    }

    override fun toString(): String {
        if (update){
        try {
            recycler.adapter!!.notifyItemChanged(indice)
            update=false
        }catch (e:Exception){}
        }
        if (delete){
            try {
                getLists()
                delete=false
            }catch (e:Exception){}
        }
        return super<AppCompatActivity>.toString()
    }

    fun getLists() {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val type=prefs.getString("type","")
        val store_id=prefs.getInt("store_id",0)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage(getString(R.string.load))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        val data=HashMap<String,Any>()
        data["store"]=store_id
        mAPIService!!.getClients(data)!!.enqueue(object : Callback<List<User>?> {
            override fun onFailure(call: Call<List<User>?>, t: Throwable) {
                Toast.makeText(this@ClientActivity,t.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<User>?>, response: retrofit2.Response<List<User>?>) {
                if (response.isSuccessful){
                    lists.clear()
                    for (client in response.body()!!){
                        lists.add(client)
                    }
                    if (lists.isNotEmpty()){
                        info_result.visibility=View.GONE
                        recycler.adapter!!.notifyDataSetChanged()
                    }else{
                        info_result.text=getString(R.string.no_result)
                        info_result.visibility=View.VISIBLE
                        //progressDialog.dismiss()
                    }
                }
            }


        })



    }
    var photo: ImageView? =null
    var select=false
    var imageEncoded:String = "";
    var imagesEncodedList: ArrayList<Bitmap>? = null;
    fun createAddClientDialogo(): AlertDialog {
        val prefs = getSharedPreferences(PREFS_FILENAME, 0)
        val store_id=prefs.getInt("store_id",0)
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
        photo=v.findViewById(R.id.product_add_photo) as ImageView
        imagesEncodedList= ArrayList()
        val product_add_rotate = v.findViewById(R.id.product_add_rotate) as ImageView
        product_add_rotate.setOnClickListener {
            select=false
            photo!!.setImageResource(R.drawable.avatar)
            imagesEncodedList!!.clear()
        }
        photo!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                SELECT_FILE
            )
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.wait))
        progressDialog.setMessage(getString(R.string.saving))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false)
        title.text ="Clientes"

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
            val client = User()
            client.store_id=store_id
            client.name = name.text.toString()
            client.tel= tel.text.toString()
            client.address = addrres.text.toString()
            client.nit = nit.text.toString()
            if (imagesEncodedList!!.isNotEmpty()) {
                val sdf = SimpleDateFormat("dd-M-yyyy_hh:mm:ss")
                val currentDate = sdf.format(Date())
                client.avatar = "clients/$currentDate.jpg"
            } else {
                client.avatar = "default"
            }
            client.type = "cli"
            client.password = tel.text.toString()
            client.points = 0
            client.dues = 0
            client.latitude = "0"
            client.longitude = "0"
            if (imagesEncodedList!!.isNotEmpty()) {
                var spaceRef =
                    storageRef.child( client.avatar )
                val baos = ByteArrayOutputStream()
                imagesEncodedList!!.first().compress(Bitmap.CompressFormat.JPEG, 80, baos)
                val data = baos.toByteArray()
                var uploadTask = spaceRef.putBytes(data)
                uploadTask.continueWithTask() {task->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    spaceRef.downloadUrl
                }.addOnCompleteListener {task->
                    client.avatar =task.result.toString()
                    mAPIService!!.addClient(client)!!.enqueue(object : Callback<User?> {
                        override fun onFailure(call: Call<User?>, t: Throwable) {
                            Toast.makeText(this@ClientActivity,t.message,Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<User?>, response: retrofit2.Response<User?>) {
                            if (response.isSuccessful){
                                name.text.clear()
                                addrres.text.clear()
                                tel.text.clear()
                                nit.text.clear()
                                photo!!.setImageResource(R.drawable.avatar)
                                getLists()
                                progressDialog.dismiss()
                                dialog.dismiss()
                                Snackbar.make(fab_add, R.string.registerexisclient, Snackbar.LENGTH_LONG)
                                    .setAction("OK", null).show()
                            }
                        }


                    })

                }
            } else {
                mAPIService!!.addClient(client)!!.enqueue(object : Callback<User?> {
                    override fun onFailure(call: Call<User?>, t: Throwable) {
                        Toast.makeText(this@ClientActivity,t.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<User?>, response: retrofit2.Response<User?>) {
                        if (response.isSuccessful){
                            name.text.clear()
                            addrres.text.clear()
                            tel.text.clear()
                            nit.text.clear()
                            photo!!.setImageResource(R.drawable.avatar)
                            getLists()
                            progressDialog.dismiss()
                            dialog.dismiss()
                            Snackbar.make(fab_add, R.string.registerexisclient, Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show()
                        }
                    }


                })
            }

        }
        return dialog
    }

    private fun  resize(image: Bitmap, maxWidth:Int, maxHeight:Int): Bitmap {
        var image_=image
        if (maxHeight > 0 && maxWidth > 0) {

            val width = image.width
            val height = image.height
            if (width<maxWidth&&height<maxHeight)
                return image_
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth;
            var finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt();
            } else {
                finalHeight =  (maxWidth.toFloat() / ratioBitmap).toInt();
            }
            image_ = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            return image_;
        } else {
            return image_;
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        val selectedImage: Uri?

        when (requestCode) {
            SELECT_FILE -> if (resultCode == Activity.RESULT_OK) {
                if (imageReturnedIntent!!.data!=null) {
                    selectedImage = imageReturnedIntent!!.data
                    imageEncoded  = selectedImage.toString()
                    imagesEncodedList!!.clear()
                    var imageStream: InputStream? = null
                    try {
                        imageStream = contentResolver.openInputStream(selectedImage!!)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                    val bmporigi = BitmapFactory.decodeStream(imageStream)
                    Log.d("TAMAÑO ORIGINAL",bmporigi.byteCount.toString()+" : "+bmporigi.width+" - "+bmporigi.height)
                    imagesEncodedList!!.add(resize(bmporigi,800,600))
                    photo!!.setImageBitmap(resize(bmporigi,800,600))

                    Log.d("TAMAÑO",resize(bmporigi,800,600).byteCount.toString()+" : "+resize(bmporigi,800,600).width+" - "+resize(bmporigi,800,600).height)
                    /*
                val selectedPath = selectedImage!!.getPath()
                if (requestCode == SELECT_FILE) {*
                    if (selectedPath != null) {
                        select=true
                        var imageStream: InputStream? = null
                        try {
                            imageStream = contentResolver.openInputStream(selectedImage!!)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }

                        // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                        val bmporigi = BitmapFactory.decodeStream(imageStream)
                        Log.d("TAMAÑO ORIGINAL",bmporigi.byteCount.toString()+" : "+bmporigi.width+" - "+bmporigi.height)

                        product_add_photo.setImageBitmap(resize(bmporigi,800,600))
                        Log.d("TAMAÑO",resize(bmporigi,800,600).byteCount.toString()+" : "+resize(bmporigi,800,600).width+" - "+resize(bmporigi,800,600).height)
                        // Ponemos nuestro bitm ap en un ImageView que tengamos en la vista

                    }
                }*/
            }
        }
    }
}
}
