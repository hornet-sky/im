package com.example.im.model

import android.os.Parcel
import android.os.Parcelable

class ContactsItem(val firstLetter: String, val account: String, val avatar: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstLetter)
        parcel.writeString(account)
        parcel.writeString(avatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactsItem> {
        override fun createFromParcel(parcel: Parcel): ContactsItem {
            return ContactsItem(parcel)
        }

        override fun newArray(size: Int): Array<ContactsItem?> {
            return arrayOfNulls(size)
        }
    }

}