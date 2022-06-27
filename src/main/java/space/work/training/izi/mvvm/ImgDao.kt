package space.work.training.izi.mvvm

import androidx.lifecycle.LiveData
import androidx.room.*
import com.social.world.tracy.mvvm.kotlin.Img

@Dao
interface ImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(img: Img): Long

    @Update
     fun update(img: Img)

    @Delete
     fun delete(img: Img)

    @Query("DELETE from Img")
     fun deleteAllImgs()

    @Query("SELECT * FROM Img")
    fun getAllImgs(): LiveData<List<Img>>
}