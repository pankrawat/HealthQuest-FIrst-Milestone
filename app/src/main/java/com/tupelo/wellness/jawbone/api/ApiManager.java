/**
 * @author Omer Muhammed
 * Copyright 2014 (c) Jawbone. All rights reserved.
 *
 */
package com.tupelo.wellness.jawbone.api;


import com.tupelo.wellness.helper.Constants;


/**
 * Main class that handles all the API calls
 */
public class ApiManager {

    private static retrofit.RestAdapter restAdapter;
    private static RestApiInterface restApiInterface;
    private static ApiHeaders restApiHeaders;

    private static retrofit.RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            restAdapter = new retrofit.RestAdapter.Builder()
                .setRequestInterceptor(getRequestInterceptor())
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .setErrorHandler(new CustomErrorHandler())
                .setEndpoint(Constants.API_URL)
                .build();
        }
        return restAdapter;
    }

    //TODO make this more robust
    private static class CustomErrorHandler implements retrofit.ErrorHandler {
        @Override public Throwable handleError(retrofit.RetrofitError cause) {
            retrofit.client.Response r = cause.getResponse();
            if (r != null && r.getStatus() == 401) {
                return cause.getCause();
            }
            return cause;
        }
    }

    public static RestApiInterface getRestApiInterface() {
        restAdapter = getRestAdapter();
        if (restApiInterface == null) {
            restApiInterface = restAdapter.create(RestApiInterface.class);
        }
        return restApiInterface;
    }

    public static ApiHeaders getRequestInterceptor() {
        if (restApiHeaders == null) {
            restApiHeaders = new ApiHeaders();
        }
        return restApiHeaders;
    }
}