package com.example.rxjavaplayground;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public final class FauxDependencyInjection {

    private static GithubWebService mGithubWebService;
    private static Manager mManager;

    public static GithubWebService injectWebService() {

        if (mGithubWebService == null) {

            OkHttpClient client = new OkHttpClient();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.github.com")
                    .setClient(new OkClient(client))
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    .build();

            mGithubWebService = restAdapter.create(GithubWebService.class);
        }
        return mGithubWebService;
    }

    public static Manager injectManager() {

        if (mManager == null) {
            mManager = new Manager(injectWebService());
        }
        return mManager;
    }
}
