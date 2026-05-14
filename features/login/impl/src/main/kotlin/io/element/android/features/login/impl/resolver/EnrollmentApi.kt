package io.element.android.features.login.impl.resolver

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EnrollmentApi {
    @POST("api/otp/generate")
    suspend fun generateOtp(@Body request: OtpRequest): OtpResponse

    @GET("api/otp/status")
    suspend fun checkEnrollmentStatus(
        @Query("email") email: String,
        @Query("context") context: String = "mimosa_enrollment"
    ): EnrollmentStatusResponse
}

@Serializable
data class OtpRequest(
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("context") val context: String = "mimosa_enrollment"
)

@Serializable
data class OtpResponse(
    @SerialName("status") val status: String,
    @SerialName("message") val message: String? = null
)

@Serializable
data class EnrollmentStatusResponse(
    @SerialName("status") val status: String, // "pending", "validated", "expired"
    @SerialName("message") val message: String? = null,
    @SerialName("config") val config: EnrollmentConfig? = null
)

@Serializable
data class EnrollmentConfig(
    @SerialName("homeserver") val homeserver: String,
    @SerialName("organization") val organization: String? = null,
    @SerialName("region") val region: String? = null
)
