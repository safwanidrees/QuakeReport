package com.safwan.quakereport;

public class Earthquake {

    private double mMagnitude;


    private String mUrl;


    private String mLocation;


    private long mTimeInMilliseconds;

    public Earthquake(double magnitude, String location, long timeInMilliseconds,String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl=url;

    }

    public double getMagnitude() {
        return mMagnitude;
    }


    public String getLocation() {
        return mLocation;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }


    public String getUrl() {
        return mUrl;
    }


}
