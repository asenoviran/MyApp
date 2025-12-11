package com.example.myapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
import android.content.Intent;
import android.os.Bundle;
import com.example.myapp.R;
public class FavoritesActivity extends AppCompatActivity {
    private BooksAdapter adapter;
    private BookListViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        RecyclerView recycler = findViewById(R.id.recyclerFavorites);
        adapter = new BooksAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        viewModel = new
                ViewModelProvider(this).get(BookListViewModel.class);
// Наблюдаем за избранным
        viewModel.getFavorites().observe(this, adapter::setItems);
// Удаляем из избранного
        adapter.setOnFavoriteClickListener(book ->
                viewModel.setFavorite(book.id, false)
        );
        adapter.setOnItemClickListener((book) -> {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("book", book);
            startActivity(i);
        });
    }
}

