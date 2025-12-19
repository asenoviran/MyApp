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

public class BookListViewModel extends AndroidViewModel {

    private final BookRepository repo;
    private final LiveData<List<Book>> book;
    private final LiveData<List<Book>> favorites;

    public BookListViewModel(@NonNull Application app) {
        super(app);
        BookDao dao = AppDatabase.getInstance(app).bookDao();
        repo = new BookRepository(dao);
        book = repo.getAll();
        favorites = repo.getFavorites();
        repo.refresh();
    }

    // --- READ ---
    public LiveData<List<Book>> getBook() {
        return book;
    }

    public LiveData<List<Book>> getFavorites() {
        return favorites;
    }

    public LiveData<List<Book>> search(String q) {
        return repo.search(q);
    }

    // --- UPDATE ---
    public void setFavorite(int id, boolean fav) {
        repo.setFavorite(id, fav);
    }

    public void update(Book b) {
        repo.update(b);
    }

    // --- DELETE ---
    public void deleteBook(Book book) {
        repo.delete(book);
    }

    public void insertBook(Book book) {
        repo.insertBook(book);
    }

}
