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
    suspend fun getDomains(): List<DomainData> {
        return try {
            api.getDomains().domains ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun resolve(username: String?, domain: String): HomeserverData? {
        return try {
            val response = api.resolveHomeServer(username, domain)
            response.homeserverUrl?.let { url ->
                HomeserverData(
                    homeserverUrl = url,
                    homeserverDomain = domain,
                    isSovereign = true
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
