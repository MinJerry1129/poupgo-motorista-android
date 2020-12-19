package com.maps;

public class DirectionsRequest {

    private Directions origin;
    private Directions destination;

    public DirectionsRequest(Directions origin, Directions destination ) {
        this.origin = origin;
        this.destination = destination;
    }
}
