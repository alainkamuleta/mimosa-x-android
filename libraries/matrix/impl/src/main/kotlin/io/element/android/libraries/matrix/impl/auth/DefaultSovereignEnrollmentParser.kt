/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import android.net.Uri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentData
import io.element.android.libraries.matrix.api.auth.SovereignEnrollmentParser
import javax.inject.Inject

@ContributesBinding(AppScope::class)
class DefaultSovereignEnrollmentParser @Inject constructor() : SovereignEnrollmentParser {
    override fun parse(content: String): SovereignEnrollmentData? {
        val uri = Uri.parse(content)
        if (uri.scheme != "convergence" || uri.host != "enroll") return null

        val homeserverUrl = uri.getQueryParameter("homeserver") ?: return null
        val oidcIssuer = uri.getQueryParameter("issuer") ?: homeserverUrl
        val organizationName = uri.getQueryParameter("org") ?: "Organisation Inconnue"
        val region = uri.getQueryParameter("region") ?: "Non spécifiée"

        return SovereignEnrollmentData(
            homeserverUrl = homeserverUrl,
            organizationName = organizationName,
            oidcIssuer = oidcIssuer,
            region = region
        )
    }
}
