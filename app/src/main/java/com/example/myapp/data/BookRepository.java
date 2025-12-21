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

    // Конструктор репозитория, получает DAO для работы с базой
    public BookRepository(BookDao dao) {
        this.dao = dao;
    }

    // --- Чтение из локальной базы ---

    // Получение всех книг из базы в виде LiveData (автоматическое обновление UI при изменении)
    public LiveData<List<Book>> getAll() {
        return dao.getAll();
    }

    // Получение только избранных книг из базы в виде LiveData
    public LiveData<List<Book>> getFavorites() {
        return dao.getFavorites();
    }

    // Поиск книг по названию с использованием LiveData
    public LiveData<List<Book>> search(String query) {
        return dao.searchByTitle(query);
    }

    // --- Обновление данных из API с сохранением локальных изменений ---

    public void refresh() {
        // Вызов Retrofit API для получения списка книг
        RetrofitClient.api().getBooks().enqueue(new Callback<List<Book>>() {

            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {

                // Проверяем успешность ответа и наличие данных
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("Repo", "Ошибка загрузки: " + response.code()); // логируем ошибку
                    return; // прекращаем обработку, если что-то не так
                }

                // Здесь response.body() содержит новый список книг с сервера
                List<Book> newList = response.body();

                // Создаём отдельный поток для работы с локальной базой (Room не разрешает сетевую работу на главном потоке)
                new Thread(() -> {
                    try {
                        // 1. Получаем текущие книги из базы (для сохранения локальных изменений)
                        List<Book> oldList = dao.getAllSync();

                        // 2. Переносим локальные данные пользователя (избранное, рейтинг, комментарии)
                        for (Book newB : newList) {
                            for (Book oldB : oldList) {
                                if (newB.id == oldB.id) {
                                    newB.isFavorite = oldB.isFavorite; // сохраняем статус "избранное"
                                    newB.rating = oldB.rating;         // сохраняем рейтинг
                                    newB.comment = oldB.comment;       // сохраняем комментарий
                                    break;
                                }
                            }
                        }

                        // 3. Перезаписываем таблицу книг в базе с обновлёнными данными
                        dao.insertAll(newList);

                    } catch (Exception e) {
                        // Логируем ошибку при обновлении базы
                        Log.e("Repo", "Ошибка при обновлении базы: " + e.getMessage());
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                // Обработка сетевой ошибки, например при отсутствии интернета
                Log.e("Repo", "Сетевая ошибка: " + t.getMessage());
            }
        });
    }

    // --- Изменение данных ---

    // Установка или снятие флага "избранное" для книги по id
    public void setFavorite(int id, boolean fav) {
        new Thread(() -> {
            try {
                dao.setFavorite(id, fav);
            } catch (Exception e) {
                Log.e("Repo", "Ошибка при установке избранного: " + e.getMessage());
            }
        }).start();
    }

    // Обновление данных конкретной книги в базе
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

    // Создание новой книги с использованием insertAll
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

    // Удаление книги из базы
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

    // Вставка книги с использованием метода insert DAO
    public void insertBook(Book book) {
        new Thread(() -> dao.insert(book)).start();
    }
}

