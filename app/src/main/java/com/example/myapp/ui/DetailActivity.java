package com.example.myapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.myapp.R;
import com.example.myapp.model.Book;

// DetailActivity — экран с подробной информацией о книге, позволяет смотреть, редактировать рейтинг и комментарий, ставить в избранное или удалять книгу
public class DetailActivity extends AppCompatActivity {

    private Book book; // текущая книга
    private BookListViewModel viewModel; // ViewModel для работы с данными

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Получаем объект книги из Intent
        book = (Book) getIntent().getSerializableExtra("book");

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // --- UI элементы ---
        ImageView img = findViewById(R.id.imageBook); // обложка книги
        TextView name = findViewById(R.id.textTitle); // название
        TextView info = findViewById(R.id.textInfo); // автор и год
        TextView desc = findViewById(R.id.textDescription); // описание
        RatingBar rating = findViewById(R.id.ratingBar); // рейтинг
        EditText comment = findViewById(R.id.editComment); // комментарий
        Button fav = findViewById(R.id.buttonFavorite); // кнопка избранного
        Button save = findViewById(R.id.buttonSave); // кнопка сохранения изменений
        Button delete = findViewById(R.id.buttonDelete); // кнопка удаления

        // Отображаем данные книги
        name.setText(book.title);
        info.setText(book.author + " · " + book.year);
        desc.setText(book.description);
        rating.setRating(book.rating);
        comment.setText(book.comment);
        updateFavButton(fav); // обновление текста кнопки избранного

        // --- Загрузка изображения через Glide ---
        String url = book.imageUrl;
        if (url != null) url = url.trim();
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fitCenter()
                .into(img);

        // --- Обработка клика по кнопке "Избранное" ---
        fav.setOnClickListener(v -> {
            book.isFavorite = !book.isFavorite; // меняем состояние избранного
            viewModel.setFavorite(book.id, book.isFavorite); // сохраняем через ViewModel
            updateFavButton(fav); // обновляем текст кнопки
        });

        // --- Обработка клика по кнопке "Сохранить" ---
        save.setOnClickListener(v -> {
            book.rating = rating.getRating(); // сохраняем рейтинг
            book.comment = comment.getText().toString(); // сохраняем комментарий
            viewModel.update(book); // обновляем книгу в базе
            finish(); // закрываем экран
        });

        // --- Обработка клика по кнопке "Удалить" ---
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

    // --- Обновление текста кнопки избранного ---
    private void updateFavButton(Button fav) {
        fav.setText(book.isFavorite
                ? "Убрать из избранного"
                : "Добавить в избранное");
    }
}

