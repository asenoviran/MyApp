package com.example.myapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.myapp.R;


public class MainActivity extends AppCompatActivity {
    private BookListViewModel viewModel;
    private BooksAdapter adapter;
    private RecyclerView recycler;
    private EditText editSearch;
    private ProgressBar progress;
    private TextView textEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// --- ИНИЦИАЛИЗАЦИЯ UI ---
        recycler = findViewById(R.id.recyclerBuildings);
        editSearch = findViewById(R.id.editSearch);
        progress = findViewById(R.id.progressBar);
        textEmpty = findViewById(R.id.textEmpty);
// --- НАСТРОЙКА СПИСКА ---
        adapter = new BooksAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
// --- ПОЛУЧАЕМ ViewModel ---
        viewModel = new
                ViewModelProvider(this).get(BookListViewModel.class);
// --- НАБЛЮДАЕМ ЗА ДАННЫМИ ---
        viewModel.getBook().observe(this, list -> {
            progress.setVisibility(android.view.View.GONE);
            if (list == null || list.isEmpty()) {
                textEmpty.setVisibility(android.view.View.VISIBLE);
                recycler.setVisibility(android.view.View.GONE);
            } else {
                textEmpty.setVisibility(android.view.View.GONE);
                recycler.setVisibility(android.view.View.VISIBLE);
// Обновляем данные адаптера
                adapter.setItems(list);
            }
        });
// --- ПОИСК ПО ENTER ---
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.search(editSearch.getText().toString())
                        .observe(this, adapter::setItems);
                return true;
            }
            return false;
        });
// --- КЛИК ПО КАРТОЧКЕ ---
        adapter.setOnItemClickListener((books) -> {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("books", books);
            startActivity(i);
        });
// --- ИЗМЕНЕНИЕ ИЗБРАННОГО ---
        adapter.setOnFavoriteClickListener(book -> {
            viewModel.setFavorite(book.id, !book.isFavorite);
        });
// --- ПЕРЕХОД В ИЗБРАННОЕ ---
        findViewById(R.id.fabFavorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class))
        );
    }
}