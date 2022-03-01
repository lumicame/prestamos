package com.glory.company.sociedad.Entry

import android.os.Parcel
import android.os.Parcelable

class Egress : Parcelable {
    internal var name: String? = null
    internal var date: String? = null
    internal var total: Double? = null
    internal var user_nit: String? = null
    internal var loand: String? = null

    constructor(name: String, date: String, total: Double, user_nit: String,loand:String) {
        this.name = name
        this.date = date
        this.total = total
        this.user_nit = user_nit
        this.loand = loand
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        date = `in`.readString()
        total = `in`.readDouble()
        user_nit = `in`.readString()
        loand = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(name)
        out.writeString(date)
        total?.let { out.writeDouble(it) }
        out.writeString(user_nit)
        out.writeString(loand)
    }

    companion object CREATOR : Parcelable.Creator<Egress> {
        override fun createFromParcel(parcel: Parcel): Egress {
            return Egress(parcel)
        }

        override fun newArray(size: Int): Array<Egress?> {
            return arrayOfNulls(size)
        }
    }
}