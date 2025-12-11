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
    // LiveData — это "живые данные". UI автоматически обновляется!
    private final LiveData<List<Book>> book;
    private final LiveData<List<Book>> favorites;
    public BookListViewModel(@NonNull Application app) {
        super(app);
// Получаем базу данных и DAO
        BookDao dao = AppDatabase.getInstance(app).bookDao();
        repo = new BookRepository(dao);
// Получаем LiveData из Room
        book = repo.getAll();
        favorites = repo.getFavorites();
// Загружаем данные из API
        repo.refresh();
    }
    public LiveData<List<Book>> getBook() {
        return book;
    }
    public LiveData<List<Book>> getFavorites() {
        return favorites;
    }
    public LiveData<List<Book>> search(String q) {
        return repo.search(q);
    }
    public void setFavorite(int id, boolean fav) {
        repo.setFavorite(id, fav);
    }
    public void update(Book b) {
        repo.update(b);
    }

}
