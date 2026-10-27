package com.assignment5.domain;

public class NoSuitableRouteException extends RuntimeException {
    public NoSuitableRouteException(String message) {
        super(message);
    }
}
