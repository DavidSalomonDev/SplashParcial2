package edu.sv.ues.mv12013.splashparcial2.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<AuthResult> register(String email, String password, String fullName) {
        return auth.createUserWithEmailAndPassword(email, password)
                .onSuccessTask(res -> {
                    String uid = res.getUser().getUid();
                    Map<String, Object> data = new HashMap<>();
                    data.put("email", email);
                    data.put("fullName", fullName);
                    DocumentReference doc = db.collection("users").document(uid);
                    return doc.set(data).continueWith(task -> res);
                });
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public String currentUid() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}