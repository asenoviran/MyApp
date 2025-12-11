package com.example.myapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.io.Serializable;

@Entity(tableName = "books")
public class Book implements Serializable {
    @PrimaryKey
    public int id;

    public String title;      // название книги
    public String author;     // автор
    public String genre;      // жанр
    public int year;          // год издания
    public String country;    // страна
    public String region;     // регион
    public String description;// описание

    // Локальные данные пользователя
    @ColumnInfo(defaultValue = "0")
    public boolean isFavorite;
    public String comment;
    @ColumnInfo(defaultValue = "0")
    public float rating;
}
