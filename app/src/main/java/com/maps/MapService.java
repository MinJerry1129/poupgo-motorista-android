package com.maps;

import com.general.functions.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.rest.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapService {
    public interface MapServiceCallback {
        void onDirectionsSuccess(DirectionsResponse response);
        void onDirectionsFailed(DirectionsResponse response);
    }

    public interface GeocodeCallback {
        void onGeocodeSuccess(GeocodeResponse response);
        void onGeocodeFailure(GeocodeResponse response);
    }

    public static void getDirections(LatLng origin, LatLng destination, MapServiceCallback callback) {

        Directions originDirections = new Directions(origin.latitude, origin.longitude);
        Directions destinationDirections = new Directions(destination.latitude, destination.longitude);
        DirectionsRequest request = new DirectionsRequest(originDirections, destinationDirections);
        Call<DirectionsResponse> call = RestClient.getClient().getDirections(request);

        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Object body = response.body();
                Utils.printLog("onresponsecallback", "response = " + new Gson().toJson(body));
                if (response.isSuccessful()) {
                    callback.onDirectionsSuccess(response.body());
                } else {
                    callback.onDirectionsFailed(response.body());
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Utils.printLog("DataError", "::" + t.getMessage());
            }
        });
    }

    public static void getGeocode(Double latitude, Double longitude, GeocodeCallback callback) {

        GeocodeOrigin geocodeOrigin = new GeocodeOrigin(latitude, longitude);
        GeocodeRequest geocodeRequest = new GeocodeRequest(geocodeOrigin);
        Call<GeocodeResponse> call = RestClient.getClient().getGeocode(geocodeRequest);

        call.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                if(response.isSuccessful()) {
                    callback.onGeocodeSuccess(response.body());
                } else {
                    callback.onGeocodeFailure(response.body());
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {

            }
        });
    }
}
