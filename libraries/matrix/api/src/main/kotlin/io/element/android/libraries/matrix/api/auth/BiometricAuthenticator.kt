package io.element.android.libraries.matrix.api.auth

import androidx.fragment.app.FragmentActivity

/**
 * Service to handle biometric authentication (Fingerprint, FaceID) for sovereign security.
 */
interface BiometricAuthenticator {
    /**
     * Checks if the device supports biometric authentication and has at least one enrolled.
     */
    fun isBiometricAvailable(activity: FragmentActivity): Boolean

    /**
     * Triggers the biometric prompt.
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (Int, String) -> Unit
    )
}
