package edu.sv.ues.mv12013.splashparcial2.ui.auth.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;
import edu.sv.ues.mv12013.splashparcial2.domain.usecase.LoginUserUseCase;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;

public class LoginViewModel extends AndroidViewModel {

    private final LoginUserUseCase loginUseCase;

    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.Idle);
    public LiveData<UiState> uiState = _uiState;

    private final MutableLiveData<Boolean> _loggedIn = new MutableLiveData<>(false);
    public LiveData<Boolean> loggedIn = _loggedIn;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.loginUseCase = new LoginUserUseCase(application.getApplicationContext());
    }

    public void login(String email, String password, boolean remember) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            _uiState.postValue(new UiState.Error("Correo o contraseña inválidos"));
            return;
        }
        _uiState.postValue(UiState.Loading);

        loginUseCase.execute(email, password, remember, new Callback<Result<UserEntity>>() {
            @Override
            public void onComplete(Result<UserEntity> result) {
                if (result != null && result.isSuccess()) {
                    _uiState.postValue(UiState.Success);
                    _loggedIn.postValue(true);
                } else {
                    String msg = result != null && result.error != null ? result.error : "Fallo de autenticación";
                    _uiState.postValue(new UiState.Error(msg));
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String pass) {
        return pass != null && pass.length() >= 8; // mínimo del enunciado
    }
}