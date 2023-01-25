package com.pne.pokerhand.data.repository

import android.app.Application
import com.pne.pokerhand.R
import com.pne.pokerhand.data.remote.CardsApi
import com.pne.pokerhand.domain.repository.CardsRepository

class CardsRepositoryImpl(
    private val api: CardsApi,
    private val appContext: Application
): CardsRepository {
    override suspend fun doNetworkCall() {
        println("Howdy cowboy")
    }
}
