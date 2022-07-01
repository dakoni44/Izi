package space.work.training.izi.mvvm.chat

import androidx.lifecycle.LiveData
import androidx.room.*

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