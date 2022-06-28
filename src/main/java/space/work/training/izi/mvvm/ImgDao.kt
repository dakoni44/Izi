package space.work.training.izi.mvvm

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

    @Query("DELETE from Img")
    suspend fun deleteAllImgs()

    @Query("SELECT * FROM Img")
    fun getAllImgs(): LiveData<List<Img>>
}