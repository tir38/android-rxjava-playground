package com.example.rxjavaplayground;

import rx.Observable;

public final class Manager {

    private GithubWebService mGithubWebService;

    public Manager(GithubWebService githubWebService) {
        mGithubWebService = githubWebService;
    }

    public Observable<String> getPublicEvents() {
        return mGithubWebService.getPublicEvents()
                .map(response -> response.getBody().toString());
    }
}
