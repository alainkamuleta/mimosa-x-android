package io.element.android.libraries.matrix.impl.auth

import android.net.Uri
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData
import timber.log.Timber
import javax.inject.Inject

class SovereignEnrollmentParser @Inject constructor() {

    /**
     * Parses a QR code string into SovereignEnrollmentData.
     * Supports both URI format (convergence://enroll) and raw JSON.
     */
    fun parse(qrContent: String): SovereignEnrollmentData? {
        return try {
            if (qrContent.startsWith("convergence://enroll")) {
                parseUri(qrContent)
            } else if (qrContent.trim().startsWith("{")) {
                // Future implementation: JSON parsing with Serialization
                null
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse Sovereign Enrollment QR Code")
            null
        }
    }

    private fun parseUri(uriString: String): SovereignEnrollmentData? {
        val uri = Uri.parse(uriString)
        val homeserverUrl = uri.getQueryParameter("hs") ?: return null
        val organizationName = uri.getQueryParameter("org") ?: "Convergence Agent"
        val oidcIssuer = uri.getQueryParameter("oidc") ?: homeserverUrl
        val region = uri.getQueryParameter("reg") ?: "Souveraine"

        return SovereignEnrollmentData(
            homeserverUrl = homeserverUrl,
            organizationName = organizationName,
            oidcIssuer = oidcIssuer,
            region = region
        )
    }
}
