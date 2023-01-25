package com.pne.pokerhand.di

import android.app.Application
import com.pne.pokerhand.data.remote.CardsApi
import com.pne.pokerhand.data.repository.CardsRepositoryImpl
import com.pne.pokerhand.domain.repository.CardsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCardsApi(): CardsApi {
        return Retrofit.Builder()
            .baseUrl("https://test.com")
            .build()
            .create(CardsApi::class.java)
    }

    @Provides
    @Singleton
    fun providesCardsRepository(
        api: CardsApi,
        app: Application,
    ): CardsRepository {
        return CardsRepositoryImpl(api, app)
    }
}