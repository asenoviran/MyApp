package com.example.myapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;        // RecyclerView для отображения книг
    private BooksAdapter adapter;             // Адаптер для RecyclerView
    private ChipGroup    chipGroupStyles;        // ChipGroup для фильтра по жанрам
    private BookListViewModel viewModel;      // ViewModel для работы с данными

    private final List<Book> fullList = new ArrayList<>();     // Полный список книг из БД
    private final List<Book> currentList = new ArrayList<>();  // Список после применения фильтров и поиска

    private boolean showOnlyFavorites = false; // Флаг отображения только избранного
    private String selectedStyle = null;       // Выбранный жанр для фильтра
    private String currentQuery = "";          // Текущий текст поиска

    private MenuItem favoritesMenuItem;        // Элемент меню "Избранное"

    // Режимы сортировки
    private enum SortMode {
        NONE,
        TITLE,
        RATING,
        GENRE
    }

    private SortMode sortMode = SortMode.NONE; // Текущий режим сортировки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Вызываем родительский onCreate для корректного создания Activity

        setContentView(R.layout.activity_main);
        // Устанавливаем разметку экрана (layout) для MainActivity

        // ---------- Toolbar ------------
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Подключаем Toolbar как ActionBar, чтобы использовать меню и кнопки

        // ---------- RecyclerView ----------
        recyclerView = findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // Настройка RecyclerView: вертикальный список, оптимизация фиксированного размера

        adapter = new BooksAdapter();
        recyclerView.setAdapter(adapter);
        // Устанавливаем адаптер для отображения элементов списка

        // --- Клик по элементу списка ---
        adapter.setOnItemClickListener((book, imageView) -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
            // Переход на экран деталей книги с передачей объекта книги
        });

        // --- Клик по кнопке избранного ---
        adapter.setOnFavoriteClickListener(book -> {
            boolean newValue = !book.isFavorite;
            viewModel.setFavorite(book.id, newValue);
            book.isFavorite = newValue;
            applyFiltersAndSort();
            // Инвертируем избранное и обновляем отображение списка
        });

        // --- Кнопка "+" для добавления книги ---
        ImageView add = findViewById(R.id.imgAddBook);
        add.setOnClickListener(v -> startActivity(new Intent(this, AddBookActivity.class)));


        // --- Долгий клик для удаления книги ---
        adapter.setOnItemLongClickListener(book -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удалить книгу")
                    .setMessage("Вы уверены, что хотите удалить эту книгу?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        viewModel.deleteBook(book);
                        fullList.remove(book);
                        applyFiltersAndSort();
                        // Удаление книги из БД и локального списка с обновлением экрана
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        // ---------- ChipGroup для фильтрации по жанрам ----------
        chipGroupStyles = findViewById(R.id.chipGroupGenres);

        // ---------- ViewModel ----------
        viewModel = new ViewModelProvider(this).get(BookListViewModel.class);
        // Получаем экземпляр ViewModel для работы с книгами

        // --- Наблюдаем за списком книг ---
        viewModel.getBook().observe(this, books -> {
            fullList.clear();
            if (books != null) fullList.addAll(books);
            // Обновляем локальный список книг при изменении данных в БД

            buildStyleChips(fullList);
            // Создаем динамические чипы для жанров
            applyFiltersAndSort();
            // Применяем фильтры и сортировку для отображения списка
        });
    }


    // ------- Создание меню Toolbar -------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        favoritesMenuItem = menu.findItem(R.id.action_favorites); // Иконка "Избранное"

        // ----- Поиск -----
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Поиск книги…");

        // --- Слушатель текста поиска ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true; // Игнорируем сабмит, используем динамический поиск
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentQuery = s;        // Сохраняем текущий текст поиска
                applyFiltersAndSort();   // Обновляем список
                return true;
            }
        });

        // --- Слушатель раскрытия/сжатия поиска ---
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem menuItem) {
                return true; // Разрешаем раскрытие
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem menuItem) {
                currentQuery = "";        // Сбрасываем поиск при закрытии
                applyFiltersAndSort();    // Обновляем список
                return true;
            }
        });

        updateFavoritesIcon(); // Обновляем иконку "Избранное"
        return true;           // Меню успешно создано
    }

    // --- Обработка нажатий на элементы меню ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_favorites) {
            showOnlyFavorites = !showOnlyFavorites; // Переключаем фильтр избранного
            updateFavoritesIcon();                  // Обновляем иконку
            applyFiltersAndSort();                  // Обновляем список
            return true;
        }

        if (item.getItemId() == R.id.action_sort) {
            showSortDialog(); // Показ диалога сортировки
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // --- Обновление иконки "Избранное" в Toolbar ---
    private void updateFavoritesIcon() {
        if (favoritesMenuItem == null) return;

        favoritesMenuItem.setIcon(
                showOnlyFavorites
                        ? R.drawable.star
                        : R.drawable.star_off
        );
    }

    // --- Показ диалога сортировки ---
    private void showSortDialog() {
        final String[] options = new String[]{"Без сортировки","По названию","По рейтингу","По жанру"};
        int checked = 0;
        switch (sortMode) {
            case NONE: checked=0; break;
            case TITLE: checked=1; break;
            case RATING: checked=2; break;
            case GENRE: checked=3; break;
        }

        new AlertDialog.Builder(this)
                .setTitle("Сортировка")
                .setSingleChoiceItems(options, checked, (dialog, which) -> {
                    switch (which) {
                        case 0: sortMode = SortMode.NONE; break;
                        case 1: sortMode = SortMode.TITLE; break;
                        case 2: sortMode = SortMode.RATING; break;
                        case 3: sortMode = SortMode.GENRE; break;
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> applyFiltersAndSort()) // Применяем сортировку
                .setNegativeButton("Отмена", null) // Отмена — ничего не делаем
                .show();
    }

    // --- Генерация чипов для жанров ---
    private void buildStyleChips(List<Book> books) {

        chipGroupStyles.removeAllViews(); // Очищаем старые чипы

        Set<String> styles = new HashSet<>();
        for (Book b : books) {
            if (!TextUtils.isEmpty(b.genre)) styles.add(b.genre); // Собираем уникальные жанры
        }

        // Чип "Все жанры"
        Chip all = createStyleChip("Все жанры");
        all.setChecked(true);
        chipGroupStyles.addView(all);
        all.setOnClickListener(v -> {
            selectedStyle = null;      // Сбрасываем фильтр по жанру
            applyFiltersAndSort();     // Применяем фильтры
        });

        // Чипы для каждого жанра
        for (String style : styles) {
            Chip chip = createStyleChip(style);
            chipGroupStyles.addView(chip);
            chip.setOnClickListener(v -> {
                selectedStyle = style;   // Сохраняем выбранный жанр
                applyFiltersAndSort();   // Применяем фильтры
            });
        }
    }

    // --- Создание отдельного чипа ---
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

    // --- Применение фильтров, поиска и сортировки ---
    private void applyFiltersAndSort() {

        currentList.clear();
        currentList.addAll(fullList); // Начинаем с полного списка

        // --- Фильтр по избранному ---
        if (showOnlyFavorites) currentList.removeIf(b -> !b.isFavorite);

        // --- Фильтр по жанру ---
        if (!TextUtils.isEmpty(selectedStyle)) currentList.removeIf(b -> !selectedStyle.equals(b.genre));

        // --- Поиск по названию ---
        if (!TextUtils.isEmpty(currentQuery)) {
            String q = currentQuery.toLowerCase();
            currentList.removeIf(b -> b.title == null || !b.title.toLowerCase().contains(q));
        }

        // --- Сортировка ---
        switch (sortMode) {
            case TITLE:
                Collections.sort(currentList, (o1, o2) -> safeString(o1.title).compareToIgnoreCase(safeString(o2.title)));
                break;
            case RATING:
                Collections.sort(currentList, (o1, o2) -> Float.compare(o2.rating, o1.rating));
                break;
            case GENRE:
                Collections.sort(currentList, (o1, o2) -> safeString(o1.genre).compareToIgnoreCase(safeString(o2.genre)));
                break;
        }

        adapter.setItems(currentList); // Обновляем адаптер
    }

    // --- Защита от null для строк ---
    private String safeString(String s) {
        return s == null ? "" : s;
    }
}

