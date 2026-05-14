package io.element.android.features.login.impl.screens.sovereignenrollment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.auth.BiometricAuthenticator
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentParser
import io.element.android.libraries.qrcode.QrCodeCameraView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SovereignEnrollmentView(
    state: SovereignEnrollmentState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeaderFooterPage(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
            )
        },
        header = {
            SovereignEnrollmentHeader(state = state)
        },
        footer = {
            SovereignEnrollmentFooter(state = state, onBackClick = onBackClick)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.step) {
                EnrollmentStep.MethodSelection -> {
                    SovereignMethodSelection(onMethodSelected = { state.eventSink(SovereignEnrollmentEvents.OnSelectMethod(it)) })
                }
                EnrollmentStep.Scanning -> {
                    QrCodeCameraView(
                        onScanQrCode = { bytes ->
                            state.eventSink(SovereignEnrollmentEvents.OnQrCodeScanned(String(bytes)))
                        },
                        renderPreview = true,
                        modifier = Modifier
                            .size(280.dp)
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Scannez votre QR Code Convergence",
                        style = ElementTheme.typography.fontBodyLgMedium,
                        color = ElementTheme.colors.textSecondary
                    )
                }
                EnrollmentStep.EmailInput -> {
                    SovereignEmailInput(onEmailSubmit = { state.eventSink(SovereignEnrollmentEvents.OnEmailSubmit(it)) })
                }
                EnrollmentStep.IdInput -> {
                    SovereignIdInput(onIdSubmit = { state.eventSink(SovereignEnrollmentEvents.OnIdSubmit(it)) })
                }
                EnrollmentStep.Confirming -> {
                    state.enrollmentData?.let { data ->
                        SovereignConfirmationContent(data = data)
                    }
                }
                EnrollmentStep.Securing -> {
                    SovereignSecuringContent()
                }
                EnrollmentStep.Loading -> {
                    // Show a progress indicator
                }
            }
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = ElementTheme.colors.textCriticalPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SovereignEnrollmentHeader(state: SovereignEnrollmentState) {
    val title = when (state.step) {
        EnrollmentStep.MethodSelection -> "Enrôlement Mimosa"
        EnrollmentStep.Scanning -> "Scanner QR Code"
        EnrollmentStep.EmailInput -> "Vérification Email"
        EnrollmentStep.IdInput -> "Identifiant Manuel"
        EnrollmentStep.Confirming -> "Confirmation d'Organisation"
        EnrollmentStep.Securing -> "Sécurisation Biométrique"
        EnrollmentStep.Loading -> "Initialisation..."
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Icon(
            imageVector = when (state.step) {
                EnrollmentStep.Confirming -> Icons.Default.VerifiedUser
                EnrollmentStep.EmailInput -> Icons.Default.Email
                EnrollmentStep.IdInput -> Icons.Default.Badge
                else -> Icons.Default.QrCodeScanner
            },
            contentDescription = null,
            tint = ElementTheme.colors.iconPrimary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = ElementTheme.typography.fontHeadingMdBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SovereignConfirmationContent(data: SovereignEnrollmentData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue, ${data.organizationName}",
            style = ElementTheme.typography.fontHeadingMdBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Région : ${data.region}",
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Votre appareil va être configuré pour utiliser l'infrastructure de votre organisation sur ${data.homeserverUrl}.",
            style = ElementTheme.typography.fontBodyMdRegular,
            textAlign = TextAlign.Center,
            color = ElementTheme.colors.textPrimary
        )
    }
}

@Composable
private fun SovereignSecuringContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = ElementTheme.colors.textSuccessPrimary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Protection Biométrique",
            style = ElementTheme.typography.fontHeadingMdBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pour protéger vos échanges sensibles, cette application nécessite une authentification biométrique (Empreinte ou Visage) à chaque ouverture.",
            style = ElementTheme.typography.fontBodyMdRegular,
            textAlign = TextAlign.Center,
            color = ElementTheme.colors.textPrimary
        )
    }
}

@Composable
private fun SovereignMethodSelection(onMethodSelected: (EnrollmentMethod) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choisissez votre méthode d'enrôlement souveraine :",
            style = ElementTheme.typography.fontBodyLgMedium,
            textAlign = TextAlign.Center,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            text = "Scanner mon QR Code",
            leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
            onClick = { onMethodSelected(EnrollmentMethod.QR_CODE) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            text = "Utiliser mon Email Convergence",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            onClick = { onMethodSelected(EnrollmentMethod.EMAIL) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            text = "Saisir mon Identifiant",
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            onClick = { onMethodSelected(EnrollmentMethod.ID) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SovereignEmailInput(onEmailSubmit: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Entrez votre adresse email Convergence (@ztn0.net) pour recevoir votre code d'enrôlement à 8 chiffres.",
            style = ElementTheme.typography.fontBodyMdRegular,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Convergence") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            text = "Recevoir le Code",
            onClick = { onEmailSubmit(email) },
            enabled = email.isNotEmpty() && email.contains("@"),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SovereignIdInput(onIdSubmit: (String) -> Unit) {
    var id by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Entrez votre identifiant Mimosa pour une configuration manuelle.",
            style = ElementTheme.typography.fontBodyMdRegular,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("Identifiant Mimosa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            text = "Continuer",
            onClick = { onIdSubmit(id) },
            enabled = id.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SovereignEnrollmentFooter(
    state: SovereignEnrollmentState,
    onBackClick: () -> Unit
) {
    val activity = androidx.compose.ui.platform.LocalContext.current as? FragmentActivity
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        if (state.step == EnrollmentStep.Confirming) {
            Button(
                text = "Confirmer et Se Connecter",
                onClick = { state.eventSink(SovereignEnrollmentEvents.OnConfirmEnrollment) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = "Réessayer",
                onClick = { state.eventSink(SovereignEnrollmentEvents.OnRetryScan) },
                modifier = Modifier.fillMaxWidth()
            )
        } else if (state.step == EnrollmentStep.Securing) {
            Button(
                text = "Activer la Biométrie",
                onClick = {
                    state.eventSink(SovereignEnrollmentEvents.OnBiometricAuthenticated)
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else if (state.step != EnrollmentStep.MethodSelection) {
            TextButton(
                text = "Retour aux méthodes",
                onClick = { state.eventSink(SovereignEnrollmentEvents.OnRetryScan) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            TextButton(
                text = "Annuler",
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
