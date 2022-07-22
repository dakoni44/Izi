package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.work.training.izi.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: ArrayList<User>)

    @Query("DELETE from User")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>
}