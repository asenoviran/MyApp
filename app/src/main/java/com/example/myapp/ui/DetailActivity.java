package com.example.myapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.widget.*;
import com.example.myapp.R;
import com.example.myapp.model.Book;
public class DetailActivity extends AppCompatActivity {
    private Book book;
    private BookListViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
// Получаем здание, которое было передано из списка
        book = (Book) getIntent().getSerializableExtra("book");
// ViewModel для обновления избранного, рейтинга и комментариев
        viewModel = new
                ViewModelProvider(this).get(BookListViewModel.class);
// --- UI ЭЛЕМЕНТЫ ---
        TextView name = findViewById(R.id.textName);
        TextView info = findViewById(R.id.textInfo);
        TextView desc = findViewById(R.id.textDescription);
        RatingBar rating = findViewById(R.id.ratingBar);
        EditText comment = findViewById(R.id.editComment);
        Button fav = findViewById(R.id.buttonFavorite);
        Button save = findViewById(R.id.buttonSave);
// Показываем сохранённые значения
        rating.setRating(book.rating);
        comment.setText(book.comment);
        updateFavButton(fav);
// --- ЛОГИКА КНОПОК ---
// Кнопка "В избранное"
        fav.setOnClickListener(v -> {
            book.isFavorite = !book.isFavorite;
            viewModel.setFavorite(book.id, book.isFavorite);
            updateFavButton(fav);
        });
// Кнопка "Сохранить"
        save.setOnClickListener(v -> {
            book.rating = rating.getRating();
            book.comment = comment.getText().toString();
// Обновляем локальную базу данных
            viewModel.update(book);
            finish();
        });
    }
    // Обновляем текст кнопки
    private void updateFavButton(Button fav) {
        fav.setText(book.isFavorite ? "Убрать из избранного"
                : "Добавить в избранное");
    }
}
