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
import space.work.training.izi.mvvm.posts.ImgDao
import space.work.training.izi.mvvm.posts.ImgDatabase
import space.work.training.izi.mvvm.posts.ImgFirebase
import space.work.training.izi.mvvm.posts.ImgRepository
import space.work.training.izi.mvvm.profile.ProfileDao
import space.work.training.izi.mvvm.profile.ProfileDatabase
import space.work.training.izi.mvvm.profile.ProfileFirebase
import space.work.training.izi.mvvm.profile.ProfileRepository
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

    @Provides
    fun provideChatListRepo(): ChatListRepository {
        return ChatListRepository()
    }

    @Provides
    fun provideImgsRepo(imgDao: ImgDao, imgFirebase: ImgFirebase): ImgRepository {
        return ImgRepository(imgDao, imgFirebase)
    }

    @Provides
    fun provideImgsFirebase(imgDao: ImgDao): ImgFirebase {
        return ImgFirebase(imgDao)
    }

    @Singleton
    @Provides
    fun provideProfileRepository(
        profileDao: ProfileDao,
        profileFirebase: ProfileFirebase
    ): ProfileRepository {
        return ProfileRepository(profileDao, profileFirebase)
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
}