package io.element.android.features.login.impl.resolver

import io.element.android.appconfig.AuthenticationConfig
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData
import io.element.android.libraries.network.RetrofitFactory
import kotlinx.coroutines.delay
import timber.log.Timber
import dev.zacsweers.metro.Inject

class SovereignEnrollmentService @Inject constructor(
    private val retrofitFactory: RetrofitFactory,
) {
    private val api by lazy {
        retrofitFactory.create(AuthenticationConfig.MATRIX_ORG_URL).create(EnrollmentApi::class.java)
    }

    suspend fun requestOtp(username: String, email: String): Boolean {
        return try {
            val response = api.generateOtp(OtpRequest(username, email))
            response.status == "success"
        } catch (e: Exception) {
            Timber.e(e, "Failed to request OTP for enrollment")
            false
        }
    }

    suspend fun pollForValidation(email: String): SovereignEnrollmentData? {
        val maxAttempts = 60 // 5 minutes (5s delay)
        var attempts = 0
        while (attempts < maxAttempts) {
            try {
                val response = api.checkEnrollmentStatus(email)
                if (response.status == "validated" && response.config != null) {
                    return SovereignEnrollmentData(
                        homeserverUrl = response.config.homeserver,
                        organizationName = response.config.organization ?: "Convergence Sovereign",
                        oidcIssuer = response.config.homeserver,
                        region = response.config.region ?: "RDC"
                    )
                } else if (response.status == "expired") {
                    return null
                }
            } catch (e: Exception) {
                Timber.e(e, "Error polling for enrollment status")
            }
            delay(5000)
            attempts++
        }
        return null
    }
}
