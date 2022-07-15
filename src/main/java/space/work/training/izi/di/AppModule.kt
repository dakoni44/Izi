package space.work.training.izi.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import space.work.training.izi.mvvm.chatList.ChatListRepository
import space.work.training.izi.mvvm.posts.ImgRepository
import space.work.training.izi.mvvm.posts.room_v_firebase.ImgDao
import space.work.training.izi.mvvm.posts.room_v_firebase.ImgDatabase
import space.work.training.izi.mvvm.profile.ProfileDao
import space.work.training.izi.mvvm.profile.ProfileDatabase
import space.work.training.izi.mvvm.profile.ProfileRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideChatListRepo(): ChatListRepository {
        return ChatListRepository()
    }

    @Provides
    fun provideImgsRepo(imgDao: ImgDao): ImgRepository {
        return ImgRepository(imgDao)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(
        profileDao: ProfileDao
    ): ProfileRepository {
        return ProfileRepository(profileDao)
    }

    @Singleton
    @Provides
    fun provideImgDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ImgDatabase::class.java,
        "img_database"
    ).build()

    @Singleton
    @Provides
    fun provideImgDao(db: ImgDatabase) = db.imgDao()

    @Singleton
    @Provides
    fun provideProfileDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ProfileDatabase::class.java,
        "profile_database"
    ).build()

    @Singleton
    @Provides
    fun provideProfileDao(db: ProfileDatabase) = db.profileDao()

    @Singleton
    @Provides
    @Named("Users")
    fun provideUserRef() = FirebaseDatabase.getInstance().getReference("Users")

    @Singleton
    @Provides
    @Named("Tokens")
    fun provideTokenRef() = FirebaseDatabase.getInstance().getReference("Tokens")

    @Singleton
    @Provides
    @Named("Posts")
    fun providePostRef() = FirebaseDatabase.getInstance().getReference("Posts")

    @Singleton
    @Provides
    @Named("Likes")
    fun provideLikesRef() = FirebaseDatabase.getInstance().getReference("Likes")

    @Singleton
    @Provides
    @Named("Groups")
    fun provideGroupsRef() = FirebaseDatabase.getInstance().getReference("Groups")

    @Singleton
    @Provides
    @Named("Friends")
    fun provideFriendsRef() = FirebaseDatabase.getInstance().getReference("Friends")

    @Singleton
    @Provides
    @Named("FriendRequests")
    fun provideFriendRequestsRef() = FirebaseDatabase.getInstance().getReference("FriendRequests")

    @Singleton
    @Provides
    @Named("Dislikes")
    fun provideDislikesRef() = FirebaseDatabase.getInstance().getReference("Dislikes")

    @Singleton
    @Provides
    @Named("Chats")
    fun provideChatsRef() = FirebaseDatabase.getInstance().getReference("Chats")

    @Singleton
    @Provides
    @Named("Chatlist")
    fun provideChatListRef() = FirebaseDatabase.getInstance().getReference("Chatlist")

    @Provides
    @Named("User")
    fun provideFirebaseUser() = FirebaseAuth.getInstance().currentUser
}