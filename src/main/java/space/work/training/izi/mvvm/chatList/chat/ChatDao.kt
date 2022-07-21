package space.work.training.izi.mvvm.chatList.chat

import androidx.lifecycle.LiveData
import androidx.room.*
import space.work.training.izi.model.Chat
import space.work.training.izi.mvvm.profile.UserInfo

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatUser(chatUser: ChatUser): Long

    @Query("DELETE FROM chat_user WHERE uId=:id")
    suspend fun deleteChatUser(id:String)

    @Query("SELECT * FROM chat_user WHERE uId=:id")
    fun getChatUser(id:String): LiveData<ChatUser>

    @Query("SELECT EXISTS (SELECT 1 FROM chat_user WHERE uid = :id)")
    fun exists(id: String): Boolean
}