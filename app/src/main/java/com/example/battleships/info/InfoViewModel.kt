package com.example.battleships.info


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.services.Mode
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.launch

class InfoViewModel(private val useCases: UseCases): ViewModel() {
    private var _serverInfo by mutableStateOf<Result<ServerInfo>?>(null)
    val rankings: Result<ServerInfo>?
        get() = _serverInfo

    fun loadServerInfo(forcedRefresh: Boolean = false) {
        viewModelScope.launch {
            _serverInfo =
                try {
                    Result.success(useCases.fetchServerInfo(
                        if (forcedRefresh) Mode.FORCE_REMOTE
                        else Mode.AUTO
                    ))
                } catch (e: Exception) {
                    Log.e("InfoViewModel", "Error loading info", e)
                    Result.failure(e)
                }
        }
    }
}