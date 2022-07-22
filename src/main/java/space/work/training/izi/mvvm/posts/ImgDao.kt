package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.work.training.izi.mvvm.posts.newImgs.ImgNew

@Dao
interface ImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imgs: ArrayList<Img>)

    @Query("DELETE from home_imgs")
    suspend fun deleteAllImgs()

    @Query("SELECT * FROM home_imgs")
    fun getAllImgs(): LiveData<List<Img>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNew(img: ArrayList<ImgNew>)

    @Query("DELETE from new_imgs")
    suspend fun deleteAllImgsNew()

    @Query("SELECT * FROM new_imgs")
    fun getAllImgsNew(): LiveData<List<ImgNew>>
}