package space.work.training.izi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import space.work.training.izi.mvvm.chat.*

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideChatListRepo(): ChatListRepository {
        return ChatListRepository()
    }
}