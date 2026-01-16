package com.example.myapplication.model.domain

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator


data class Character(
    val id: Int,
    val name: String,
    val age: String,
    val imageUrl: String,
    val description: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?:""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(age)
        parcel.writeString(imageUrl)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Creator<Character> {
        override fun createFromParcel(parcel: Parcel): Character = Character(parcel)
        override fun newArray(size: Int): Array<Character?> = arrayOfNulls(size)
    }
}
