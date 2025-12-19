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
                    try {
                        // 1. Берём текущие книги из базы (с сохранённым isFavorite)
                        List<Book> oldList = dao.getAllSync();

                        // 2. Сохраняем локальные данные пользователя
                        for (Book newB : newList) {
                            for (Book oldB : oldList) {
                                if (newB.id == oldB.id) {
                                    newB.isFavorite = oldB.isFavorite;
                                    // можно переносить комментарии или рейтинг
                                    break;
                                }
                            }
                        }

                        // 3. Перезаписываем таблицу с сохранёнными изменениями
                        dao.insertAll(newList);

                    } catch (Exception e) {
                        Log.e("Repo", "Ошибка при обновлении базы: " + e.getMessage());
                    }
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
        new Thread(() -> {
            try {
                dao.setFavorite(id, fav);
            } catch (Exception e) {
                Log.e("Repo", "Ошибка при установке избранного: " + e.getMessage());
            }
        }).start();
    }

    public void update(Book book) {
        new Thread(() -> {
            try {
                dao.update(book);
            } catch (Exception e) {
                Log.e("Repo", "Ошибка при обновлении книги: " + e.getMessage());
            }
        }).start();
    }

    // --- Создание новой книги ---
    public void createBook(Book book) {
        new Thread(() -> {
            try {
                dao.insertAll(List.of(book));
            } catch (Exception e) {
                Log.e("Repo", "Ошибка при добавлении книги: " + e.getMessage());
            }
        }).start();
    }

    // --- Удаление книги ---
    public void delete(Book book) {
        new Thread(() -> {
            try {
                dao.delete(book);
            } catch (Exception e) {
                Log.e("Repo", "Ошибка при удалении книги: " + e.getMessage());
            }
        }).start();
    }
    // --- Вставка одной книги ---
    public void insertBook(Book book) {
        new Thread(() -> dao.insert(book)).start();
    }



}
