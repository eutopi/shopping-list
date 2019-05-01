package us.ait.shoppinglist.data

import android.arch.persistence.room.*

@Dao
interface TobuyDAO {

    @Query("Select * from tobuy")
    fun getAllTobuy(): List<Tobuy>

    @Insert
    fun insertTobuy(tobuy: Tobuy) : Long

    @Delete
    fun deleteTobuy(vararg tobuy: Tobuy)

    @Query("DELETE FROM tobuy")
    fun deleteAll()

    @Update
    fun updateTobuy(tobuy: Tobuy)
}