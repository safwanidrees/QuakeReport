package com.safwan.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);

        Log.i(LOG_TAG,"TEST : onLoaderReset caled");

        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
forceLoad();
    }

    public List<Earthquake>loadInBackground(){
        if(mUrl==null){
            return null;

        }
        List<Earthquake>earthquakes=QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}

