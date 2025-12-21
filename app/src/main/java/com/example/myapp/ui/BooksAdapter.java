package com.example.myapp.ui;

import com.bumptech.glide.Glide;
import com.example.myapp.R;
import com.example.myapp.model.Book;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

// Адаптер для RecyclerView: отображает список книг, поддерживает клики на элемент, избранное и долгий клик для удаления
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.Holder> {

    private List<Book> items = new ArrayList<>(); // список книг

    // --- Интерфейсы для обработки кликов ---
    public interface OnItemClickListener { void onItemClick(Book book, ImageView img); } // обычный клик
    public interface OnFavoriteClickListener { void onFavoriteClick(Book book); } // клик на избранное
    public interface OnItemLongClickListener { void onItemLongClick(Book book); } // долгий клик

    private OnItemClickListener clickListener;
    private OnFavoriteClickListener favListener;
    private OnItemLongClickListener longClickListener;

    // --- Установка списка книг и обновление RecyclerView ---
    public void setItems(List<Book> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    // --- Установка слушателей кликов ---
    public void setOnItemClickListener(OnItemClickListener l) { clickListener = l; }
    public void setOnFavoriteClickListener(OnFavoriteClickListener l) { favListener = l; }
    public void setOnItemLongClickListener(OnItemLongClickListener l) { longClickListener = l; }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание ViewHolder для каждой карточки книги
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Book book = items.get(pos);

        // --- Заполнение данных ---
        h.textTitle.setText(book.title);
        h.textAuthor.setText(book.author);
        h.textGenre.setText(book.genre);

        String url = book.imageUrl != null ? book.imageUrl.trim() : null;

        Glide.with(h.itemView.getContext()) // загрузка изображения
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .centerCrop()
                .into(h.image);

        // --- Отображение состояния избранного ---
        h.favorite.setImageResource(
                book.isFavorite
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        // --- Анимация появления карточки ---
        h.itemView.setAlpha(0f);
        h.itemView.setTranslationY(40);
        h.itemView.animate().alpha(1f).translationY(0).setDuration(200).start();

        // --- Обработка клика по карточке ---
        h.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(book, h.image);
        });

        // --- Обработка долгого клика по карточке (удаление) ---
        h.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(book);
                return true;
            }
            return false;
        });

        // --- Обработка клика по кнопке избранного ---
        h.favorite.setOnClickListener(v -> {
            if (favListener != null) favListener.onFavoriteClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return items.size(); // количество элементов в списке
    }

    // --- ViewHolder: хранит ссылки на элементы UI одной карточки ---
    static class Holder extends RecyclerView.ViewHolder {
        ImageView image, favorite;
        TextView textTitle, textAuthor, textGenre;

        public Holder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imageBook);
            favorite = v.findViewById(R.id.iconFavorite);
            textTitle = v.findViewById(R.id.textTitle);
            textAuthor = v.findViewById(R.id.textAuthor);
            textGenre = v.findViewById(R.id.textGenre);
        }
    }
}
