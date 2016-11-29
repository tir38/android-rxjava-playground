package com.example.rxjavaplayground;

import retrofit.client.Response;
import retrofit.http.GET;
import rx.Observable;

public interface GithubWebService {

    @GET("/events")
    Observable<Response> getPublicEvents();

}
