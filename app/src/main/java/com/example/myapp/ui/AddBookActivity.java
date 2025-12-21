package com.example.myapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapp.R;
import com.example.myapp.model.Book;

public class AddBookActivity extends AppCompatActivity {

    // Поля для ввода данных книги
    private EditText editTitle, editAuthor, editGenre, editYear, editDescription, editImage;
    private BookListViewModel viewModel; // ViewModel для работы с базой

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book); // подключаем разметку

        // Инициализация полей ввода по ID из layout
        editTitle = findViewById(R.id.title);
        editAuthor = findViewById(R.id.Author);
        editGenre = findViewById(R.id.Genre);
        editYear = findViewById(R.id.Year);
        editDescription = findViewById(R.id.Description);
        editImage = findViewById(R.id.ImageUrl);

        // Кнопка сохранения новой книги
        Button btnSave = findViewById(R.id.buttonSave);

        // Получаем ViewModel для взаимодействия с базой
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // Обработка нажатия кнопки "Сохранить"
        btnSave.setOnClickListener(v -> {
            // --- Получаем данные, введённые пользователем ---
            String title = editTitle.getText().toString().trim();
            String author = editAuthor.getText().toString().trim();
            String genre = editGenre.getText().toString().trim();
            int year = Integer.parseInt(editYear.getText().toString().trim()); // преобразуем в int
            String desc = editDescription.getText().toString().trim();
            String imageUrl = editImage.getText().toString().trim();

            // --- Создаём объект Book и заполняем поля ---
            Book newBook = new Book();
            newBook.title = title;
            newBook.author = author;
            newBook.genre = genre;
            newBook.year = year;
            newBook.description = desc;
            newBook.imageUrl = imageUrl;
            newBook.isFavorite = false;

            // --- Вставка книги в базу через ViewModel/Repository ---
            viewModel.insertBook(newBook);

            // Закрываем экран и возвращаемся назад
            finish();
        });
    }
}