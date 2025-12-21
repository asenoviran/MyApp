package com.example.myapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.myapp.model.Book;


@Database(entities = {Book.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Единый экземпляр базы данных (Singleton) для предотвращения создания нескольких объектов
    private static volatile AppDatabase INSTANCE;

    // --- Абстрактный метод для получения DAO ---
    public abstract BookDao bookDao();
    // Room автоматически создаст реализацию этого метода при компиляции.
    // Через DAO мы будем выполнять все операции с таблицей книг: вставка, удаление, обновление, запросы.

    // --- Получение экземпляра базы данных ---
    public static AppDatabase getInstance(Context context) {
        // Проверяем, создан ли уже экземпляр
        if (INSTANCE == null) {
            // Синхронизация для потокобезопасности (чтобы два потока одновременно не создали базу)
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Создаём базу данных с помощью Room
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(), // Контекст приложения
                                    AppDatabase.class,              // Класс базы данных
                                    "book_db"                       // Имя файла базы данных
                            )
                            .fallbackToDestructiveMigration() // Если версия БД изменилась, база будет пересоздана (все данные удалятся)
                            .build();
                }
            }
        }
        return INSTANCE; // Возвращаем единый экземпляр базы данных
    }
}
