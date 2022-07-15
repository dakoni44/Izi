package space.work.training.izi.mvvm.posts.room_v_firebase

import androidx.lifecycle.LiveData
import androidx.room.*
import space.work.training.izi.mvvm.posts.Img

@Dao
interface ImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(img: Img): Long

    @Update
    suspend fun update(img: Img)

    @Delete
    suspend fun delete(img: Img)

    @Query("DELETE from home_imgs")
    suspend fun deleteAllImgs()

    @Query("SELECT * FROM home_imgs")
    fun getAllImgs(): LiveData<List<Img>>
}