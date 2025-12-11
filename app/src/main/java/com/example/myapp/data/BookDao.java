package com.example.myapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.myapp.model.Book;
import java.util.List;

@Dao
public interface BookDao {

    // Получить все книги, сортировка по названию
    @Query("SELECT * FROM books ORDER BY title ASC")
    LiveData<List<Book>> getAll();

    // Избранное
    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY title ASC")
    LiveData<List<Book>> getFavorites();

    // Поиск по названию книги
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%'")
    LiveData<List<Book>> searchByTitle(String query);

    // Фильтры по жанру, автору и региону
    @Query("SELECT * FROM books WHERE genre = :genre")
    LiveData<List<Book>> filterByGenre(String genre);

    @Query("SELECT * FROM books WHERE author = :author")
    LiveData<List<Book>> filterByAuthor(String author);

    @Query("SELECT * FROM books WHERE region = :region")
    LiveData<List<Book>> filterByRegion(String region);

    // Сортировки
    @Query("SELECT * FROM books ORDER BY year ASC")
    LiveData<List<Book>> sortByYear();

    @Query("SELECT * FROM books ORDER BY region ASC")
    LiveData<List<Book>> sortByRegion();

    // Вставка данных (из API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Book> books);

    // Обновление записи
    @Update
    void update(Book book);

    // Изменить статус избранного
    @Query("UPDATE books SET isFavorite = :fav WHERE id = :id")
    void setFavorite(int id, boolean fav);
}
