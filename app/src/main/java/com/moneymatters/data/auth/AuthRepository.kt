package com.moneymatters.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val college: String = "",
    val incomeRange: String = "",
    val isOnboardingComplete: Boolean = false
)

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val tokenManager: TokenManager,
    private val rateLimiter: RateLimiter
) {
    suspend fun loginWithEmail(email: String, password: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val rateLimitStatus = rateLimiter.checkLoginRateLimit()
        if (rateLimitStatus.isLockedOut) {
            val minutes = (rateLimitStatus.lockOutRemainingMillis / (60 * 1000)).coerceAtLeast(1)
            return@withContext Result.failure(Exception("Too many failed attempts. Locked out for $minutes minutes."))
        }

        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User missing")

            val tokenResult = user.getIdToken(true).await()
            val tokenString = tokenResult.token ?: ""
            tokenManager.saveTokens(
                jwtToken = tokenString,
                refreshToken = tokenString,
                userId = user.uid
            )

            rateLimiter.resetAttempts()
            val profile = getUserProfile(user.uid) ?: UserProfile(uid = user.uid, email = email)
            Result.success(profile)
        } catch (e: Exception) {
            rateLimiter.recordFailedAttempt()
            Result.failure(e)
        }
    }

    suspend fun signupWithEmail(name: String, email: String, password: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Signup failed")

            val profile = UserProfile(
                uid = user.uid,
                name = name,
                email = email,
                isOnboardingComplete = false
            )

            firestore.collection("users").document(user.uid).set(profile).await()

            val tokenResult = user.getIdToken(true).await()
            val tokenString = tokenResult.token ?: ""
            tokenManager.saveTokens(
                jwtToken = tokenString,
                refreshToken = tokenString,
                userId = user.uid
            )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user ?: throw Exception("Google auth failed")

            var profile = getUserProfile(user.uid)
            if (profile == null) {
                profile = UserProfile(
                    uid = user.uid,
                    name = user.displayName ?: "",
                    email = user.email ?: "",
                    isOnboardingComplete = false
                )
                firestore.collection("users").document(user.uid).set(profile).await()
            }

            val tokenResult = user.getIdToken(true).await()
            val tokenString = tokenResult.token ?: ""
            tokenManager.saveTokens(
                jwtToken = tokenString,
                refreshToken = tokenString,
                userId = user.uid
            )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginAnonymously(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user ?: throw Exception("Anonymous login failed")

            val profile = UserProfile(
                uid = user.uid,
                name = "Guest User",
                email = "",
                isOnboardingComplete = false
            )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!rateLimiter.canRequestPasswordReset()) {
            return@withContext Result.failure(Exception("Please wait 5 minutes before requesting another reset link."))
        }

        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveProfileSetup(college: String, incomeRange: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = firebaseAuth.currentUser
            ?: return@withContext Result.failure(Exception("Not logged in"))
        try {
            val updates = mapOf(
                "college" to college,
                "incomeRange" to incomeRange,
                "isOnboardingComplete" to true
            )
            firestore.collection("users").document(user.uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        firebaseAuth.signOut()
        tokenManager.clearSession()
    }
}

