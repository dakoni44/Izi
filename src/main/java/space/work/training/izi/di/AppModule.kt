package space.work.training.izi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import space.work.training.izi.mvvm.chat.*
import space.work.training.izi.mvvm.posts.ImgRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideChatListRepo(): ChatListRepository {
        return ChatListRepository()
    }

    @Provides
    fun provideImgsRepo(): ImgRepository {
        return ImgRepository()
    }
}