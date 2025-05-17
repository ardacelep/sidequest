package com.tablefour.sidequest.entities.enums;

public enum DateSearchType {
    EXACT, // Exact date match
    BEFORE, // Before specified date
    AFTER, // After specified date
    BETWEEN, // Between two dates
    OVERLAPPING // Job duration overlaps with given period
}