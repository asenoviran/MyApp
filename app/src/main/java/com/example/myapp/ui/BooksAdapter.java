package com.example.myapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<Book> items = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Book book);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public void setItems(List<Book> list) {
        items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = items.get(position);
        holder.textTitle.setText(book.title);
        holder.textAuthor.setText(book.author);
        holder.textGenre.setText(book.genre);
        holder.iconFavorite.setImageResource(
                book.isFavorite ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.onItemClick(book);
        });

        holder.iconFavorite.setOnClickListener(v -> {
            if (favoriteClickListener != null) favoriteClickListener.onFavoriteClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textAuthor, textGenre;
        ImageView iconFavorite;

        BookViewHolder(@NonNull View v) {
            super(v);
            textTitle = v.findViewById(R.id.textTitle);
            textAuthor = v.findViewById(R.id.textAuthor);
            textGenre = v.findViewById(R.id.textGenre);
            iconFavorite = v.findViewById(R.id.iconFavorite);
        }
    }
}
