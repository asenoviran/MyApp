package com.example.myapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.myapp.R;
import com.example.myapp.model.Book;

public class DetailActivity extends AppCompatActivity {

    private Book book;
    private BookListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Получаем объект книги
        book = (Book) getIntent().getSerializableExtra("book");

        // ViewModel
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // --- UI ЭЛЕМЕНТЫ ---
        ImageView img = findViewById(R.id.imageBook);
        TextView name = findViewById(R.id.textTitle);
        TextView info = findViewById(R.id.textInfo);
        TextView desc = findViewById(R.id.textDescription);
        RatingBar rating = findViewById(R.id.ratingBar);
        EditText comment = findViewById(R.id.editComment);
        Button fav = findViewById(R.id.buttonFavorite);
        Button save = findViewById(R.id.buttonSave);
        Button delete = findViewById(R.id.buttonDelete); // кнопка удаления

        // ПОКАЗЫВАЕМ ДАННЫЕ
        name.setText(book.title);
        info.setText(book.author + " · " + book.year);
        desc.setText(book.description);

        rating.setRating(book.rating);
        comment.setText(book.comment);
        updateFavButton(fav);

        // --- ЗАГРУЗКА ИЗОБРАЖЕНИЯ ---
        String url = book.imageUrl;
        if (url != null) url = url.trim();

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fitCenter()
                .into(img);

        // --- КНОПКА "Избранное" ---
        fav.setOnClickListener(v -> {
            book.isFavorite = !book.isFavorite;
            viewModel.setFavorite(book.id, book.isFavorite);
            updateFavButton(fav);
        });

        // --- Кнопка "Сохранить" ---
        save.setOnClickListener(v -> {
            book.rating = rating.getRating();
            book.comment = comment.getText().toString();
            viewModel.update(book);
            finish();
        });

        // --- Кнопка "Удалить" ---
        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удаление книги")
                    .setMessage("Вы точно хотите удалить эту книгу?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        viewModel.deleteBook(book); // удаляем через ViewModel
                        finish(); // закрываем экран
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void updateFavButton(Button fav) {
        fav.setText(book.isFavorite
                ? "Убрать из избранного"
                : "Добавить в избранное");
    }
}
