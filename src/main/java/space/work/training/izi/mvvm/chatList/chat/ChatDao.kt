package space.work.training.izi.mvvm.chatList.chat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.work.training.izi.model.Chat

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatUser(chatUser: ChatUser): Long

    @Query("DELETE FROM chat_user WHERE uId=:id")
    suspend fun deleteChatUser(id: String)

    @Query("SELECT * FROM chat_user WHERE uId=:id")
    fun getChatUser(id: String): LiveData<ChatUser>

    @Query("UPDATE chat_user SET img=:img,username=:username WHERE uId=:id")
    suspend fun updateChatUser(img: String, username: String, id: String)

    @Query("UPDATE chat_user SET chatList=:list WHERE uId=:id")
    suspend fun updateChatList(list: ArrayList<Chat>, id: String)

    @Query("SELECT EXISTS (SELECT 1 FROM chat_user WHERE uid = :id)")
    fun exists(id: String): Boolean
}