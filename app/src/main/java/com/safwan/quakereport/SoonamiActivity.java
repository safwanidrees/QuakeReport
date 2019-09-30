package com.safwan.quakereport;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

public class SoonamiActivity extends AppCompatActivity {
    public static final String LOG_TAG = SoonamiActivity.class.getSimpleName();
    private static final String USGS_REQUEST_URL="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2012-01-01&endtime=2012-12-01&minmagnitude=6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soonami);

        TusnamiAsynTask task=new TusnamiAsynTask();
task.execute();

    }

    private class TusnamiAsynTask extends AsyncTask<URL,Void,Event>{
        @Override
        protected Event doInBackground(URL... urls) {
URL url=createUrl(USGS_REQUEST_URL);
String jsonResponce="";
try{
    jsonResponce=makeHttpRequest(url);
}
catch (IOException e)
{

}
Event earthquake=extractFeatureFromJson(jsonResponce);
return earthquake;

        }

        protected void onPostExecute(Event earthquake) {
            if (earthquake == null) {
                return;
            }

            updateUi(earthquake);
        }

    }

    private void updateUi(Event earthquake) {
        // Display the earthquake title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(earthquake.title);

        // Display the earthquake date in the UI
        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(getDateString(earthquake.time));

        // Display whether or not there was a tsunami alert in the UI
        TextView tsunamiTextView = (TextView) findViewById(R.id.tsunami_alert);
        tsunamiTextView.setText(getTsunamiAlertString(earthquake.tsunamiAlert));
    }

    /**
     * Returns a formatted date and time string for when the earthquake happened.
     */
    private String getDateString(long timeInMilliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss z");
        return formatter.format(timeInMilliseconds);
    }

    /**
     * Return the display string for whether or not there was a tsunami alert for an earthquake.
     */
    private String getTsunamiAlertString(int tsunamiAlert) {
        switch (tsunamiAlert) {
            case 0:
                return getString(R.string.alert_no);
            case 1:
                return getString(R.string.alert_yes);
            default:
                return getString(R.string.alert_not_available);
        }
    }

    private Event extractFeatureFromJson(String earthquakeJSON) {
    try{
         JSONObject baseJsonResponce=new JSONObject(earthquakeJSON);
        JSONArray featurearray=baseJsonResponce.getJSONArray("features");
        if(featurearray.length()>0){

            JSONObject firstfeature= featurearray.getJSONObject(0);
            JSONObject properties = firstfeature.getJSONObject("properties");
            String title = properties.getString("title");
            long time = properties.getLong("time");
            int tsunamiAlert = properties.getInt("tsunami");
            return new Event(title, time, tsunamiAlert);


        }
    }
    catch (JSONException e)
    {
        Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);

    }
    return null;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponce="";
        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try{
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
inputStream=urlConnection.getInputStream();
jsonResponce=readFromStream(inputStream);
        } catch (IOException e){

        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponce;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //BufferedReader is used to arrange the string
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
               //it is always in BufferedReader
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private URL createUrl(String stringUrl  ) {
        URL url=null;
        try{
            url=new URL(stringUrl);

        }catch (MalformedURLException exception) {
        Log.e(LOG_TAG,"Error with creating Url",exception);
        }
        return url;
    }

}
