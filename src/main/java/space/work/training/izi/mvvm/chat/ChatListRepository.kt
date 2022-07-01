package space.work.training.izi.mvvm.chat

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class ChatListRepository @Inject constructor(
    private val chatListFirebase: ChatListFirebase,
    private val userDao: UserDao
):CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    suspend fun notifyFirebaseDataChange(list: List<User>) {
        userDao.deleteAllUsers()
        for (img in list) {
            userDao.insert(img)
        }
    }

    fun getAllUsers(): LiveData<List<User>> {
        var list = chatListFirebase.getUsers()
        if (!list.isNullOrEmpty()) {
            launch {
                notifyFirebaseDataChange(list)
            }
        }
        return userDao.getAllUsers()
    }

    suspend fun insertUser(user: User) = userDao.insert(user)

    fun addUser(user: User) {
        //chatListFirebase.addUSer(user);
    }
}