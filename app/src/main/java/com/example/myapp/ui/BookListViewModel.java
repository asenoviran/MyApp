package com.example.myapp.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.myapp.data.AppDatabase;
import com.example.myapp.data.BookDao;
import com.example.myapp.data.BookRepository;
import com.example.myapp.model.Book;
import java.util.List;

// ViewModel для работы с книгами: чтение, поиск, добавление, обновление, удаление
public class BookListViewModel extends AndroidViewModel {

    // --- Поля ---
    private final BookRepository repo;              // Репозиторий для работы с базой и API
    private final LiveData<List<Book>> book;       // Список всех книг (LiveData для наблюдения UI)
    private final LiveData<List<Book>> favorites;  // Список избранных книг

    // --- Конструктор ---
    public BookListViewModel(@NonNull Application app) {
        super(app);
        // Получаем DAO для работы с базой данных
        BookDao dao = AppDatabase.getInstance(app).bookDao();
        // Создаём репозиторий с этим DAO
        repo = new BookRepository(dao);
        // Получаем все книги из базы (LiveData)
        book = repo.getAll();
        // Получаем избранные книги (LiveData)
        favorites = repo.getFavorites();
        // Обновляем данные с сервера API и сохраняем их в базу
        repo.refresh();
    }

    // --- READ (чтение данных) ---

    // Получение всех книг для UI
    public LiveData<List<Book>> getBook() {
        return book;
    }

    // Получение только избранных книг
    public LiveData<List<Book>> getFavorites() {
        return favorites;
    }

    // Поиск книги по названию
    public LiveData<List<Book>> search(String q) {
        return repo.search(q);
    }

    // Установка или снятие флага "избранное" для книги по id
    public void setFavorite(int id, boolean fav) {
        repo.setFavorite(id, fav);
    }

    // Обновление всех данных книги (рейтинг, комментарий, другие изменения)
    public void update(Book b) {
        repo.update(b);
    }

    // --- DELETE (удаление) ---

    // Удаление книги из базы
    public void deleteBook(Book book) {
        repo.delete(book);
    }

    // --- CREATE / INSERT (создание новой книги) ---

    // Вставка новой книги в базу
    public void insertBook(Book book) {
        repo.insertBook(book);
    }

}
