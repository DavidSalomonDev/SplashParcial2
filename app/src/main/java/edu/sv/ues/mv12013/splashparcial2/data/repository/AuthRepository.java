package edu.sv.ues.mv12013.splashparcial2.data.repository;

import android.content.Context;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sv.ues.mv12013.splashparcial2.common.NetworkUtils;
import edu.sv.ues.mv12013.splashparcial2.common.SecurityUtils;
import edu.sv.ues.mv12013.splashparcial2.data.local.dao.PendingSyncDao;
import edu.sv.ues.mv12013.splashparcial2.data.local.dao.UserDao;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.AppDatabase;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.PendingSyncEntity;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;

public class AuthRepository {

    private final Context app;
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final UserDao userDao;
    private final PendingSyncDao pendingDao;
    private final Gson gson = new Gson();

    public AuthRepository(Context context) {
        this.app = context.getApplicationContext();
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        AppDatabase db = AppDatabase.getInstance(app);
        this.userDao = db.userDao();
        this.pendingDao = db.pendingSyncDao();
    }

    public Result register(String email, String fullName, String password, boolean remember) {
        try {
            String pwdHash = SecurityUtils.sha256(password);
            boolean online = NetworkUtils.isOnline(app);
            if (online) {
                AuthResult ar = Tasks.await(auth.createUserWithEmailAndPassword(email, password));
                String uid = ar.getUser() != null ? ar.getUser().getUid() : email;

                Map<String, Object> data = new HashMap<>();
                data.put("uid", uid);
                data.put("email", email);
                data.put("fullName", fullName);
                data.put("createdAt", System.currentTimeMillis());
                Tasks.await(firestore.collection("users").document(uid).set(data));

                userDao.upsert(UserEntity.createLocal(uid, email, fullName, remember, pwdHash));
                return Result.success(uid);
            } else {
                String uid = "local_" + email;
                userDao.upsert(UserEntity.createLocal(uid, email, fullName, remember, pwdHash));

                Map<String, Object> payload = new HashMap<>();
                payload.put("op", "REGISTER");
                payload.put("email", email);
                payload.put("fullName", fullName);
                payload.put("password", password); // necesario para FirebaseAuth al sincronizar

                PendingSyncEntity e = new PendingSyncEntity();
                e.type = "REGISTER";
                e.payload = gson.toJson(payload);
                e.createdAt = System.currentTimeMillis();
                pendingDao.insert(e);

                return Result.queued(uid, "Registrado offline. Se sincronizará al tener internet.");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    public Result login(String email, String password, boolean remember) {
        try {
            boolean online = NetworkUtils.isOnline(app);
            String pwdHash = SecurityUtils.sha256(password);

            if (online) {
                AuthResult ar = Tasks.await(auth.signInWithEmailAndPassword(email, password));
                String uid = ar.getUser() != null ? ar.getUser().getUid() : email;

                Map<String, Object> doc = Tasks.await(firestore.collection("users").document(uid).get()).getData();
                String name = doc != null && doc.get("fullName") != null ? String.valueOf(doc.get("fullName")) : "";

                userDao.upsert(UserEntity.createLocal(uid, email, name, remember, pwdHash));
                return Result.success(uid);
            } else {
                UserEntity local = userDao.getSingle();
                if (local != null && email.equalsIgnoreCase(local.email) && pwdHash.equals(local.passwordHash)) {
                    local.remember = remember;
                    userDao.update(local);
                    return Result.success(local.uid);
                }
                return Result.error("Credenciales inválidas en modo offline");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    public void syncPendingIfOnline() {
        if (!NetworkUtils.isOnline(app)) return;
        List<PendingSyncEntity> list = pendingDao.getAll();
        for (PendingSyncEntity p : list) {
            try {
                Map<?,?> payload = gson.fromJson(p.payload, Map.class);
                String op = (String) payload.get("op");
                if ("REGISTER".equals(op)) {
                    String email = (String) payload.get("email");
                    String fullName = (String) payload.get("fullName");
                    String password = (String) payload.get("password");

                    AuthResult ar = Tasks.await(auth.createUserWithEmailAndPassword(email, password));
                    String uid = ar.getUser() != null ? ar.getUser().getUid() : email;

                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", uid);
                    data.put("email", email);
                    data.put("fullName", fullName);
                    data.put("createdAt", System.currentTimeMillis());
                    Tasks.await(firestore.collection("users").document(uid).set(data));
                }
                pendingDao.delete(p);
            } catch (Exception ignored) {
                // queda en cola para próximo intento
            }
        }
    }

    public static class Result {
        public boolean ok;
        public String uid;
        public String message;
        public boolean queued;

        static Result success(String uid) {
            Result r = new Result();
            r.ok = true;
            r.uid = uid;
            return r;
        }
        static Result queued(String uid, String msg) {
            Result r = new Result();
            r.ok = true;
            r.uid = uid;
            r.queued = true;
            r.message = msg;
            return r;
        }
        static Result error(String msg) {
            Result r = new Result();
            r.ok = false;
            r.message = msg;
            return r;
        }
    }
}