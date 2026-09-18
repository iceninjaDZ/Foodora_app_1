package com.example.data.error

import com.example.model.Language

enum class ErrorCategory {
    NETWORK_OFFLINE,
    NETWORK_TIMEOUT,
    FIREBASE_AUTH,
    FIREBASE_FIRESTORE_SYNC,
    FIREBASE_QUOTA,
    SERVER_ERROR,
    CLIENT_ERROR,
    UNKNOWN
}

enum class SyncStatus {
    SYNCED,
    SYNCING,
    FAILED,
    OFFLINE
}

data class AppError(
    val id: String = java.util.UUID.randomUUID().toString(),
    val category: ErrorCategory,
    val title: String,
    val userMessage: String,
    val technicalDetails: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val canRetry: Boolean = true,
    val actionLabel: String = "Retry",
    val contextInfo: String = "General"
)
