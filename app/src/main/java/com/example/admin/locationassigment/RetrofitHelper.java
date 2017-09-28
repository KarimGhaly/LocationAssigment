package com.example.admin.locationassigment;

import com.example.admin.locationassigment.model.GeocodeResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 9/27/2017.
 */

public class RetrofitHelper {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    public static final String APIKEY = "AIzaSyAa-Gstjyiu59GQsmf7YV4CVFL5fzfaoKw";

    public static Retrofit create(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit;
    }
    public static Call<GeocodeResponse> getByLatLong(String Lat)
    {
        Retrofit retrofit = create();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getbyLatLong(Lat,APIKEY);
    }
    public static Call<GeocodeResponse> getByAddress(String address)
    {
        Retrofit retrofit = create();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getbyAddress(address,APIKEY);
    }

    interface APIService{
        @GET ("json")
        Call<GeocodeResponse> getbyLatLong(@Query("latlng")String Lat, @Query("key") String APIKEY);

        @GET("json")
        Call<GeocodeResponse> getbyAddress(@Query("address") String Address,@Query("key") String APIKEY);
    }
}
