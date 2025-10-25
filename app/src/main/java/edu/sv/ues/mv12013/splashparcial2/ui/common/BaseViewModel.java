package edu.sv.ues.mv12013.splashparcial2.ui.common;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

public class BaseViewModel extends ViewModel {
    public final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.Idle);
    protected void setLoading() { uiState.postValue(UiState.Loading); }
    protected void setSuccess() { uiState.postValue(UiState.Success); }
    protected void setError(String message) { uiState.postValue(new UiState.Error(message)); }
}