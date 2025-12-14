package com.example.myapp.ui;

import com.bumptech.glide.Glide;
import com.example.myapp.R;
import com.example.myapp.model.Book;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.*;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.Holder> {

    private List<Book> items = new ArrayList<>();

    // Интерфейсы для кликов по элементу и по избранному
    public interface OnItemClickListener {
        void onItemClick(Book book, ImageView img);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Book book);
    }

    private OnItemClickListener clickListener;
    private OnFavoriteClickListener favListener;

    // Установка списка книг
    public void setItems(List<Book> list) {
        this.items = list;
        notifyDataSetChanged(); // обновляем адаптер
    }

    // Установка слушателя клика по карточке
    public void setOnItemClickListener(OnItemClickListener l) {
        clickListener = l;
    }

    // Установка слушателя клика по кнопке избранного
    public void setOnFavoriteClickListener(OnFavoriteClickListener l) {
        favListener = l;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем ViewHolder из layout карточки книги
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Book book = items.get(pos);

        // Заполняем текстовые поля
        h.textTitle.setText(book.title);
        h.textAuthor.setText(book.author);
        h.textGenre.setText(book.genre);

        String url = book.imageUrl;
        if (url != null) url = url.trim();

        Glide.with(h.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .centerCrop()
                .into(h.image);

        // Установка состояния кнопки избранного
        h.favorite.setImageResource(
                book.isFavorite
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        // Анимация появления карточки
        h.itemView.setAlpha(0f);
        h.itemView.setTranslationY(40);
        h.itemView.animate().alpha(1f).translationY(0).setDuration(200).start();

        // Клик по всей карточке
        h.itemView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onItemClick(book, h.image);
        });

        // Клик по кнопке избранного
        h.favorite.setOnClickListener(v -> {
            if (favListener != null)
                favListener.onFavoriteClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return items.size(); // возвращаем размер списка
    }

    // ViewHolder хранит ссылки на элементы карточки
    static class Holder extends RecyclerView.ViewHolder {
        ImageView image, favorite;
        TextView textTitle, textAuthor, textGenre;

        public Holder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imageBook); // картинка книги
            favorite = v.findViewById(R.id.iconFavorite); // иконка избранного
            textTitle = v.findViewById(R.id.textTitle); // название книги
            textAuthor = v.findViewById(R.id.textAuthor); // автор книги
            textGenre = v.findViewById(R.id.textGenre); // жанр книги
        }
    }
}


