package com.example.myapp.network;


import com.example.myapp.model.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiService {
    // Запрос GET /book
    @GET("books")
    Call<List<Book>> getBooks();
    @POST("books")
    Call<Book> createBook(@Body Book book); // создание новой книги

    @DELETE("books/{id}")
    Call<Void> deleteBook(@Path("id") int id); // удаление книги по id
}



