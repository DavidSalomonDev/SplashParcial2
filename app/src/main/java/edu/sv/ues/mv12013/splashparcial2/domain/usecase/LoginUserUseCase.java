package edu.sv.ues.mv12013.splashparcial2.domain.usecase;

import android.content.Context;
import android.content.SharedPreferences;

import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import edu.sv.ues.mv12013.splashparcial2.data.repository.AuthRepository;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;

public class LoginUserUseCase {

    private final Context app;
    private final AuthRepository repo;

    public LoginUserUseCase(Context appContext) {
        this.app = appContext.getApplicationContext();
        this.repo = new AuthRepository(this.app);
    }

    public void execute(String email, String password, boolean remember, Callback<Result<UserEntity>> cb) {
        new Thread(() -> {
            AuthRepository.Result r = repo.login(email, password, remember);
            if (r.ok) {
                SharedPreferences sp = app.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
                sp.edit().putBoolean("logged_in", true).apply();
                cb.onComplete(Result.success(null, null));
            } else {
                cb.onComplete(Result.error(r.message));
            }
        }).start();
    }
}