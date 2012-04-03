package com.dofideas.geiger2;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class pachubeUpdate extends AsyncTask<String, Void, Void> {
	private Context contextRef;
	private String key;
	pachubeUpdate(Context andContext, String key){
		this.contextRef=andContext;		
		this.key=key;
	}
	
	protected Void doInBackground(String... p) {
		String sieverts=p[0];
		String lon=p[1];
		String lat=p[2];
		sendUpdate(sieverts,lon,lat);
        return null ;
    }
	
	void sendUpdate( String sieverts,  String lon, String lat) {
		JSONObject object = new JSONObject();
		try {
			object.put("version", "1.0.0");
			
			JSONObject location = new JSONObject();
			location.put( "lat",lat );
			location.put( "lon",lon);
			object.put("location", location);
			
				JSONObject dataStream1 = new JSONObject();
					dataStream1.put("current_value" ,sieverts);
					dataStream1.put("id" ,"movilSergio" );
					JSONArray dataStreamArray = new JSONArray();
				dataStreamArray.put(dataStream1);
			object.put("datastreams",dataStreamArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		URL url;
		try {
			url = new URL("http://api.pachube.com/v2/feeds/53341");

		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setDoInput(true);

		httpCon.setRequestMethod("PUT");
		httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpCon.addRequestProperty("X-PachubeApiKey", "pU7dy9u2qHbbGPW3o3uTZtNkhdeSAKxtbERjaCtXRVhkaz0g");
		OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
		Log.d("qq",object.toString());
		out.write(object.toString());
		out.flush();
		out.close();
		
		InputStream is = httpCon.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }
	      rd.close();
	      Log.d("qq",response.toString());
	      return;
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
