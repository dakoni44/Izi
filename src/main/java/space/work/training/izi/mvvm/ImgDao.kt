package com.social.world.tracy.mvvm.kotlin

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(img: Img): Long

    @Update
    suspend fun update(img: Img)

    @Delete
    suspend fun delete(img: Img)

    @Query("DELETE from img_table")
    suspend fun deleteAllImgs()

    @Query("SELECT * FROM img_table")
    fun getAllImgs(): LiveData<List<Img>>
}