package com.general.files;

import android.content.Context;

import com.general.functions.GeneralFunctions;
import com.maps.GeocodeResponse;
import com.maps.MapService;
import com.utils.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Admin on 02-07-2016.
 */
public class GetAddressFromLocation {
    double latitude;
    double longitude;
    Context mContext;
    GeneralFunctions generalFunc;

    ExecuteWebServerUrl currentWebTask;

    AddressFound addressFound;

    boolean isLoaderEnable = false;
    boolean isDestinationPointAddress = false;

    public GetAddressFromLocation(Context mContext, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setIsDestination(boolean isDestinationPointAddress){
        this.isDestinationPointAddress =isDestinationPointAddress;
    }

    public void setLoaderEnable(boolean isLoaderEnable){

        this.isLoaderEnable=isLoaderEnable;
    }
    public void execute() {
        if (currentWebTask != null) {
            currentWebTask.cancel(true);
            currentWebTask = null;
        }
        MapService.getGeocode(latitude, longitude, new MapService.GeocodeCallback() {
            @Override
            public void onGeocodeSuccess(GeocodeResponse response) {
                String completeAddress = response.CompleteAddress;

                if (addressFound != null) {
                    addressFound.onAddressFound(completeAddress,latitude,longitude);
                }
            }

            @Override
            public void onGeocodeFailure(GeocodeResponse response) {
                if(isDestinationPointAddress == true){
                    if (addressFound != null) {
                        addressFound.onAddressFound("",latitude,longitude);
                    }
                }
            }
        });
    }

    public interface AddressFound {
        void onAddressFound(String address,double latitude,double longitude);
    }

    public void setAddressList(AddressFound addressFound) {
        this.addressFound = addressFound;
    }
}
