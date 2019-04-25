package com.example.voiceassistant;

import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Aphorism {
//    public static class QuoteText {
//        @SerializedName("quoteText")
//        public String aphorism;
//    }
    public static class Author {
        @SerializedName("quoteAuthor")
        public String author;
    }
    public static class QuoteText {
        @SerializedName("quoteText")
        public String aphorism;
        @SerializedName("quoteAuthor")
        public String author;
    }
    public interface AphorismService {
        @GET("/api/1.0/?method=getQuote")
        Call<QuoteText> getResult(@Query("format") String format, @Query("lang") String lang);
    }
    public static void get(final Consumer<String> callback) {
        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.forismatic.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<QuoteText> call = retrofit
                .create(AphorismService.class)
                .getResult("json","ru");

        call.enqueue(new Callback<QuoteText>() {
            @Override
            public void onResponse(Call<QuoteText> call, Response<QuoteText> response) {
                QuoteText quoteText = response.body();
                String text;

                if(quoteText.author.isEmpty()) {
                    text = ""+quoteText.aphorism + " Автор: отсутсвует ";
                } else {
                    text = ""+quoteText.aphorism + " Автор: " + quoteText.author;
                }
                callback.accept(text);
            }

            @Override
            public void onFailure(Call<QuoteText> call, Throwable t) {

            }
        });
    }
}
