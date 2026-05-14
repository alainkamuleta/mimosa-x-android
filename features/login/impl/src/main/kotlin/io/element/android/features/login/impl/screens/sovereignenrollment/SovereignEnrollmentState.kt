package io.element.android.features.login.impl.screens.sovereignenrollment

import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData

data class SovereignEnrollmentState(
    val step: EnrollmentStep,
    val enrollmentData: SovereignEnrollmentData?,
    val errorMessage: String?,
    val eventSink: (SovereignEnrollmentEvents) -> Unit
)

sealed interface EnrollmentStep {
    data object MethodSelection : EnrollmentStep
    data object Scanning : EnrollmentStep
    data object EmailInput : EnrollmentStep
    data object IdInput : EnrollmentStep
    data object Confirming : EnrollmentStep
    data object Securing : EnrollmentStep
    data object Loading : EnrollmentStep
}
