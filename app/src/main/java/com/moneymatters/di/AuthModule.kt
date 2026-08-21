package com.moneymatters.di

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moneymatters.data.auth.AuthRepository
import com.moneymatters.data.auth.RateLimiter
import com.moneymatters.data.auth.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("AuthModule", "FirebaseAuth unavailable: ${e.message}")
            // Return default instance or handle gracefully
            try {
                FirebaseAuth.getInstance()
            } catch (ex: Exception) {
                // If options missing, initialize dummy FirebaseApp to avoid crash
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:1234567890:android:abcdef123456")
                    .setApiKey("AIzaSyDummyKeyForAppStartupToNotCrash")
                    .setProjectId("money-matters-app")
                    .build()
                FirebaseApp.initializeApp(context, options)
                FirebaseAuth.getInstance()
            }
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(@ApplicationContext context: Context): FirebaseFirestore {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("AuthModule", "FirebaseFirestore unavailable: ${e.message}")
            try {
                FirebaseFirestore.getInstance()
            } catch (ex: Exception) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:1234567890:android:abcdef123456")
                    .setApiKey("AIzaSyDummyKeyForAppStartupToNotCrash")
                    .setProjectId("money-matters-app")
                    .build()
                FirebaseApp.initializeApp(context, options)
                FirebaseFirestore.getInstance()
            }
        }
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_SERVER_CLIENT_ID")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideRateLimiter(): RateLimiter = RateLimiter()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        tokenManager: TokenManager,
        rateLimiter: RateLimiter
    ): AuthRepository {
        return AuthRepository(firebaseAuth, firestore, tokenManager, rateLimiter)
    }
}
