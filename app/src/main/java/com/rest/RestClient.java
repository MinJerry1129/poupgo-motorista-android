package com.rest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.poupgo.driver.BuildConfig;
import com.maps.DirectionsRequest;
import com.maps.DirectionsResponse;
import com.maps.GeocodeRequest;
import com.maps.GeocodeResponse;
import com.utils.CommonUtilities;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;


public class RestClient {

    private static ApiInterface apiInterface;
    private static String baseUrl = CommonUtilities.SERVER;

    public static ApiInterface getClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient okHttpClient = null;

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();


        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = client.create(ApiInterface.class);
        return apiInterface;
    }


    public static Gson getGSONBuilder() {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    if (src == src.longValue())
                        return new JsonPrimitive("" + src.longValue());
                    return new JsonPrimitive("" + src);
                }).create();

        return gson;
    }

    public interface ApiInterface {

        @FormUrlEncoded
        @POST(CommonUtilities.SERVER_WEBSERVICE_PATH)
        Call<Object> getResponse(@FieldMap Map<String, String> params);

        @GET
        Call<Object> getResponse(@Url String url);

        @POST(CommonUtilities.SERVER + "routes/here/directions.php/")
        Call<DirectionsResponse> getDirections(@Body DirectionsRequest directions);

        @POST(CommonUtilities.SERVER + "routes/here/placeDetails.php")
        Call<GeocodeResponse> getGeocode(@Body GeocodeRequest geocode);

        @Multipart
        @POST(CommonUtilities.SERVER_WEBSERVICE_PATH)
        Call<Object> uploadData(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> params);
    }

}

