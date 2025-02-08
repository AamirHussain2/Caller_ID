package com.example.diallerapp.model.uicreatecontact


import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class AddressModel(
    var address: String = "",
    var addressLabel: String = "",
    var isLabelVisible: Boolean = false
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    init {
        Log.d("AddressModel", "AddressModel initialized with address: $address, addressLabel: $addressLabel")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(addressLabel)
        parcel.writeByte(if (isLabelVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressModel> {
        override fun createFromParcel(parcel: Parcel): AddressModel {
            return AddressModel(parcel)
        }

        override fun newArray(size: Int): Array<AddressModel?> {
            return arrayOfNulls(size)
        }
    }
}