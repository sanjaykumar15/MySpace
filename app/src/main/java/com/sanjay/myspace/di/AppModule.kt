package com.sanjay.myspace.di

import android.app.Application
import android.content.Context
import com.sanjay.myspace.helper.DataStoreHelper
import com.sanjay.myspace.helper.FirebaseHelper
import com.sanjay.myspace.helper.RealmHelper
import com.sanjay.myspace.utils.Constants
import com.sanjay.myspace.utils.InternetObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Singleton
    @Provides
    fun providesRealm(context: Context): Realm {
        Realm.init(context)
        val realmConfiguration =
            RealmConfiguration.Builder()
                .name(Constants.RealmCons.DB_NAME)
                .deleteRealmIfMigrationNeeded()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        return Realm.getDefaultInstance()
    }

    @Singleton
    @Provides
    fun providesRealmHelper(realm: Realm): RealmHelper {
        return RealmHelper(realm)
    }

    @Singleton
    @Provides
    fun providesInternetObserver(context: Context): InternetObserver {
        return InternetObserver(context)
    }

    @Singleton
    @Provides
    fun providesFirebaseHelper(): FirebaseHelper {
        return FirebaseHelper()
    }

    @Singleton
    @Provides
    fun providesDatastoreHelper(context: Context): DataStoreHelper {
        return DataStoreHelper(context)
    }
}