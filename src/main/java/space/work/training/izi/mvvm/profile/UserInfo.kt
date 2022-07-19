package space.work.training.izi.mvvm.profile

import androidx.room.*

@Entity(tableName = "user_info")
data class UserInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "uId")
    var uId: String = "",
    @ColumnInfo(name = "uList")
    var uList : ArrayList<String>,
    @ColumnInfo(name = "views")
    var views: String = "",
    @ColumnInfo(name = "friends")
    var friends: String = "",
    @ColumnInfo(name = "likes")
    var likes: String = "",
    @ColumnInfo(name = "dislikes")
    var dislikes: String = "",
    @ColumnInfo(name = "posts")
    var posts: String = ""
)
