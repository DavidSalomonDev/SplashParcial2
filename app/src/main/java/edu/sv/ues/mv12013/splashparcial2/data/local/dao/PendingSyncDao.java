package edu.sv.ues.mv12013.splashparcial2.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.sv.ues.mv12013.splashparcial2.data.local.entity.PendingSyncEntity;

@Dao
public interface PendingSyncDao {
    @Insert long insert(PendingSyncEntity e);
    @Delete void delete(PendingSyncEntity e);

    @Query("SELECT * FROM pending_sync ORDER BY createdAt ASC")
    List<PendingSyncEntity> getAll();

    @Query("DELETE FROM pending_sync")
    void clear();
}