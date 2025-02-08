package com.example.diallerapp.model.uicreatecontact

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class EmailModel(
    var email: String = "",
    var emailLabel: String = "",
    var isLabelVisible: Boolean = false
) :Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",   // Handle null values safely
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    ) {
    }

    init {
        Log.d("EmailModel", "EmailModel initialized with email: $email, emailLabel: $emailLabel")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(emailLabel)
        parcel.writeByte(if (isLabelVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmailModel> {
        override fun createFromParcel(parcel: Parcel): EmailModel {
            return EmailModel(parcel)
        }

        override fun newArray(size: Int): Array<EmailModel?> {
            return arrayOfNulls(size)
        }
    }
}