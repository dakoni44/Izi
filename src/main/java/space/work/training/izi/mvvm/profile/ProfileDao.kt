package space.work.training.izi.mvvm.profile

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileImgs(img: ProfileImg): Long

    @Update
    suspend fun update(img: ProfileImg)

    @Delete
    suspend fun delete(img: ProfileImg)

    @Query("DELETE from profile_imgs")
    suspend fun deleteAllImgs()

    @Query("SELECT * FROM profile_imgs")
    fun getAllImgs(): LiveData<List<ProfileImg>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUSerInfo(userInfo: UserInfo): Long

    @Query("DELETE from user_info")
    suspend fun deleteUSerInfo()

    @Query("SELECT * FROM user_info")
    fun getUserInfo(): LiveData<UserInfo>

    @Query("UPDATE user_info SET uList=:uList")
    suspend fun updateUList(uList: ArrayList<String>)

    @Query("UPDATE user_info SET views=:views")
    suspend fun updateViews(views: String)

    @Query("UPDATE user_info SET friends=:friends")
    suspend fun updateFriends(friends: String)

    @Query("UPDATE user_info SET likes=:likes")
    suspend fun updateLikes(likes: String)

    @Query("UPDATE user_info SET dislikes=:dislikes")
    suspend fun updateDislikes(dislikes: String)

    @Query("UPDATE user_info SET posts=:posts")
    suspend fun updatePosts(posts: String)

/*    @Query("SELECT views FROM user_info")
    fun getViews(): LiveData<String>

    @Query("SELECT friends FROM user_info")
    fun getFriends(): LiveData<String>

    @Query("SELECT likes FROM user_info")
    fun getLikes(): LiveData<String>

    @Query("SELECT dislikes FROM user_info")
    fun getDislikes(): LiveData<String>

    @Query("SELECT posts FROM user_info")
    fun getPosts(): LiveData<String>*/

}