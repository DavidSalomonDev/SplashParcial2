package edu.sv.ues.mv12013.splashparcial2.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.sv.ues.mv12013.splashparcial2.core.Callback;
import edu.sv.ues.mv12013.splashparcial2.data.local.dao.UserDao;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.AppDatabase;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import edu.sv.ues.mv12013.splashparcial2.domain.model.Result;

public class UserRepository {

    private final UserDao userDao;
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final SharedPreferences prefs;

    public UserRepository(Context context) {
        this.userDao = AppDatabase.getInstance(context).userDao();
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }

    // ========== CACHE LOCAL (Room) ==========

    public void cacheUser(UserEntity user) {
        new Thread(() -> userDao.upsert(user)).start();
    }

    public UserEntity getCurrentUser() {
        // Llamar desde background thread o usar LiveData
        return userDao.getSingle();
    }

    public void clearUsers() {
        new Thread(() -> userDao.clear()).start();
    }

    // ========== LOGIN REMOTO (Firebase Auth) ==========

    public void loginRemote(String email, String pass, boolean remember, Callback<Result<UserEntity>> cb) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> handleLoginSuccess(result, remember, cb))
                .addOnFailureListener(e -> cb.onComplete(Result.error(
                        e.getMessage() != null ? e.getMessage() : "Error de autenticación"
                )));
    }

    private void handleLoginSuccess(@NonNull AuthResult result, boolean remember, Callback<Result<UserEntity>> cb) {
        String uid = result.getUser() != null ? result.getUser().getUid() : null;
        String email = result.getUser() != null ? result.getUser().getEmail() : null;

        if (uid == null) {
            cb.onComplete(Result.error("No se obtuvo UID de usuario"));
            return;
        }

        // Obtener datos completos desde Firestore
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String fullName = doc.exists() && doc.contains("fullName")
                            ? doc.getString("fullName") : "";

                    UserEntity user = new UserEntity(uid, email, fullName, remember);
                    cacheUser(user);

                    if (remember) {
                        prefs.edit()
                                .putString("email", email)
                                .putBoolean("remember", true)
                                .apply();
                    } else {
                        prefs.edit().clear().apply();
                    }

                    cb.onComplete(Result.success(user));
                })
                .addOnFailureListener(e -> {
                    // Si falla Firestore, igual devolvemos usuario básico
                    UserEntity user = new UserEntity(uid, email, "", remember);
                    cacheUser(user);
                    cb.onComplete(Result.success(user));
                });
    }

    // ========== REGISTRO REMOTO (Firebase Auth + Firestore) ==========

    public void registerRemoteAndCache(String email, String pass, String fullName,
                                       Callback<Result<UserEntity>> cb) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser() != null ? result.getUser().getUid() : null;
                    if (uid == null) {
                        cb.onComplete(Result.error("No se obtuvo UID de usuario"));
                        return;
                    }

                    // Crear documento en Firestore (colección users)
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("uid", uid);
                    doc.put("email", email);
                    doc.put("fullName", fullName);

                    firestore.collection("users").document(uid)
                            .set(doc)
                            .addOnSuccessListener(unused -> {
                                UserEntity user = new UserEntity(uid, email, fullName, false);
                                cacheUser(user);
                                cb.onComplete(Result.success(user));
                            })
                            .addOnFailureListener(e -> cb.onComplete(Result.error(
                                    e.getMessage() != null ? e.getMessage() : "Error guardando en Firestore"
                            )));
                })
                .addOnFailureListener(e -> cb.onComplete(Result.error(
                        e.getMessage() != null ? e.getMessage() : "Error creando usuario"
                )));
    }

    // ========== ACTUALIZAR PERFIL (local + remoto) ==========

    public void updateUserProfile(String uid, String newName, boolean isOnline,
                                  Callback<Result<UserEntity>> cb) {
        if (isOnline) {
            // Actualizar en Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", newName);

            firestore.collection("users").document(uid)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        // Actualizar cache local
                        new Thread(() -> {
                            UserEntity user = userDao.findById(uid);
                            if (user != null) {
                                user.fullName = newName;
                                userDao.update(user);
                            }
                        }).start();
                        cb.onComplete(Result.success(null));
                    })
                    .addOnFailureListener(e -> cb.onComplete(Result.error(
                            e.getMessage() != null ? e.getMessage() : "Error actualizando perfil"
                    )));
        } else {
            // Offline: guardar cambio local y marcar para sincronización
            new Thread(() -> {
                UserEntity user = userDao.findById(uid);
                if (user != null) {
                    user.fullName = newName;
                    userDao.update(user);
                    // TODO: Crear PendingSync en Room para sincronizar después
                }
            }).start();
            cb.onComplete(Result.success(null));
        }
    }

    // ========== LOGOUT ==========

    public void logout() {
        auth.signOut();
        prefs.edit().clear().apply();
        clearUsers();
    }
}