package edu.sv.ues.mv12013.splashparcial2.core.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import edu.sv.ues.mv12013.splashparcial2.data.repository.AuthRepository;

public class SyncWorker extends Worker {
    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        new AuthRepository(getApplicationContext()).syncPendingIfOnline();
        return ListenableWorker.Result.success();
    }

    public static void enqueueWhenOnline(Context context) {
        Constraints c = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(c)
                .build();
        WorkManager.getInstance(context.getApplicationContext()).enqueue(req);
    }
}