package com.glory.company.sociedad.Retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiAdapter {
    private var retrofit: Retrofit? = null

     fun getClient(baseUrl: String?): Retrofit? {
         // Creamos un interceptor y le indicamos el log level a usar

         // Creamos un interceptor y le indicamos el log level a usar
         val logging = HttpLoggingInterceptor()
         logging.level = HttpLoggingInterceptor.Level.BODY

         // Asociamos el interceptor a las peticiones

         // Asociamos el interceptor a las peticiones
         val httpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging)
             .readTimeout(110, TimeUnit.SECONDS)
             .connectTimeout(110, TimeUnit.SECONDS)
             .build();


        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }
        return retrofit
    }
}