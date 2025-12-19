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

    private EditText editTitle, editAuthor, editGenre, editYear, editDescription, editImage;
    private RatingBar ratingBar;
    private BookListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        editTitle = findViewById(R.id.title);
        editAuthor = findViewById(R.id.Author);
        editGenre = findViewById(R.id.Genre);
        editYear = findViewById(R.id.Year);
        editDescription = findViewById(R.id.Description);
        editImage = findViewById(R.id.ImageUrl);

        Button btnSave = findViewById(R.id.buttonSave);

        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String author = editAuthor.getText().toString().trim();
            String genre = editGenre.getText().toString().trim();
            int year = Integer.parseInt(editYear.getText().toString().trim());
            String desc = editDescription.getText().toString().trim();
            String imageUrl = editImage.getText().toString().trim();


            Book newBook = new Book();
            newBook.title = title;
            newBook.author = author;
            newBook.genre = genre;
            newBook.year = year;
            newBook.description = desc;
            newBook.imageUrl = imageUrl;
            newBook.isFavorite = false;

            viewModel.insertBook(newBook); // вызываем метод в репозитории

            finish();
        });
    }
}
