package com.example.myapp.data;

import android.util.Log;
import androidx.lifecycle.LiveData;

import com.example.myapp.model.Book;
import com.example.myapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRepository {

    private final BookDao dao;

    public BookRepository(BookDao dao) {
        this.dao = dao;
    }

    // --- Чтение из локальной базы ---
    public LiveData<List<Book>> getAll() {
        return dao.getAll();
    }

    public LiveData<List<Book>> getFavorites() {
        return dao.getFavorites();
    }

    public LiveData<List<Book>> search(String query) {
        return dao.searchByTitle(query);
    }

    // --- Обновление данных из API c сохранением избранного ---
    public void refresh() {

        RetrofitClient.api().getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call,
                                   Response<List<Book>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("Repo", "Ошибка загрузки: " + response.code());
                    return;
                }

                List<Book> newList = response.body();

                new Thread(() -> {

                    // 1. Берём текущие книги из базы (с сохранённым isFavorite)
                    List<Book> oldList = dao.getAllSync();

                    // 2. Сохраняем локальные данные пользователя
                    for (Book newB : newList) {
                        for (Book oldB : oldList) {
                            if (newB.id == oldB.id) {

                                // --- ВАЖНО ---
                                // переносим локальные изменения
                                newB.isFavorite = oldB.isFavorite;

                                // если у тебя есть комментарии или рейтинг — переносим их тоже
                                // newB.comment = oldB.comment;
                                // newB.rating = oldB.rating;

                                break;
                            }
                        }
                    }

                    // 3. Перезаписываем таблицу но уже с сохранёнными избранными
                    dao.insertAll(newList);

                }).start();
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("Repo", "Сетевая ошибка: " + t.getMessage());
            }
        });
    }

    // --- Изменение данных ---
    public void setFavorite(int id, boolean fav) {
        new Thread(() -> dao.setFavorite(id, fav)).start();
    }

    public void update(Book book) {
        new Thread(() -> dao.update(book)).start();
    }
}
