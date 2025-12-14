package com.example.myapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.myapp.model.Book;

@Database(entities = {Book.class}, version = 2, exportSchema = false) // ↑ увеличили версию
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract BookDao bookDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "architecture_db"
                            )
                            .fallbackToDestructiveMigration() // пересоздание БД при изменении схемы
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
