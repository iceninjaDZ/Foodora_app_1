package com.example

import com.example.data.error.ErrorCategory
import com.example.data.error.GlobalExceptionHandler
import com.example.model.Language
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GlobalExceptionHandlerTest {

    @Test
    fun parseException_unknownHost_returnsNetworkOffline() {
        val exception = UnknownHostException("Unable to resolve host")
        val error = GlobalExceptionHandler.parseException(exception, "Test", Language.EN)

        assertEquals(ErrorCategory.NETWORK_OFFLINE, error.category)
        assertTrue(error.userMessage.contains("No Internet Connection", ignoreCase = true) || error.userMessage.contains("connect", ignoreCase = true))
    }

    @Test
    fun parseException_socketTimeout_returnsNetworkTimeout() {
        val exception = SocketTimeoutException("Read timed out")
        val error = GlobalExceptionHandler.parseException(exception, "Sync Orders", Language.EN)

        assertEquals(ErrorCategory.NETWORK_TIMEOUT, error.category)
        assertTrue(error.canRetry)
    }

    @Test
    fun parseException_firebaseAuth_returnsFirebaseAuth() {
        val exception = FirebaseAuthException("ERROR_INVALID_CUSTOM_TOKEN", "Token expired")
        val error = GlobalExceptionHandler.parseException(exception, "Login", Language.AR)

        assertEquals(ErrorCategory.FIREBASE_AUTH, error.category)
        assertTrue(error.title.contains("مصادقة") || error.userMessage.contains("تسجيل الدخول"))
    }

    @Test
    fun parseException_firestorePermissionDenied_returnsFirebaseAuthOrPermission() {
        val exception = FirebaseFirestoreException(
            "Access denied",
            FirebaseFirestoreException.Code.PERMISSION_DENIED
        )
        val error = GlobalExceptionHandler.parseException(exception, "Firestore Save", Language.EN)

        assertEquals(ErrorCategory.FIREBASE_AUTH, error.category)
        assertTrue(error.userMessage.contains("permissions", ignoreCase = true))
    }

    @Test
    fun parseException_firestoreResourceExhausted_returnsQuota() {
        val exception = FirebaseFirestoreException(
            "Quota exceeded",
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED
        )
        val error = GlobalExceptionHandler.parseException(exception, "Firestore Query", Language.EN)

        assertEquals(ErrorCategory.FIREBASE_QUOTA, error.category)
    }

    @Test
    fun safeFirebaseCall_onSuccess_returnsResult() = runBlocking {
        var errorCalled = false
        val result = GlobalExceptionHandler.safeFirebaseCall(
            operationName = "Test Success",
            onError = { errorCalled = true }
        ) {
            "success_payload"
        }

        assertTrue(result.isSuccess)
        assertEquals("success_payload", result.getOrNull())
        assertFalse(errorCalled)
    }

    @Test
    fun safeFirebaseCall_onFailure_catchesAndInvokesError() = runBlocking {
        var errorInvoked = false
        val result = GlobalExceptionHandler.safeFirebaseCall<String>(
            operationName = "Test Failure",
            onError = { appError ->
                errorInvoked = true
                assertEquals(ErrorCategory.NETWORK_OFFLINE, appError.category)
            }
        ) {
            throw FirebaseNetworkException("Network connection lost")
        }

        assertTrue(result.isFailure)
        assertTrue(errorInvoked)
    }
}
