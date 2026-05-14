package io.element.android.features.login.impl.screens.sovereignenrollment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.matrix.api.auth.BiometricAuthenticator

@ContributesNode(AppScope::class)
class SovereignEnrollmentNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: SovereignEnrollmentPresenter.Factory,
    private val biometricAuthenticator: BiometricAuthenticator,
) : Node(buildContext, plugins = plugins) {

    interface Callback : Plugin {
        fun onEnrollmentConfirmed()
        fun onBackClick()
    }

    private val presenter = presenterFactory.create()
    private val callback = callback<Callback>()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val activity = LocalContext.current as? FragmentActivity

        SovereignEnrollmentView(
            state = state,
            onBackClick = callback::onBackClick,
            modifier = modifier
        )

        // Logic to trigger biometric prompt when event is received
        // Note: In a real app, we'd use a dedicated event sink or a side-effect
        if (state.step == EnrollmentStep.Securing && activity != null) {
            if (biometricAuthenticator.isBiometricAvailable(activity)) {
                biometricAuthenticator.authenticate(
                    activity = activity,
                    title = "Accès Sécurisé Mimosa",
                    subtitle = "Veuillez vous authentifier pour finaliser l'enrôlement souverain.",
                    onSuccess = {
                        state.eventSink(SovereignEnrollmentEvents.OnBiometricAuthenticated)
                    },
                    onError = { _, _ ->
                        // Handle error
                    }
                )
            } else {
                // Biometrics not available, skip for now or show warning
                state.eventSink(SovereignEnrollmentEvents.OnBiometricAuthenticated)
            }
        }

        if (state.step == EnrollmentStep.Loading) {
            callback.onEnrollmentConfirmed()
        }
    }
}
