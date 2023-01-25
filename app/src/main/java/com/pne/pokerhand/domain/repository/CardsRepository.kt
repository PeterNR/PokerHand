package com.pne.pokerhand.domain.repository

interface CardsRepository {
    suspend fun doNetworkCall()
}