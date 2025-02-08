package com.example.diallerapp.model.uicreatecontact

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class PhoneModel (

    var phoneNumber: String = "",
    var phoneLabel: String = "",
    var isLabelVisible: Boolean = false

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    )

    init {
        Log.d("PhoneModel", "phoneNumber: $phoneNumber, phoneLabel: $phoneLabel")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(phoneNumber)
        parcel.writeString(phoneLabel)
        parcel.writeByte(if (isLabelVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhoneModel> {
        override fun createFromParcel(parcel: Parcel): PhoneModel {
            return PhoneModel(parcel)
        }

        override fun newArray(size: Int): Array<PhoneModel?> {
            return arrayOfNulls(size)
        }
    }
}