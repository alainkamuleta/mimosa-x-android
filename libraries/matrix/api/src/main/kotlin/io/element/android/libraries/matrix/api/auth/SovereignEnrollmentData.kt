package io.element.android.libraries.matrix.api.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data required to enroll a device into a sovereign organization within the Convergence network.
 */
@Parcelize
data class SovereignEnrollmentData(
    val homeserverUrl: String,
    val organizationName: String,
    val oidcIssuer: String,
    val region: String,
    val environment: String = "Production"
) : Parcelable
