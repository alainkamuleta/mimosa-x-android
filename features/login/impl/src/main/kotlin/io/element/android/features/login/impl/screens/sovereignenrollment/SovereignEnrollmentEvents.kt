package io.element.android.features.login.impl.screens.sovereignenrollment

import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData

sealed interface SovereignEnrollmentEvents {
    data class OnQrCodeScanned(val content: String) : SovereignEnrollmentEvents
    data object OnConfirmEnrollment : SovereignEnrollmentEvents
    data object OnBiometricAuthenticated : SovereignEnrollmentEvents
    data object OnRetryScan : SovereignEnrollmentEvents
    data object OnCancel : SovereignEnrollmentEvents
}
