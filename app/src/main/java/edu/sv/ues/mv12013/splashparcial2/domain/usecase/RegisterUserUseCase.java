package edu.sv.ues.mv12013.splashparcial2.domain.usecase;

import android.content.Context;
import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import edu.sv.ues.mv12013.splashparcial2.data.repository.UserRepository;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;

public class RegisterUserUseCase {
    private final UserRepository repo;

    public RegisterUserUseCase(Context context) {
        this.repo = new UserRepository(context);
    }

    public void execute(String email, String pass, String fullName,
                        Callback<Result<UserEntity>> cb) {
        repo.registerRemoteAndCache(email, pass, fullName, cb);
    }
}