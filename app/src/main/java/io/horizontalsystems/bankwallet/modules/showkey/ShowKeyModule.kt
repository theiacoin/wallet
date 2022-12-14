package io.horizontalsystems.bankwallet.modules.showkey

import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.Account
import kotlinx.parcelize.Parcelize

object ShowKeyModule {
    const val ACCOUNT = "account"

    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShowKeyViewModel(account, App.pinComponent, App.evmBlockchainManager) as T
        }
    }

    fun prepareParams(account: Account) = bundleOf(ACCOUNT to account)

    @Parcelize
    data class PrivateKey(val blockchain: String, val value: String) : Parcelable

    enum class ViewState{
        Warning, Key
    }

    data class WordNumbered(val word: String, val number: Int)

}
