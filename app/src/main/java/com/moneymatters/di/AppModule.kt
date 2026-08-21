package com.moneymatters.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.moneymatters.core.data.SettingsStorage
import com.moneymatters.core.data.SettingsStorageImpl
import com.revenuecat.purchases.Purchases
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSettingsStorage(impl: SettingsStorageImpl): SettingsStorage

    companion object {
        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }

        @Provides
        @Singleton
        fun providePurchases(): Purchases? {
            return try {
                if (Purchases.isConfigured) {
                    Purchases.sharedInstance
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
