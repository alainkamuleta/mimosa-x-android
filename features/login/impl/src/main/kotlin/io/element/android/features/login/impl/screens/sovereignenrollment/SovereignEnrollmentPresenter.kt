package io.element.android.features.login.impl.screens.sovereignenrollment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.resolver.SovereignEnrollmentService
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SovereignEnrollmentPresenter @AssistedInject constructor(
    private val enrollmentParser: SovereignEnrollmentParser,
    private val enrollmentService: SovereignEnrollmentService,
    private val accountProviderDataSource: AccountProviderDataSource,
) : Presenter<SovereignEnrollmentState> {

    @AssistedFactory
    interface Factory {
        fun create(): SovereignEnrollmentPresenter
    }

    @Composable
    override fun present(): SovereignEnrollmentState {
        val coroutineScope = rememberCoroutineScope()
        var step by remember { mutableStateOf<EnrollmentStep>(EnrollmentStep.MethodSelection) }
        var enrollmentData by remember { mutableStateOf<io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var pollingJob by remember { mutableStateOf<Job?>(null) }

        fun handleEvent(event: SovereignEnrollmentEvents) {
            when (event) {
                is SovereignEnrollmentEvents.OnSelectMethod -> {
                    step = when (event.method) {
                        EnrollmentMethod.QR_CODE -> EnrollmentStep.Scanning
                        EnrollmentMethod.EMAIL -> EnrollmentStep.EmailInput
                        EnrollmentMethod.ID -> EnrollmentStep.IdInput
                    }
                }
                is SovereignEnrollmentEvents.OnQrCodeScanned -> {
                    val data = enrollmentParser.parse(event.content)
                    if (data != null) {
                        enrollmentData = data
                        step = EnrollmentStep.Confirming
                        errorMessage = null
                    } else {
                        errorMessage = "QR Code non reconnu ou invalide pour Convergence."
                    }
                }
                is SovereignEnrollmentEvents.OnEmailSubmit -> {
                    errorMessage = null
                    step = EnrollmentStep.Loading
                    coroutineScope.launch {
                        val success = enrollmentService.requestOtp(username = event.email.substringBefore("@"), email = event.email)
                        if (success) {
                            pollingJob?.cancel()
                            pollingJob = launch {
                                val data = enrollmentService.pollForValidation(event.email)
                                if (data != null) {
                                    enrollmentData = data
                                    step = EnrollmentStep.Confirming
                                } else {
                                    errorMessage = "Délai d'enrôlement expiré ou erreur de validation."
                                    step = EnrollmentStep.EmailInput
                                }
                            }
                        } else {
                            errorMessage = "Impossible d'envoyer le code OTP. Vérifiez votre email."
                            step = EnrollmentStep.EmailInput
                        }
                    }
                }
                is SovereignEnrollmentEvents.OnIdSubmit -> {
                    // Manual ID fallback
                    enrollmentData = SovereignEnrollmentData(
                        homeserverUrl = "https://ma3x.ztn0.net",
                        organizationName = "Configuration Manuelle",
                        oidcIssuer = "https://ma3x.ztn0.net",
                        region = "Non spécifiée"
                    )
                    step = EnrollmentStep.Confirming
                }
                SovereignEnrollmentEvents.OnConfirmEnrollment -> {
                    enrollmentData?.let {
                        step = EnrollmentStep.Securing
                    }
                }
                SovereignEnrollmentEvents.OnBiometricAuthenticated -> {
                    enrollmentData?.let { data ->
                        coroutineScope.launch {
                            accountProviderDataSource.setUrl(data.homeserverUrl)
                            step = EnrollmentStep.Loading
                        }
                    }
                }
                SovereignEnrollmentEvents.OnRetryScan -> {
                    pollingJob?.cancel()
                    step = EnrollmentStep.MethodSelection
                    enrollmentData = null
                    errorMessage = null
                }
                SovereignEnrollmentEvents.OnCancel -> {
                    pollingJob?.cancel()
                }
            }
        }

        return SovereignEnrollmentState(
            step = step,
            enrollmentData = enrollmentData,
            errorMessage = errorMessage,
            eventSink = ::handleEvent
        )
    }
}
