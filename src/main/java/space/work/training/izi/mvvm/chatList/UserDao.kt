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

    @Query("DELETE from chatList_users WHERE uId=:id")
    suspend fun deleteAllUsers(id: String)

    @Query("SELECT * FROM chatList_users WHERE uId=:id")
    fun getAllUsers(id: String): LiveData<ChatListUsers>
}