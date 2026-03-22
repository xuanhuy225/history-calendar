package com.example.historycalendar.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.historycalendar.core.AppInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val appInitializer: AppInitializer,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isReady = MutableStateFlow(savedStateHandle.get<Boolean>("is_ready") == true)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    init {
        if (!_isReady.value) {
            viewModelScope.launch {
                appInitializer.initialize()
                _isReady.value = true
                savedStateHandle["is_ready"] = true
            }
        }
    }
}
