package com.example.diallerapp.model.uicreatecontact

import android.os.Parcel
import android.os.Parcelable

data class BirthdayModel(
    var birthdayDatePicker: String = "",
    var birthdayLabel: String = "",
    var isLabelVisible: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(birthdayDatePicker)
        parcel.writeString(birthdayLabel)
        parcel.writeByte(if (isLabelVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BirthdayModel> {
        override fun createFromParcel(parcel: Parcel): BirthdayModel {
            return BirthdayModel(parcel)
        }

        override fun newArray(size: Int): Array<BirthdayModel?> {
            return arrayOfNulls(size)
        }
    }
}
