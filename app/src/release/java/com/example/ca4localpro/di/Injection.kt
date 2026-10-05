package com.example.ca4localpro.di

import com.example.ca4localpro.data.auth.AuthDataSource
import com.example.ca4localpro.data.auth.RemoteAuthDataSource
import com.example.ca4localpro.data.repository.UserRepository
import com.example.ca4localpro.data.remote.RemoteUserRepository
import com.example.ca4localpro.data.repository.ServiceRepository
import com.example.ca4localpro.data.remote.RemoteServiceRepository

object Injection {
    fun provideAuthDataSource(): AuthDataSource {
        return RemoteAuthDataSource()
    }
    
    fun provideUserRepository(): UserRepository {
        return RemoteUserRepository()
    }

    fun provideServiceRepository(): ServiceRepository {
        return RemoteServiceRepository()
    }
}