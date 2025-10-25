package edu.sv.ues.mv12013.splashparcial2.data.local.entity;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.sv.ues.mv12013.splashparcial2.data.local.dao.PendingSyncDao;
import edu.sv.ues.mv12013.splashparcial2.data.local.dao.UserDao;

@Database(entities = {UserEntity.class, PendingSyncEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract PendingSyncDao pendingSyncDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "splash_parcial2.db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}