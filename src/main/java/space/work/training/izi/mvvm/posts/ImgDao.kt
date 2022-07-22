package space.work.training.izi.mvvm.posts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.work.training.izi.model.Img
import space.work.training.izi.mvvm.posts.newImgs.ImgNew

@Dao
interface ImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imgHome: ImgHome)

    @Query("DELETE FROM home_imgs WHERE uId=:id")
    suspend fun deleteAllImgs(id: String)

    @Query("SELECT * FROM home_imgs WHERE uid=:id")
    fun getAllImgs(id: String): LiveData<List<Img>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNew(imgNew: ImgNew)

    @Query("DELETE FROM new_imgs WHERE uId=:id")
    suspend fun deleteAllNewImgs(id: String)

    @Query("SELECT * FROM new_imgs WHERE uid=:id")
    fun getAllImgsNew(id: String): LiveData<List<ImgNew>>
}