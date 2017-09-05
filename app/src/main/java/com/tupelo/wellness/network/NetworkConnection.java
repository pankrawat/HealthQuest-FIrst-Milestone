package com.tupelo.wellness.network;

/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class NetworkConnection {


    public String networkHit(List<NameValuePair> pairs, String url) {
        //		Logger.info("Post url---> "+url);
        //		Logger.info("Post Params---> "+pairs.toString());
        String responseEntity = null;
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            HttpResponse response = client.execute(post);
            int statuscode = getStatusCode(response);
            if (statuscode == 200) {
                responseEntity = EntityUtils.toString(response.getEntity());
            } else
                responseEntity = statuscode + "";

        } catch (Throwable e) {
            e.printStackTrace();
            return "Connection Timeout";
        }
        //		Logger.info("response:--"+responseEntity);

        return responseEntity;
    }

    private int getStatusCode(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int status_code = statusLine.getStatusCode();
        //		Logger.info("status code:--"+status_code);
        return status_code;
    }

    public static JSONObject getJSONFromUrl(String url) {
        InputStream is = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpget);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            //			e.printStackTrace();
        } catch (ClientProtocolException e) {
            //			e.printStackTrace();
        } catch (IOException e) {
            //			e.printStackTrace();
        }
        StringBuilder sb = null;
        JSONObject jObj = null;
        try {
            if (is != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
            }
        } catch (Exception e) {
            //			e.printStackTrace();
        }

        try {
            if (sb != null && sb.length() > 0)
                jObj = new JSONObject(sb.toString());
        } catch (JSONException e) {
            //			e.printStackTrace();
        }

        return jObj;

    }

    public String networkHitJson(String json, String url) {
        //Logger.info("Post url---> "+url);
        //Logger.info("Post Params---> "+json);
        String response = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        try {
            StringEntity se = new StringEntity(json);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            post.setEntity(se);
            HttpResponse httpresponse = client.execute(post);
            HttpEntity resEntity = httpresponse.getEntity();
            int status = getStatusCode(httpresponse);
            if (status == 200)
                response = EntityUtils.toString(resEntity);
        } catch (UnsupportedEncodingException e) {
            //			e.printStackTrace();
        } catch (Exception exception) {

        }
        //		Logger.info("response---> "+response);
        return response;
    }

}
