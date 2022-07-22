package space.work.training.izi.mvvm.profile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import space.work.training.izi.model.Img

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUSerInfo(userInfo: UserInfo): Long

    @Query("DELETE from user_info")
    suspend fun deleteUSerInfo()

    @Query("SELECT * FROM user_info WHERE uId=:id")
    fun getUserInfo(id: String): LiveData<UserInfo>

    @Query("SELECT EXISTS (SELECT 1 FROM user_info WHERE uid = :id)")
    fun exists(id: String): Boolean

    @Query("UPDATE user_info SET uList=:uList WHERE uId=:id")
    suspend fun updateUList(uList: ArrayList<String>, id: String)

    @Query("UPDATE user_info SET profileImgs=:profileImgs WHERE uId=:id")
    suspend fun updatePImgs(profileImgs: ArrayList<Img>, id: String)

    @Query("UPDATE user_info SET views=:views WHERE uId=:id")
    suspend fun updateViews(views: String, id: String)

    @Query("UPDATE user_info SET friends=:friends WHERE uId=:id")
    suspend fun updateFriends(friends: String, id: String)

    @Query("UPDATE user_info SET likes=:likes WHERE uId=:id")
    suspend fun updateLikes(likes: String, id: String)

    @Query("UPDATE user_info SET dislikes=:dislikes WHERE uId=:id")
    suspend fun updateDislikes(dislikes: String, id: String)

    @Query("UPDATE user_info SET posts=:posts WHERE uId=:id")
    suspend fun updatePosts(posts: String, id: String)

}