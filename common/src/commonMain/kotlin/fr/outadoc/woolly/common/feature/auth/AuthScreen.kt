package fr.outadoc.woolly.common.feature.auth

import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.statekeeper.Parcelize

sealed class AuthScreen : Parcelable {

    @Parcelize
    object AuthDomainSelect : AuthScreen()

    @Parcelize
    data class AuthCodeInput(val domain: String) : AuthScreen()
}
