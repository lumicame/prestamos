package com.glory.company.sociedad.loans

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class due : Parcelable {
    internal var date: String? = null
    internal var num: Int? = null
    internal var due: Int? = null
    internal var state: Boolean? = null

    constructor() {
        this.num = num
        this.date = date
        this.due = due
        this.state = state
    }
    constructor(num:Int, date:String,due:Int,state:Boolean) {
        this.num = num
        this.date = date
        this.due = due
        this.state = state
    }

    private constructor(`in`: Parcel) {
        num = `in`.readInt()
        date = `in`.readString()
        due= `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(date)
        num?.let { out.writeInt(it) }
        due?.let { out.writeInt(it) }
    }

    companion object {

        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<due> = object : Parcelable.Creator<due> {
            override fun createFromParcel(`in`: Parcel): due {
                return due(`in`)
            }

            override fun newArray(size: Int): Array<due?> {
                return arrayOfNulls(size)
            }
        }
    }

}


