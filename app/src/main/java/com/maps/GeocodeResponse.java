package com.maps;

public class GeocodeResponse {
    public String CompleteAddress;
    public String Country;
    public String State;
    public String City;
    public String District;
    public String Street;
    public String HouseNumber;
    public String PostalCode;

    public GeocodeResponse (String CompleteAddress, String Country, String State, String City, String District, String Street, String HouseNumber, String PostalCode) {
        this.CompleteAddress = CompleteAddress;
        this.Country = Country;
        this.State = State;
        this.City = City;
        this.District = District;
        this.Street = Street;
        this.HouseNumber = HouseNumber;
        this.PostalCode = PostalCode;
    }
}
