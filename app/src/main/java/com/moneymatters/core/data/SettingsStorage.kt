package com.moneymatters.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsStorage {
    fun isOnboardingCompleted(): Flow<Boolean>
    fun isReducedMotionEnabled(): Flow<Boolean>
}

@Singleton
class SettingsStorageImpl @Inject constructor() : SettingsStorage {
    override fun isOnboardingCompleted(): Flow<Boolean> = flowOf(false)
    override fun isReducedMotionEnabled(): Flow<Boolean> = flowOf(false)
}
