package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.myapp.model.Book;
import java.util.List;

@Dao
public interface BookDao {

    // --- Чтение данных из базы ---

    // Получение всех книг по названию
    @Query("SELECT * FROM books ORDER BY title ASC")
    LiveData<List<Book>> getAll();

    // Получение всех книг, которые помечены как "избранные"
    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY title ASC")
    LiveData<List<Book>> getFavorites();

    // Поиск книг по части названия (LIKE '%query%')
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%'")
    LiveData<List<Book>> searchByTitle(String query);

    // Фильтрация книг по жанру
    @Query("SELECT * FROM books WHERE genre = :genre")
    LiveData<List<Book>> filterByGenre(String genre);

    // Фильтрация книг по автору
    @Query("SELECT * FROM books WHERE author = :author")
    LiveData<List<Book>> filterByAuthor(String author);

    // Фильтрация книг по региону
    @Query("SELECT * FROM books WHERE region = :region")
    LiveData<List<Book>> filterByRegion(String region);

    // Получение всех книг, отсортированных по году издания (по возрастанию)
    @Query("SELECT * FROM books ORDER BY year ASC")
    LiveData<List<Book>> sortByYear();

    // Получение всех книг, отсортированных по региону (по алфавиту)
    @Query("SELECT * FROM books ORDER BY region ASC")
    LiveData<List<Book>> sortByRegion();

    // --- Вставка и обновление данных ---

    // Вставка списка книг; при конфликте (одинаковый id) заменяет существующие записи
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Book> books);

    // Вставка одной книги; при конфликте заменяет существующую запись
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);

    // Обновление существующей книги (по id)
    @Update
    void update(Book book);

    // Установка/снятие флага "избранное" у книги по id
    @Query("UPDATE books SET isFavorite = :fav WHERE id = :id")
    void setFavorite(int id, boolean fav);

    // --- Удаление ---

    // Удаление книги из базы
    @Delete
    void delete(Book book);

    // --- Синхронное чтение всех книг ---

    // Получение списка всех книг без LiveData (синхронный метод)
    @Query("SELECT * FROM books")
    List<Book> getAllSync();
}
