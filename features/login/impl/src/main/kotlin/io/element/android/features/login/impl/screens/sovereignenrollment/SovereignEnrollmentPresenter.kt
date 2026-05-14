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
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentParser
import kotlinx.coroutines.launch

class SovereignEnrollmentPresenter @AssistedInject constructor(
    private val enrollmentParser: SovereignEnrollmentParser,
    private val accountProviderDataSource: AccountProviderDataSource,
) : Presenter<SovereignEnrollmentState> {

    @AssistedFactory
    interface Factory {
        fun create(): SovereignEnrollmentPresenter
    }

    @Composable
    override fun present(): SovereignEnrollmentState {
        val coroutineScope = rememberCoroutineScope()
        var step by remember { mutableStateOf<EnrollmentStep>(EnrollmentStep.Scanning) }
        var enrollmentData by remember { mutableStateOf<io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        fun handleEvent(event: SovereignEnrollmentEvents) {
            when (event) {
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
                    step = EnrollmentStep.Scanning
                    enrollmentData = null
                    errorMessage = null
                }
                SovereignEnrollmentEvents.OnCancel -> {
                    // Handled by the Node
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
