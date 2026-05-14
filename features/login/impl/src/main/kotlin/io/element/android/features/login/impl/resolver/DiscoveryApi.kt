package io.element.android.features.login.impl.resolver

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscoveryApi {
    @GET("api/discovery/domains")
    suspend fun getDomains(): DiscoveryResponse<List<DomainData>>

    @GET("api/discovery/resolve")
    suspend fun resolveHomeServer(
        @Query("username") username: String?,
        @Query("domain") domain: String
    ): DiscoveryResponse<HomeServerResolution>
}

@Serializable
data class DiscoveryResponse<T>(
    @SerialName("status") val status: String,
    @SerialName("domains") val domains: T? = null,
    @SerialName("homeserver_url") val homeserverUrl: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("domain") val domain: String? = null,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class DomainData(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("icon") val icon: String? = null,
)

@Serializable
data class HomeServerResolution(
    @SerialName("homeserver_url") val homeserverUrl: String,
    @SerialName("username") val username: String? = null,
    @SerialName("domain") val domain: String,
)
