package com.example.myapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.model.Book;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BooksAdapter adapter;
    private ChipGroup chipGroupStyles;

    private BookListViewModel viewModel;

    private final List<Book> fullList = new ArrayList<>();     // все книги
    private final List<Book> currentList = new ArrayList<>();  // после фильтров

    private boolean showOnlyFavorites = false;
    private String selectedStyle = null;
    private String currentQuery = "";

    private MenuItem favoritesMenuItem;

    private enum SortMode {
        NONE,
        TITLE,
        RATING,
        GENRE
    }

    private SortMode sortMode = SortMode.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------- Toolbar ----------
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ---------- Recycler ----------
        recyclerView = findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new BooksAdapter();
        recyclerView.setAdapter(adapter);

        // Клик по элементу
        adapter.setOnItemClickListener((book, imageView) -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });

        // Клик по избранному
        adapter.setOnFavoriteClickListener(book -> {
            boolean newValue = !book.isFavorite;
            viewModel.setFavorite(book.id, newValue);
            book.isFavorite = newValue;
            applyFiltersAndSort();
        });

        // --- Обработчик кнопки "+" ---
        ImageView add = findViewById(R.id.imgAddBook);
        add.setOnClickListener(v -> {
            startActivity(new Intent(this, AddBookActivity.class));
        });


        // Долгий клик для удаления книги
        adapter.setOnItemLongClickListener(book -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удалить книгу")
                    .setMessage("Вы уверены, что хотите удалить эту книгу?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        viewModel.deleteBook(book);
                        fullList.remove(book); // удаляем из локального списка
                        applyFiltersAndSort();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        // ---------- ChipGroup ----------
        chipGroupStyles = findViewById(R.id.chipGroupGenres);

        // ---------- ViewModel ----------
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);

        viewModel.getBook().observe(this, books -> {
            fullList.clear();
            if (books != null) fullList.addAll(books);

            buildStyleChips(fullList);
            applyFiltersAndSort();
        });
    }

    // ------- Toolbar menu -------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        favoritesMenuItem = menu.findItem(R.id.action_favorites);

        // ----- Search -----
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Поиск книги…");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentQuery = s;
                applyFiltersAndSort();
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem menuItem) {
                currentQuery = "";
                applyFiltersAndSort();
                return true;
            }
        });

        updateFavoritesIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_favorites) {
            showOnlyFavorites = !showOnlyFavorites;
            updateFavoritesIcon();
            applyFiltersAndSort();
            return true;
        }

        if (item.getItemId() == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFavoritesIcon() {
        if (favoritesMenuItem == null) return;

        if (showOnlyFavorites) {
            favoritesMenuItem.setIcon(R.drawable.star);
        } else {
            favoritesMenuItem.setIcon(R.drawable.star_off);
        }
    }

    // ---- Sorting dialog ----
    private void showSortDialog() {
        final String[] options = new String[]{"Без сортировки","По названию","По рейтингу","По жанру"};
        int checked = 0;
        switch (sortMode) {
            case NONE: checked=0; break;
            case TITLE: checked=1; break;
            case RATING: checked=2; break;
            case GENRE: checked=4; break;
        }

        new AlertDialog.Builder(this)
                .setTitle("Сортировка")
                .setSingleChoiceItems(options, checked, (dialog, which) -> {
                    switch (which) {
                        case 0: sortMode = SortMode.NONE; break;
                        case 1: sortMode = SortMode.TITLE; break;
                        case 2: sortMode = SortMode.RATING; break;
                        case 4: sortMode = SortMode.GENRE; break;
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> applyFiltersAndSort())
                .setNegativeButton("Отмена", null)
                .show();
    }

    // ---- Chips generation ----
    private void buildStyleChips(List<Book> books) {

        chipGroupStyles.removeAllViews();

        Set<String> styles = new HashSet<>();
        for (Book b : books) {
            if (!TextUtils.isEmpty(b.genre)) {
                styles.add(b.genre);
            }
        }

        Chip all = createStyleChip("Все жанры");
        all.setChecked(true);
        chipGroupStyles.addView(all);
        all.setOnClickListener(v -> {
            selectedStyle = null;
            applyFiltersAndSort();
        });

        for (String style : styles) {
            Chip chip = createStyleChip(style);
            chipGroupStyles.addView(chip);
            chip.setOnClickListener(v -> {
                selectedStyle = style;
                applyFiltersAndSort();
            });
        }
    }

    private Chip createStyleChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setChipBackgroundColorResource(R.color.colorPrimary);
        chip.setTextColor(getColor(R.color.white));
        chip.setCheckedIconVisible(false);
        chip.setChipCornerRadius(80f);
        return chip;
    }

    // ---- Apply filters + search + sort ----
    private void applyFiltersAndSort() {

        currentList.clear();
        currentList.addAll(fullList);

        if (showOnlyFavorites)
            currentList.removeIf(b -> !b.isFavorite);

        if (!TextUtils.isEmpty(selectedStyle))
            currentList.removeIf(b -> !selectedStyle.equals(b.genre));

        if (!TextUtils.isEmpty(currentQuery)) {
            String q = currentQuery.toLowerCase();
            currentList.removeIf(b -> b.title == null ||
                    !b.title.toLowerCase().contains(q));
        }

        switch (sortMode) {
            case TITLE:
                Collections.sort(currentList, (o1, o2) ->
                        safeString(o1.title).compareToIgnoreCase(safeString(o2.title)));
                break;
            case RATING:
                Collections.sort(currentList, (o1, o2) ->
                        Float.compare(o2.rating, o1.rating));
                break;
            case GENRE:
                Collections.sort(currentList, (o1, o2) ->
                        safeString(o1.genre).compareToIgnoreCase(safeString(o2.genre)));
                break;
        }

        adapter.setItems(currentList);
    }



    private String safeString(String s) {
        return s == null ? "" : s;
    }
}
