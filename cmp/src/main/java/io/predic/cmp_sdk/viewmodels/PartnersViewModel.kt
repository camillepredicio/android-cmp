package io.predic.cmp_sdk.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.predic.cmp_sdk.models.Partner
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.viewmodels.SettingsViewModel.Companion.partnerRecyclerViewList

internal class PartnersViewModel : ViewModel() {

    companion object {
        fun configPartnersList(enabled: Boolean = false): List<Partner> {
            val list = mutableListOf<Partner>()

            partnerRecyclerViewList?.forEach { partner ->
                list.add(Partner(partner.key, partner.name, partner.text, enabled))
            }
            return list.toList()
        }
    }

    val partnersSwitchState: MutableLiveData<Constants.SwitchStateStatus> by lazy {
        MutableLiveData<Constants.SwitchStateStatus>()
    }

    /**
     * Used to change state of partner.
     *
     * @param isChecked Define wished state of partner i.e. checked/unchecked.
     */
    fun onSwitchCheckedChange(isChecked: Boolean) {
        if (isChecked) {
            partnersSwitchState.value = Constants.SwitchStateStatus.SwitchChecked
        } else {
            partnersSwitchState.value = Constants.SwitchStateStatus.SwitchUnchecked
        }
    }
}

