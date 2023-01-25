package com.pne.pokerhand.data.remote

import retrofit2.http.GET

interface CardsApi {

    @GET("test")
    suspend fun doNetworkCall()
}