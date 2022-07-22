package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: ChatListUsers)

    @Query("DELETE from ChatListUsers WHERE uId=:id")
    suspend fun deleteAllUsers(id: String)

    @Query("SELECT * FROM ChatListUsers")
    fun getAllUsers(): LiveData<ChatListUsers>
}