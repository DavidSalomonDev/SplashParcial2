package edu.sv.ues.mv12013.splashparcial2.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import edu.sv.ues.mv12013.splashparcial2.data.local.entity.UserEntity;
import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getSingle(); // usuario actual

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    UserEntity findById(String uid);

    @Query("SELECT * FROM users")
    List<UserEntity> getAll();

    @Query("DELETE FROM users")
    void clear();
}