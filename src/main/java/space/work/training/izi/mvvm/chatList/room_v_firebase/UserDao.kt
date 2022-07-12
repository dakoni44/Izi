package space.work.training.izi.mvvm.chatList.room_v_firebase

import androidx.lifecycle.LiveData
import androidx.room.*
import space.work.training.izi.mvvm.chatList.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE from User")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>
}