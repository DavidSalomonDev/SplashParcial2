package edu.sv.ues.mv12013.splashparcial2.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pending_sync")
public class PendingSyncEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String type;     // REGISTER, PROFILE_UPDATE

    @NonNull
    public String payload;  // JSON

    public long createdAt;
}