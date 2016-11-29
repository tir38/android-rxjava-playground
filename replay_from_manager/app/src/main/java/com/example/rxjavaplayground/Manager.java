package com.example.rxjavaplayground;


import rx.Observable;

public final class Manager {

    private GithubWebService mGithubWebService;
    private Observable<String> mPublicEventsObservable;

    public Manager(GithubWebService githubWebService) {
        mGithubWebService = githubWebService;
    }

    public Observable<String> getPublicEvents() {
        if (mPublicEventsObservable == null) {
            mPublicEventsObservable = mGithubWebService.getPublicEvents()
                    .map(response -> response.getBody().toString())
                    .publish()
                    .refCount()
            ;
        }
        return mPublicEventsObservable;
    }
}
