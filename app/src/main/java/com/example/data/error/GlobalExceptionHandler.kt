package com.example.data.error

import com.example.model.Language
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineExceptionHandler
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object GlobalExceptionHandler {

    fun createCoroutineExceptionHandler(
        contextInfo: String = "Coroutine",
        onError: (AppError) -> Unit
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            val appError = parseException(throwable, contextInfo)
            onError(appError)
        }
    }

    suspend fun <T> safeApiCall(
        contextInfo: String = "API Call",
        language: Language = Language.EN,
        onError: ((AppError) -> Unit)? = null,
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (t: Throwable) {
            val appError = parseException(t, contextInfo, language)
            onError?.invoke(appError)
            Result.failure(t)
        }
    }

    suspend fun <T> safeFirebaseCall(
        operationName: String = "Firebase Operation",
        language: Language = Language.EN,
        onError: ((AppError) -> Unit)? = null,
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (t: Throwable) {
            val appError = parseException(t, operationName, language)
            onError?.invoke(appError)
            Result.failure(t)
        }
    }

    fun parseException(
        throwable: Throwable,
        contextInfo: String = "Operation",
        language: Language = Language.EN
    ): AppError {
        val category: ErrorCategory
        val title: String
        val message: String
        val technicalDetails = "${throwable::class.java.simpleName}: ${throwable.message ?: "Unknown cause"}"

        when (throwable) {
            is UnknownHostException, is ConnectException, is FirebaseNetworkException -> {
                category = ErrorCategory.NETWORK_OFFLINE
                when (language) {
                    Language.AR -> {
                        title = "انقطاع الاتصال بالإنترنت"
                        message = "تعذر الوصول إلى الخادم السحابي. يرجى التحقق من اتصال شبكة Wi-Fi أو بيانات الهاتف."
                    }
                    Language.FR -> {
                        title = "Connexion Internet Interrompue"
                        message = "Impossible de joindre le serveur Cloud. Veuillez vérifier votre connexion réseau."
                    }
                    Language.EN -> {
                        title = "No Internet Connection"
                        message = "Unable to connect to Foodora Cloud servers. Please verify your internet or Wi-Fi connection."
                    }
                }
            }

            is SocketTimeoutException -> {
                category = ErrorCategory.NETWORK_TIMEOUT
                when (language) {
                    Language.AR -> {
                        title = "انتهت مهلة استجابة الخادم"
                        message = "استغرق الخادم وقتاً طويلاً للرد. قد يكون اتصالك بطيئاً أو أن الخادم قيد الصيانة."
                    }
                    Language.FR -> {
                        title = "Délai de Connexion Dépassé"
                        message = "Le serveur met trop de temps à répondre. Vérifiez la stabilité de votre réseau."
                    }
                    Language.EN -> {
                        title = "Connection Timed Out"
                        message = "The server took too long to respond. Please check network speed or try again shortly."
                    }
                }
            }

            is FirebaseAuthException -> {
                category = ErrorCategory.FIREBASE_AUTH
                when (language) {
                    Language.AR -> {
                        title = "خطأ في مصادقة الجلسة"
                        message = "انتهت صلاحية جلسة تسجيل الدخول أو أن بيانات الاعتماد غير صالحة. يرجى إعادة تسجيل الدخول."
                    }
                    Language.FR -> {
                        title = "Échec d'Authentification"
                        message = "Session expirée ou identifiants non valides. Veuillez vous reconnecter."
                    }
                    Language.EN -> {
                        title = "Authentication Failed"
                        message = "Your authentication session has expired or credentials are invalid. Please log in again."
                    }
                }
            }

            is FirebaseFirestoreException -> {
                when (throwable.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                        category = ErrorCategory.FIREBASE_AUTH
                        when (language) {
                            Language.AR -> {
                                title = "تم رفض الإذن السحابي"
                                message = "ليس لديك الصلاحيات الكافية لتعديل أو مزامنة هذه البيانات على Firebase Firestore."
                            }
                            Language.FR -> {
                                title = "Permission Refusée"
                                message = "Permissions insuffisantes pour synchroniser ces données sur Firestore."
                            }
                            Language.EN -> {
                                title = "Permission Denied"
                                message = "You do not have sufficient permissions to sync this data on Firebase Firestore."
                            }
                        }
                    }
                    FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> {
                        category = ErrorCategory.FIREBASE_QUOTA
                        when (language) {
                            Language.AR -> {
                                title = "تم تجاوز الحصة السحابية"
                                message = "تم بلوغ الحد الأقصى لطلبات قاعدة البيانات السحابية. يرجى الترقية أو الانتظار قليلاً."
                            }
                            Language.FR -> {
                                title = "Quota Dépassé"
                                message = "Le quota de requêtes Firestore a été atteint. Veuillez patienter."
                            }
                            Language.EN -> {
                                title = "Cloud Quota Exceeded"
                                message = "Firestore request rate quota has been reached. Please wait before retrying."
                            }
                        }
                    }
                    FirebaseFirestoreException.Code.UNAVAILABLE -> {
                        category = ErrorCategory.FIREBASE_FIRESTORE_SYNC
                        when (language) {
                            Language.AR -> {
                                title = "خدمة المزامنة غير متاحة مؤقتاً"
                                message = "قاعدة بيانات Firestore السحابية غير متاحة حالياً. يتم حفظ التغييرات محلياً مؤقتاً."
                            }
                            Language.FR -> {
                                title = "Synchronisation Indisponible"
                                message = "Le service Firestore est momentanément indisponible. Données en cache local."
                            }
                            Language.EN -> {
                                title = "Sync Service Unavailable"
                                message = "Firebase Firestore is temporarily unavailable. Changes are securely cached locally."
                            }
                        }
                    }
                    else -> {
                        category = ErrorCategory.FIREBASE_FIRESTORE_SYNC
                        when (language) {
                            Language.AR -> {
                                title = "فشل مزامنة البيانات السحابية"
                                message = "حدث خطأ أثناء مزامنة الطلبات والبيانات مع Firestore (${throwable.code.name})."
                            }
                            Language.FR -> {
                                title = "Erreur de Synchronisation Cloud"
                                message = "Échec de synchronisation avec Firestore (${throwable.code.name})."
                            }
                            Language.EN -> {
                                title = "Cloud Sync Error"
                                message = "Failed to synchronize restaurant operations with Firestore (${throwable.code.name})."
                            }
                        }
                    }
                }
            }

            is HttpException -> {
                val code = throwable.code()
                when (code) {
                    in 400..499 -> {
                        category = ErrorCategory.CLIENT_ERROR
                        when (language) {
                            Language.AR -> {
                                title = "طلب غير صالح (HTTP $code)"
                                message = "تعذر إكمال طلب الخادم بسبب خطأ في البيانات المدخلة أو انتهاء الصلاحية."
                            }
                            Language.FR -> {
                                title = "Requête Invalide (HTTP $code)"
                                message = "La requête vers l'API a été rejetée par le serveur distant."
                            }
                            Language.EN -> {
                                title = "API Request Rejected (HTTP $code)"
                                message = "The remote server rejected the API request due to invalid parameters or expired session."
                            }
                        }
                    }
                    in 500..599 -> {
                        category = ErrorCategory.SERVER_ERROR
                        when (language) {
                            Language.AR -> {
                                title = "خطأ في الخادم السحابي (HTTP $code)"
                                message = "الخادم يواجه ضغطاً أو عطلاً داخلياً مؤقتاً. جاري إعادة المحاولة تلقائياً."
                            }
                            Language.FR -> {
                                title = "Erreur Serveur (HTTP $code)"
                                message = "Le serveur rencontre une erreur interne momentanée. Réessayez."
                            }
                            Language.EN -> {
                                title = "Cloud Server Error (HTTP $code)"
                                message = "The server is experiencing internal issues. Please retry in a few moments."
                            }
                        }
                    }
                    else -> {
                        category = ErrorCategory.SERVER_ERROR
                        title = "HTTP Error $code"
                        message = throwable.message()
                    }
                }
            }

            is IOException -> {
                category = ErrorCategory.NETWORK_OFFLINE
                when (language) {
                    Language.AR -> {
                        title = "فشل في اتصال الشبكة"
                        message = "تعذر تبادل البيانات مع الشبكة الخارجية. يرجى التحقق من اتصال الجهاز."
                    }
                    Language.FR -> {
                        title = "Erreur de Communication Réseau"
                        message = "Impossible de communiquer avec le réseau. Vérifiez votre connexion."
                    }
                    Language.EN -> {
                        title = "Network Communication Failure"
                        message = "Could not communicate with the remote endpoint. Please check your connection."
                    }
                }
            }

            is FirebaseException -> {
                category = ErrorCategory.FIREBASE_FIRESTORE_SYNC
                when (language) {
                    Language.AR -> {
                        title = "خطأ في خدمات Firebase"
                        message = "واجهت عمليات Firebase السحابية استثناءً غير متوقع: ${throwable.localizedMessage ?: "فشل المزامنة"}"
                    }
                    Language.FR -> {
                        title = "Erreur Firebase"
                        message = "Une exception est survenue lors de l'opération Firebase: ${throwable.localizedMessage}"
                    }
                    Language.EN -> {
                        title = "Firebase Service Issue"
                        message = "Firebase cloud service encountered an unexpected error: ${throwable.localizedMessage}"
                    }
                }
            }

            else -> {
                category = ErrorCategory.UNKNOWN
                when (language) {
                    Language.AR -> {
                        title = "حدث خطأ غير متوقع"
                        message = throwable.localizedMessage ?: "حدث استثناء أثناء تنفيذ العملية السحابية."
                    }
                    Language.FR -> {
                        title = "Erreur Inattendue"
                        message = throwable.localizedMessage ?: "Une erreur inconnue s'est produite."
                    }
                    Language.EN -> {
                        title = "Unexpected Error"
                        message = throwable.localizedMessage ?: "An unexpected error occurred during execution."
                    }
                }
            }
        }

        return AppError(
            category = category,
            title = title,
            userMessage = message,
            technicalDetails = technicalDetails,
            contextInfo = contextInfo
        )
    }
}
