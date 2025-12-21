package com.example.myapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.model.Book;

// FavoritesActivity — экран, отображающий только избранные книги пользователя, с возможностью вернуться назад
public class FavoritesActivity extends AppCompatActivity {

    private BooksAdapter adapter; // адаптер для RecyclerView
    private BookListViewModel viewModel; // ViewModel для получения данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Настройка ActionBar: заголовок и кнопка "назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Избранное");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Настройка RecyclerView для отображения списка книг
        RecyclerView recycler = findViewById(R.id.recyclerFavorites);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BooksAdapter();
        recycler.setAdapter(adapter);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // Подписка на LiveData избранных книг, обновление списка при изменении
        viewModel.getFavorites().observe(this, list -> {
            if (list != null) {
                adapter.setItems(list);
            }
        });
    }

    // Обработка нажатия кнопки "назад" в ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // закрываем экран
        return true;
    }
}


