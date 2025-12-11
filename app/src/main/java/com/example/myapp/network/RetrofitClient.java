package com.example.myapp.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    // URL  мок-сервера. ОБЯЗАТЕЛЬНО заканчивается на /
    private static final String BASE_URL =
            "https://8c0d6884-19bc-4d8d-9d7d-d61e067e5dab.mock.pstmn.io/";
    private static Retrofit retrofit;
    public static Retrofit get() {
        if (retrofit == null) {
// Интерсептор, чтобы смотреть запросы/ответы (очень полезно!)
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
// Клиент OkHttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build();
// Создаём Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    // Быстрый доступ к API
    public static ApiService api() {
        return get().create(ApiService.class);
    }
}

