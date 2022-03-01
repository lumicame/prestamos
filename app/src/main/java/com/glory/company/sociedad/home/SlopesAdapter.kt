package com.glory.company.sociedad.home

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
import androidx.recyclerview.widget.RecyclerView
import com.glory.company.sociedad.R
import com.glory.company.sociedad.loans.ViewLoandActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view_slopes.view.*
import java.text.DecimalFormat

class SlopesAdapter(private val slopes: ArrayList<slopes>, private val context: Context) : RecyclerView.Adapter<SlopesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_slopes, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItems(slopes[p1],context)
    }

    override fun getItemCount()=slopes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var db = FirebaseFirestore.getInstance()
        val PREFS_FILENAME = "com.company.glory.inversiones.prefs"
        val nformat = DecimalFormat("##,###,###.##")

        fun bindItems(item: slopes, context: Context) {
            var prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
            val database=prefs!!.getString("database", "")
            val type=prefs!!.getString("type", "")
            if (type=="client"){
                itemView.Btn_call.visibility=View.GONE
                itemView.charge.text = "Ver Cartulina"
                itemView.name_route_loand.visibility=View.GONE
            }

            itemView.client_address.text="#"+item.id+" - "+item.client.address

                    itemView.client_name.text= item.client.name+" #"+item.id
            if (item.pass==10000)
            itemView.client_name.setTextColor(context.resources.getColor(R.color.overlay_dark_40))
            else
                itemView.client_name.setTextColor(context.resources.getColor(R.color.red_800))
                    if (item.client.avatar=="default")
                    else
                        Picasso.get().load(item.client.avatar).into(itemView.client_avatar)

                    itemView.Btn_call.setOnClickListener {
                        val i = Intent(Intent.ACTION_DIAL)
                        i.data = Uri.parse("tel:"+item.client.tel)
                        context.startActivity(i)
                    }

            itemView.name_route_loand.text=item.route_name
            itemView.charge.setOnClickListener {
                val intent= Intent(context, ViewLoandActivity::class.java)
                intent.putExtra("id",item.id)
                context.startActivity(intent)
            }
            itemView.due_p_loand.text=""+item.slopes
            itemView.due_pass_loand.text=""+item.pass
            itemView.due_price_loand.text="$"+nformat.format(item.due)
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