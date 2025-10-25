package edu.sv.ues.mv12013.splashparcial2.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    public String uid;         // uid de Firebase

    public String email;
    public String fullName;
    public boolean remember;   // para “Recordarme” (opcional)

    public UserEntity(@NonNull String uid, String email, String fullName, boolean remember) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.remember = remember;
    }
}