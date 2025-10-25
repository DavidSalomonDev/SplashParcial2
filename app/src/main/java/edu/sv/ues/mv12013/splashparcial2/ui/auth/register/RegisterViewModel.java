package edu.sv.ues.mv12013.splashparcial2.ui.auth.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import edu.sv.ues.mv12013.splashparcial2.common.SecurityUtils;
import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;
import edu.sv.ues.mv12013.splashparcial2.domain.usecase.RegisterUserUseCase;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;

public class RegisterViewModel extends AndroidViewModel {

    private final RegisterUserUseCase useCase;
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.Idle);
    public LiveData<UiState> uiState = _uiState;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        this.useCase = new RegisterUserUseCase(application.getApplicationContext());
    }

    public void register(String email, String name, String password) {
        if (!SecurityUtils.isValidEmail(email)) { _uiState.postValue(new UiState.Error("Correo inválido")); return; }
        if (!SecurityUtils.isValidName(name)) { _uiState.postValue(new UiState.Error("Nombre mínimo 7 caracteres")); return; }
        if (!SecurityUtils.isValidPassword(password)) { _uiState.postValue(new UiState.Error("Contraseña >=8 y alfanumérica")); return; }

        _uiState.postValue(UiState.Loading);
        useCase.execute(email, name, password, false, new Callback<Result<Void>>() {
            @Override
            public void onComplete(Result<Void> result) {
                if (result != null && result.isSuccess()) {
                    _uiState.postValue(UiState.Success);
                } else {
                    _uiState.postValue(new UiState.Error(result != null ? result.error : "Error"));
                }
            }
        });
    }
}