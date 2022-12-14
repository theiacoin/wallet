package io.horizontalsystems.bankwallet.modules.swap.settings.uniswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.modules.swap.settings.SwapSettingsModule.SwapSettingsError
import io.horizontalsystems.bankwallet.modules.swap.settings.uniswap.UniswapSettingsModule.State
import io.horizontalsystems.bankwallet.modules.swap.uniswap.UniswapTradeService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class UniswapSettingsViewModel(
        private val service: UniswapSettingsService,
        private val tradeService: UniswapTradeService
) : ViewModel() {

    var buttonState by mutableStateOf(Pair(Translator.getString(R.string.SwapSettings_Apply), true))
        private set

    private val disposable = CompositeDisposable()

    init {
        service.stateObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    syncAction()
                }.let {
                    disposable.add(it)
                }
    }

    private fun syncAction() {
        when (service.state) {
            is State.Valid -> {
                buttonState = Pair(Translator.getString(R.string.SwapSettings_Apply), true)
            }
            State.Invalid -> {
                val error = service.errors.firstOrNull() ?: return
                var errorText: String? = null

                when (error) {
                    is SwapSettingsError.InvalidAddress -> {
                        errorText = Translator.getString(R.string.SwapSettings_Error_InvalidAddress)
                    }
                    is SwapSettingsError.ZeroSlippage,
                    is SwapSettingsError.InvalidSlippage -> {
                        errorText = Translator.getString(R.string.SwapSettings_Error_InvalidSlippage)
                    }
                    is SwapSettingsError.ZeroDeadline -> {
                        errorText = Translator.getString(R.string.SwapSettings_Error_InvalidDeadline)
                    }
                }

                errorText?.let {
                    buttonState = Pair(it, false)
                }
            }
        }
    }

    override fun onCleared() {
        disposable.clear()
    }

    fun onDoneClick(): Boolean {
        return when (val state = service.state) {
            is State.Valid -> {
                tradeService.tradeOptions = state.tradeOptions
                true
            }
            is State.Invalid -> {
                false
            }
        }
    }

    sealed class ActionState {
        object Enabled : ActionState()
        class Disabled(val title: String) : ActionState()
    }
}
