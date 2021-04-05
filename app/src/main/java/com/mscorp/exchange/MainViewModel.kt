package com.mscorp.exchange

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mscorp.exchange.api.RatesOverview
import kotlinx.coroutines.launch


class MainViewModel(context: Context) : ViewModel() {

    private val repository = MainRepository(context)
    private var loginResponse: LiveData<List<RatesOverview>>

    init {
        loginResponse = repository.getLiveDAtat()!!
        viewModelScope.launch {
            val res = repository.getData()!!
            loginResponse = res
        }
    }

    fun getData() = loginResponse
}

