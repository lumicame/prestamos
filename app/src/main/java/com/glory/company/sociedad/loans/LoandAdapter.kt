package com.glory.company.sociedad.loans

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view_loands.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class LoandAdapter(private val loands: ArrayList<loand>, private val context: Context) : RecyclerView.Adapter<LoandAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_loands, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(loands[p1],context,p1)
    }

    override fun getItemCount()=loands.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var db = FirebaseFirestore.getInstance()
        val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
        val nformat = DecimalFormat("##,###,###.##")
        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference
        fun bindItems(item: loand, context: Context,position:Int) {

            val sdf = SimpleDateFormat("yyyy-M-d")
            val sdf2 = SimpleDateFormat("dd/MM/yyyy")
            val currentDateandTime = sdf.format(Calendar.getInstance().time)
            var prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            itemView.charge.setOnClickListener {
                try {
                    val intent= Intent(context, ViewLoandActivity::class.java)
                    intent.putExtra("id",item.id)
                    context.startActivity(intent)
                }catch (e:Exception){}
            }
            itemView.num_count.text=""+(position+1)
            itemView.client_address.text="#"+item.id+" - " + item.client!!.address
            var previous = 0
            var dias: Long = 0
            for (document in item.dues!!) {
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

            }
            if (previous > 0) {
                if (dias <= 0)
                    itemView.client_name.setTextColor(context.resources.getColor(R.color.red_800))
                else
                    itemView.client_name.setTextColor(context.resources.getColor(R.color.red_800))
            }
            else
                itemView.client_name.setTextColor(context.resources.getColor(R.color.grey_40))
            itemView.client_name.text= item.client!!.name
            itemView.nit_client_loand.text=item.client!!.nit
            itemView.tel_client_loand.text=item.client!!.tel
            if (item.client!!.avatar!="")
                Picasso.get().load(item.client!!.avatar).placeholder(R.drawable.progress_animation ).into(itemView.client_avatar)

            itemView.Btn_call.setOnClickListener {
                val i = Intent(Intent.ACTION_DIAL)
                i.data = Uri.parse("tel:"+item.client!!.tel)
                context.startActivity(i)
            }
            if (item.dues!!.isNotEmpty()){
                var count=0
                for (document in item.dues!!){
                    if (document.num.toString().toInt()==item.next)
                        itemView.date_next_loand.text=document.date

                    if (!document.state!!)
                        count++
                }
                itemView.due_p_loand.text=""+count
            }
            itemView.name_route_loand.text=item.name_route
            itemView.date_initial_loand.text=item.date
            itemView.type_loand.text=item.type

        }

        fun enviaMensajeWhatsApp(context: Context, tel:String) {
            val pm= context.packageManager
            try {
                val waIntent =  Intent("android.intent.action.MAIN");
                waIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation");
                waIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("+57$tel")+"@s.whatsapp.net");
                context.startActivity(Intent.createChooser(waIntent, "Compartir con:"));
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(context, "WhatsApp no esta instalado!", Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }
}