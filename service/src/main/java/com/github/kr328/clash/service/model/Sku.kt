@file:UseSerializers(UUIDSerializer::class)

package com.github.kr328.clash.service.model

import android.os.Parcel
import android.os.Parcelable
import com.github.kr328.clash.core.util.Parcelizer
import com.github.kr328.clash.service.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

@Serializable
data class Sku(
    val id: Int,
    val platformId: Int,
    val subscriptionId: Int,
    val link: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(platformId)
        parcel.writeInt(subscriptionId)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sku> {
        override fun createFromParcel(parcel: Parcel): Sku {
            return Sku(parcel)
        }

        override fun newArray(size: Int): Array<Sku?> {
            return arrayOfNulls(size)
        }
    }

}