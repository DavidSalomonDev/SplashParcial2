package edu.sv.ues.mv12013.splashparcial2.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import edu.sv.ues.mv12013.splashparcial2.common.NetworkUtils;
import edu.sv.ues.mv12013.splashparcial2.core.workers.SyncWorker;
import edu.sv.ues.mv12013.splashparcial2.data.local.dao.PendingSyncDao;
import edu.sv.ues.mv12013.splashparcial2.data.local.dao.UserDao;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.AppDatabase;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.PendingSyncEntity;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;

public class ProfileViewModel extends AndroidViewModel {

    private final MutableLiveData<String> _name = new MutableLiveData<>("");
    public LiveData<String> name = _name;

    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.Idle);
    public LiveData<UiState> uiState = _uiState;

    private final UserDao userDao;
    private final PendingSyncDao pendingDao;
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final Gson gson;

    private String currentUid;
    private String currentEmail;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        this.userDao = db.userDao();
        this.pendingDao = db.pendingSyncDao();
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.gson = new Gson();
    }

    public void loadCurrentUser() {
        _uiState.postValue(UiState.Loading);

        // 1) Cargar desde Room inmediatamente (hilo bg)
        new Thread(() -> {
            UserEntity local = userDao.getSingle();
            if (local != null) {
                currentUid = local.uid;
                currentEmail = local.email;
                String localName = local.fullName != null ? local.fullName : "";
                _name.postValue(localName.isEmpty() ? "" : localName);
            } else {
                // fallback a FirebaseAuth para UID
                if (auth.getCurrentUser() != null) {
                    currentUid = auth.getCurrentUser().getUid();
                    currentEmail = auth.getCurrentUser().getEmail();
                }
            }

            // 2) Si hay internet y UID, refrescar Firestore y actualizar Room
            boolean online = NetworkUtils.isOnline(getApplication().getApplicationContext());
            if (online && currentUid != null) {
                firestore.collection("users").document(currentUid)
                        .get()
                        .addOnSuccessListener(this::onRemoteDoc)
                        .addOnFailureListener(e -> _uiState.postValue(UiState.Idle));
            } else {
                _uiState.postValue(UiState.Idle);
            }
        }).start();
    }

    private void onRemoteDoc(DocumentSnapshot doc) {
        if (doc != null && doc.exists()) {
            String remoteName = doc.contains("fullName") ? doc.getString("fullName") : "";
            if (remoteName == null) remoteName = "";
            String remoteEmail = doc.contains("email") ? doc.getString("email") : currentEmail;

            String finalRemoteEmail = remoteEmail;
            String finalRemoteName = remoteName;

            // Actualizar Room en bg
            new Thread(() -> {
                UserEntity local = userDao.getSingle();
                if (local != null) {
                    local.fullName = finalRemoteName;
                    if (finalRemoteEmail != null && !finalRemoteEmail.isEmpty()) {
                        local.email = finalRemoteEmail;
                    }
                    userDao.update(local);
                }
            }).start();

            if (!finalRemoteName.isEmpty()) {
                _name.postValue(finalRemoteName);
            }
        }
        _uiState.postValue(UiState.Idle);
    }

    public void updateName(String newName) {
        String normalized = newName == null ? "" : newName.trim();
        if (normalized.isEmpty()) {
            _uiState.postValue(new UiState.Error("El nombre es requerido"));
            return;
        }
        // Usar variable final para lambdas/hilos
        final String nameToSave = normalized;

        if (currentUid == null) {
            // Intentar obtener UID actual
            if (auth.getCurrentUser() != null) {
                currentUid = auth.getCurrentUser().getUid();
            } else {
                UserEntity local = userDao.getSingle();
                if (local != null) currentUid = local.uid;
            }
            if (currentUid == null) {
                _uiState.postValue(new UiState.Error("No hay usuario autenticado"));
                return;
            }
        }

        _uiState.postValue(UiState.Loading);

        boolean online = NetworkUtils.isOnline(getApplication().getApplicationContext());
        if (online) {
            // Remoto primero: Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", nameToSave);

            firestore.collection("users").document(currentUid)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        // Actualizar cache local
                        new Thread(() -> {
                            UserEntity u = userDao.findById(currentUid);
                            if (u != null) {
                                u.fullName = nameToSave;
                                userDao.update(u);
                            }
                        }).start();
                        _name.postValue(nameToSave);
                        _uiState.postValue(UiState.Success);
                    })
                    .addOnFailureListener(e -> _uiState.postValue(
                            new UiState.Error(e.getMessage() != null ? e.getMessage() : "Error actualizando perfil")
                    ));
        } else {
            // Offline: actualizar Room + encolar PendingSync
            new Thread(() -> {
                try {
                    UserEntity u = userDao.findById(currentUid);
                    if (u != null) {
                        u.fullName = nameToSave;
                        userDao.update(u);
                    }

                    PendingSyncEntity e = new PendingSyncEntity();
                    e.type = "PROFILE_UPDATE";
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("uid", currentUid);
                    payload.put("fullName", nameToSave);
                    e.payload = gson.toJson(payload);
                    e.createdAt = System.currentTimeMillis();
                    pendingDao.insert(e);

                    // Encolar Worker con red requerida
                    SyncWorker.enqueueWhenOnline(getApplication().getApplicationContext());

                    _name.postValue(nameToSave);
                    _uiState.postValue(UiState.Success);
                } catch (Exception ex) {
                    _uiState.postValue(new UiState.Error("Error guardando offline"));
                }
            }).start();
        }
    }
}