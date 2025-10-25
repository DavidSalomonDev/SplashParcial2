package edu.sv.ues.mv12013.splashparcial2.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    public String uid;           // uid de Firebase (offline puede ser "local_<email>")

    public String email;
    public String fullName;
    public boolean remember;

    // nuevos campos para login offline
    public String passwordHash;
    public long createdAt;
    public long updatedAt;

    public UserEntity(@NonNull String uid, String email, String fullName, boolean remember) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.remember = remember;
    }

    public static UserEntity createLocal(String uid, String email, String fullName, boolean remember, String passwordHash) {
        UserEntity u = new UserEntity(uid, email, fullName, remember);
        u.passwordHash = passwordHash;
        long now = System.currentTimeMillis();
        u.createdAt = now;
        u.updatedAt = now;
        return u;
    }
}