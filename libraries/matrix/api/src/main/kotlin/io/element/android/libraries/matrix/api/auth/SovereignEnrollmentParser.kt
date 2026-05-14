package io.element.android.libraries.matrix.api.auth

/**
 * Parser for the sovereign enrollment QR codes used in Convergence.
 * Example URI: convergence://enroll?homeserver=https://ma3x.ztn0.net&org=Ministere+Defense&region=RDC
 */
interface SovereignEnrollmentParser {
    fun parse(content: String): SovereignEnrollmentData?
}
