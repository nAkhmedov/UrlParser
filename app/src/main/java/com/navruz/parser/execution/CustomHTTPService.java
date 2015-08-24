package com.navruz.parser.execution;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Navruz on 22.08.2015.
 */
public interface CustomHTTPService {

    @GET("/")
    void getAutoResponse(Callback<String> res);

}
