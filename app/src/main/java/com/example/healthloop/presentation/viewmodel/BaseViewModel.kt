package com.example.healthloop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthloop.presentation.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState
    
    protected fun updateState(state: UiState<T>) {
        _uiState.value = state
    }
    
    protected fun launchDataLoad(block: suspend () -> T) {
        viewModelScope.launch {
            try {
                updateState(UiState.Loading)
                val result = block()
                updateState(UiState.Success(result))
            } catch (e: Exception) {
                updateState(UiState.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }
}