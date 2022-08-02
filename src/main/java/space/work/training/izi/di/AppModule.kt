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
import space.work.training.izi.mvvm.chatList.ChatListDatabase
import space.work.training.izi.mvvm.chatList.chat.ChatDatabase
import space.work.training.izi.mvvm.posts.ImgDatabase
import space.work.training.izi.mvvm.profile.ProfileDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFDb(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideImgDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ImgDatabase::class.java,
        "img_database"
    ).fallbackToDestructiveMigration().build()

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
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideProfileDao(db: ProfileDatabase) = db.profileDao()

    @Singleton
    @Provides
    fun provideChatListDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ChatListDatabase::class.java,
        "chatList_database"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideUserDao(db: ChatListDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideChatDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ChatDatabase::class.java,
        "chat_database"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideChatDao(db: ChatDatabase) = db.chatDao()
}