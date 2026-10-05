package com.example.ca4localpro.di

import com.example.ca4localpro.data.auth.AuthDataSource
import com.example.ca4localpro.data.auth.TemporaryAuthDataSource
import com.example.ca4localpro.data.repository.UserRepository
import com.example.ca4localpro.data.remote.RemoteUserRepository
import com.example.ca4localpro.data.repository.ServiceRepository
import com.example.ca4localpro.data.remote.RemoteServiceRepository

object Injection {
    fun provideAuthDataSource(): AuthDataSource {
        return TemporaryAuthDataSource()
    }

    fun provideUserRepository(): UserRepository {
        return RemoteUserRepository() // Even in debug we connect to remote DB
    }

    fun provideServiceRepository(): ServiceRepository {
        return RemoteServiceRepository()
    }
}