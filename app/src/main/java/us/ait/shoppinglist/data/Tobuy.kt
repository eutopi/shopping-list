package us.ait.shoppinglist.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tobuy")
data class Tobuy (
    @PrimaryKey(autoGenerate = true) var tobuyId: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "category") var category: Int,
    @ColumnInfo(name = "status") var status: Boolean

): Serializable