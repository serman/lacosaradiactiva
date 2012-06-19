package com.lacosaradioactiva.geiger.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class SendXML {
	String ADDRESS= "http://www.yoursite.com/script.php";
	HttpClient httpclient;
	
	public void start(){
		httpclient = new DefaultHttpClient();
	}
	public void postValues(String seq, String cpm, String us,String lon,String lat) {
	    // Create a new HttpClient and Post Header
	    
	    HttpPost httppost = new HttpPost("");
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("seq", seq));
	        nameValuePairs.add(new BasicNameValuePair("cpm", cpm));
	        nameValuePairs.add(new BasicNameValuePair("us", us));
	        nameValuePairs.add(new BasicNameValuePair("lon", lon));
	        nameValuePairs.add(new BasicNameValuePair("lat", lat));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	}
	
}
