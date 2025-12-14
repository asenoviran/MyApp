package com.example.myapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.model.Book;

public class FavoritesActivity extends AppCompatActivity {

    private BooksAdapter adapter;
    private BookListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Устанавливаем название ActionBar и кнопку назад
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Избранное");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // RecyclerView
        RecyclerView recycler = findViewById(R.id.recyclerFavorites);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BooksAdapter();
        recycler.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        // Наблюдаем за избранными книгами
        viewModel.getFavorites().observe(this, list -> {
            if (list != null) {
                adapter.setItems(list);
            }
        });

        // Кнопка "назад" в ActionBar
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


