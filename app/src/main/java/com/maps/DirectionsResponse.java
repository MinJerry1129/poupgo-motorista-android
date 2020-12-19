package com.maps;

public class DirectionsResponse {
    public Directions[] polyline;
    public int distance;
    public int travelTime;

    public DirectionsResponse(Directions[] polyline, int distance, int travelTime) {
        this.polyline = polyline;
        this.distance = distance;
        this.travelTime = travelTime;
    }
}
