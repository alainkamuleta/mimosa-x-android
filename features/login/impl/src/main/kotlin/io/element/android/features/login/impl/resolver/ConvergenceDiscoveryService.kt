package io.element.android.features.login.impl.resolver

import dev.zacsweers.metro.Inject
import io.element.android.appconfig.AuthenticationConfig
import io.element.android.libraries.network.RetrofitFactory
import timber.log.Timber

@Inject
class ConvergenceDiscoveryService(
    private val retrofitFactory: RetrofitFactory,
) {
    private val api by lazy {
        retrofitFactory.create(AuthenticationConfig.MATRIX_ORG_URL).create(DiscoveryApi::class.java)
    }

    suspend fun getDomains(): List<DomainData> = try {
        val response = api.getDomains()
        if (response.status == "success") {
            response.domains ?: emptyList()
        } else {
            Timber.w("Discovery API returned error: ${response.message}")
            emptyList()
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to fetch domains from discovery API")
        emptyList()
    }

    suspend fun resolveHomeServer(username: String?, domain: String): String? = try {
        val response = api.resolveHomeServer(username, domain)
        if (response.status == "success") {
            response.homeserverUrl
        } else {
            Timber.w("Resolution API returned error: ${response.message}")
            null
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to resolve homeserver for $domain")
        null
    }
}
