package com.lucasginard.airelibre.modules.data

import com.lucasginard.airelibre.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkService{
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideQuoteApiClient(retrofit: Retrofit):APIHome{
        return retrofit.create(APIHome::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceGitHub{
    @Named("retrofit_gitHub")
    @Singleton
    @Provides
    fun provideGitHub(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/repos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideReposContributors(@Named("retrofit_gitHub") retrofit: Retrofit): APIGitHub {
        return retrofit.create(APIGitHub::class.java)
    }
}