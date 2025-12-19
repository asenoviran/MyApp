package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.myapp.model.Book;
import java.util.List;

@Dao
public interface BookDao {

    // --- Чтение ---
    @Query("SELECT * FROM books ORDER BY title ASC")
    LiveData<List<Book>> getAll();

    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY title ASC")
    LiveData<List<Book>> getFavorites();

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%'")
    LiveData<List<Book>> searchByTitle(String query);

    @Query("SELECT * FROM books WHERE genre = :genre")
    LiveData<List<Book>> filterByGenre(String genre);

    @Query("SELECT * FROM books WHERE author = :author")
    LiveData<List<Book>> filterByAuthor(String author);

    @Query("SELECT * FROM books WHERE region = :region")
    LiveData<List<Book>> filterByRegion(String region);

    @Query("SELECT * FROM books ORDER BY year ASC")
    LiveData<List<Book>> sortByYear();

    @Query("SELECT * FROM books ORDER BY region ASC")
    LiveData<List<Book>> sortByRegion();

    // --- Вставка / обновление ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Book> books);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);


    @Update
    void update(Book book);

    @Query("UPDATE books SET isFavorite = :fav WHERE id = :id")
    void setFavorite(int id, boolean fav);

    // --- Удаление ---
    @Delete
    void delete(Book book);

    // --- Синхронное получение всех книг ---
    @Query("SELECT * FROM books")
    List<Book> getAllSync();
}

