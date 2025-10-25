package edu.sv.ues.mv12013.splashparcial2.ui.auth.register;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;
import edu.sv.ues.mv12013.splashparcial2.domain.usecase.RegisterUserUseCase;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;

public class RegisterViewModel extends AndroidViewModel {

    private final RegisterUserUseCase useCase;
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.Idle);
    public LiveData<UiState> uiState = _uiState;

    public RegisterViewModel(@NonNull Application app) {
        super(app);
        this.useCase = new RegisterUserUseCase(app.getApplicationContext());
    }

    public void register(String email, String name, String password) {
        if (!isValidEmail(email) || !isValidPassword(password) || !isValidName(name)) {
            _uiState.postValue(new UiState.Error("Datos inválidos"));
            return;
        }
        _uiState.postValue(UiState.Loading);
        useCase.execute(email, password, name, new Callback<Result<UserEntity>>() {
            @Override
            public void onComplete(Result<UserEntity> result) {
                if (result != null && result.isSuccess()) {
                    _uiState.postValue(UiState.Success);
                } else {
                    String msg = result != null && result.error != null ? result.error : "Error en registro";
                    _uiState.postValue(new UiState.Error(msg));
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String pass) {
        return pass != null && pass.length() >= 8 && pass.matches(".*[A-Za-z].*") && pass.matches(".*\\d.*");
    }
    private boolean isValidName(String name) {
        return name != null && name.trim().length() >= 7;
    }
}