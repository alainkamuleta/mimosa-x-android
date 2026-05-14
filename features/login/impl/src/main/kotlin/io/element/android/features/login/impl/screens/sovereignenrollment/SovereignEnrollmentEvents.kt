package io.element.android.features.login.impl.screens.sovereignenrollment

import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData

sealed interface SovereignEnrollmentEvents {
    data class OnSelectMethod(val method: EnrollmentMethod) : SovereignEnrollmentEvents
    data class OnQrCodeScanned(val content: String) : SovereignEnrollmentEvents
    data class OnEmailSubmit(val email: String) : SovereignEnrollmentEvents
    data class OnIdSubmit(val id: String) : SovereignEnrollmentEvents
    data object OnConfirmEnrollment : SovereignEnrollmentEvents
    data object OnBiometricAuthenticated : SovereignEnrollmentEvents
    data object OnRetryScan : SovereignEnrollmentEvents
    data object OnCancel : SovereignEnrollmentEvents
}

enum class EnrollmentMethod {
    QR_CODE, EMAIL, ID
}
