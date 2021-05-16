package fr.outadoc.woolly.common.feature.auth

import fr.outadoc.mastodonk.client.MastodonClient
import fr.outadoc.woolly.common.feature.preference.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val scope: CoroutineScope,
    private val authProxyRepository: AuthProxyRepository,
    private val preferenceRepository: PreferenceRepository
) {
    private val _authState = MutableStateFlow(
        preferenceRepository.authInfo.value.toAuthState()
    )
    val authState: StateFlow<AuthState> = _authState

    fun onDomainTextChanged(domain: String) {
        val currentState = authState.value as? AuthState.Disconnected ?: return
        _authState.value = currentState.copy(domain = domain)
    }

    fun onSubmitDomain() {
        val currentState = authState.value as? AuthState.Disconnected ?: return
        if (currentState.loading) return

        val domain = currentState.domain.trim()

        val client = MastodonClient {
            this.domain = domain
        }

        _authState.value = currentState.copy(loading = true)

        scope.launch(Dispatchers.IO) {
            _authState.value = try {
                client.instance.getInstanceInfo()
                AuthState.InstanceSelected(
                    domain = domain,
                    authorizeUrl = authProxyRepository.getAuthorizeUrl(domain)
                )
            } catch (e: Throwable) {
                currentState.copy(loading = false, error = e)
            }
        }
    }

    fun onAuthCodeReceived(code: String) {
        val currentState = authState.value as? AuthState.InstanceSelected ?: return
        if (currentState.loading) return

        _authState.value = currentState.copy(loading = true)

        scope.launch(Dispatchers.IO) {
            _authState.value = try {
                val token = authProxyRepository.getToken(currentState.domain, code)
                val authInfo = AuthInfo(currentState.domain, token)

                // Save auth info to disk
                preferenceRepository.authInfo.value = authInfo

                // We're authenticated!
                AuthState.Authenticated(authInfo)
            } catch (e: Throwable) {
                currentState.copy(error = e)
            }
        }
    }

    fun onBackPressed() {
        val currentState = authState.value as? AuthState.InstanceSelected ?: return
        _authState.value = AuthState.Disconnected(domain = currentState.domain)
    }

    fun logout() {
        preferenceRepository.authInfo.value = null
        _authState.value = AuthState.Disconnected()
    }

    private fun AuthInfo?.toAuthState(): AuthState {
        return if (this == null) AuthState.Disconnected()
        else AuthState.Authenticated(this)
    }
}
