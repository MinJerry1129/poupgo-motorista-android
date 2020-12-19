package com.view;

public class CityListItem {


    public String iCityId;
    public String vCity;


    public CityListItem(String iCityId, String vCity) {
        this.iCityId = iCityId;
        this.vCity = vCity;
    }

    @Override
    public String toString() {
        return  vCity ;
    }


}
