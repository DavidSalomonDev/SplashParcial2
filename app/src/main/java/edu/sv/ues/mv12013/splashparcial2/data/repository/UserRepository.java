package edu.sv.ues.mv12013.splashparcial2.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

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

    private static final String TAG = "UserRepository";

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

        // Intentar obtener el doc; si no existe, lo creamos con mínimos campos
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String fullName = doc.contains("fullName") ? doc.getString("fullName") : "";
                        UserEntity user = new UserEntity(uid, email, fullName, remember);
                        cacheUser(user);
                        persistRemember(email, remember);
                        cb.onComplete(Result.success(user, null));
                    } else {
                        Log.w(TAG, "User doc not found for uid=" + uid + ", creating minimal doc...");
                        Map<String, Object> minimal = new HashMap<>();
                        minimal.put("uid", uid);
                        minimal.put("email", email);
                        minimal.put("fullName", ""); // vacío por ahora
                        firestore.collection("users").document(uid)
                                .set(minimal)
                                .addOnSuccessListener(unused2 -> {
                                    UserEntity user = new UserEntity(uid, email, "", remember);
                                    cacheUser(user);
                                    persistRemember(email, remember);
                                    cb.onComplete(Result.success(user, null));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to create minimal user doc", e);
                                    // Aun así dejamos entrar para no bloquear al usuario
                                    UserEntity user = new UserEntity(uid, email, "", remember);
                                    cacheUser(user);
                                    persistRemember(email, remember);
                                    cb.onComplete(Result.success(user, null));
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetch user doc failure", e);
                    // Permitimos login pero con nombre vacío
                    UserEntity user = new UserEntity(uid, email, "", remember);
                    cacheUser(user);
                    persistRemember(email, remember);
                    cb.onComplete(Result.success(user, null));
                });
    }

    private void persistRemember(String email, boolean remember) {
        if (remember) {
            prefs.edit().putString("email", email).putBoolean("remember", true).apply();
        } else {
            prefs.edit().clear().apply();
        }
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
                    doc.put("createdAt", System.currentTimeMillis());

                    firestore.collection("users").document(uid)
                            .set(doc)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "User doc created in Firestore for uid=" + uid);
                                UserEntity user = new UserEntity(uid, email, fullName, false);
                                cacheUser(user);
                                cb.onComplete(Result.success(user, null));
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error saving user doc to Firestore", e);
                                cb.onComplete(Result.error(
                                        e.getMessage() != null ? e.getMessage() : "Error guardando en Firestore"
                                ));
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "FirebaseAuth createUser failure", e);
                    cb.onComplete(Result.error(
                            e.getMessage() != null ? e.getMessage() : "Error creando usuario"
                    ));
                });
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
                        cb.onComplete(Result.success(null, null));
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
            cb.onComplete(Result.success(null, null));
        }
    }

    // ========== LOGOUT ==========

    public void logout() {
        auth.signOut();
        prefs.edit().clear().apply();
        clearUsers();
    }
}