package edu.sv.ues.mv12013.splashparcial2.domain.usecase;

import android.content.Context;

import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.data.repository.AuthRepository;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;
import edu.sv.ues.mv12013.splashparcial2.core.workers.SyncWorker;

public class RegisterUserUseCase {

    private final Context app;
    private final AuthRepository repo;

    public RegisterUserUseCase(Context appContext) {
        this.app = appContext.getApplicationContext();
        this.repo = new AuthRepository(this.app);
    }

    public void execute(String email, String name, String password, boolean remember, Callback<Result<Void>> cb) {
        new Thread(() -> {
            AuthRepository.Result r = repo.register(email, name, password, remember);
            if (r.ok) {
                if (r.queued) {
                    SyncWorker.enqueueWhenOnline(app);
                }
                cb.onComplete(Result.success(null, r.message));
            } else {
                cb.onComplete(Result.error(r.message));
            }
        }).start();
    }
}