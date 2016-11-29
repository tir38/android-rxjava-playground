This is a regular occurance where our Manager class exposes calls to our webservice. Any fragtivity can call the manager:


```
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
```

This results in duplcate calls. This isn't *usually* a problem when only one "thing" is making a request per screen. It might be overkill to hit the web service for every screen but this way we ensure data is always up to date.

But what happens when two things that are on the screen at the same time both make a request? We get two calls to the web service when only one was needed. Here our Fragment and Activity are both requesting the same data:


```
11-29 15:40:33.484 11116-11116/com.example.rxjavaplayground D/MainActivity: getting events from manager
11-29 15:40:33.504 11116-11116/com.example.rxjavaplayground D/MainFragment: getting events from manager
11-29 15:40:33.519 11116-11159/com.example.rxjavaplayground D/Retrofit: ---> HTTP GET https://api.github.com/events
11-29 15:40:33.520 11116-11160/com.example.rxjavaplayground D/Retrofit: ---> HTTP GET https://api.github.com/events
...
11-29 15:40:34.832 11116-11160/com.example.rxjavaplayground D/Retrofit: <--- HTTP 200 https://api.github.com/events (1311ms)
11-29 15:40:34.850 11116-11116/com.example.rxjavaplayground D/MainFragment: got string: TypedByteArray[length=63727]
11-29 15:40:34.850 11116-11116/com.example.rxjavaplayground D/MainFragment: complete
11-29 15:40:35.012 11116-11159/com.example.rxjavaplayground D/Retrofit: <--- HTTP 200 https://api.github.com/events (1492ms)
11-29 15:40:35.022 11116-11116/com.example.rxjavaplayground D/MainActivity: got string: TypedByteArray[length=64188]
11-29 15:40:35.022 11116-11116/com.example.rxjavaplayground D/MainActivity: complete
```

Instead of coordinating data between them, let's solve this with RxJava:

We hold our "cold" observable as singleton. By using `publish`/`refcount` we are able to make it "hot" when anyone connects to it:

```
public Observable<String> getPublicEvents() {
    if (mPublicEventsObservable == null) {
        mPublicEventsObservable = mGithubWebService.getPublicEvents()
				.map(response -> response.getBody().toString())
                .publish()
                .refCount();
    }
    return mPublicEventsObservable;
}
```

resulting in a single web request and shared data between all callers:

```
11-29 15:50:09.819 27327-27327/com.example.rxjavaplayground D/MainActivity: getting events from manager
11-29 15:50:09.834 27327-27327/com.example.rxjavaplayground D/MainFragment: getting events from manager
11-29 15:50:09.843 27327-27357/com.example.rxjavaplayground D/Retrofit: ---> HTTP GET https://api.github.com/events
...
11-29 15:50:10.757 27327-27357/com.example.rxjavaplayground D/Retrofit: <--- HTTP 200 https://api.github.com/events (913ms)
11-29 15:50:10.926 27327-27327/com.example.rxjavaplayground D/MainActivity: got string: TypedByteArray[length=84137]
11-29 15:50:10.926 27327-27327/com.example.rxjavaplayground D/MainFragment: got string: TypedByteArray[length=84137]
11-29 15:50:10.926 27327-27327/com.example.rxjavaplayground D/MainActivity: complete
11-29 15:50:10.927 27327-27327/com.example.rxjavaplayground D/MainFragment: complete
```

### Cache-like behavior
Let's say that based on the synchronisity of the the app, the Fragment makes its request **after** the Activity's request has completed. This will result again in two web requests. I would think the thing to do would be to use `replay`:

```
public Observable<String> getPublicEvents() {
    if (mPublicEventsObservable == null) {
        mPublicEventsObservable = mGithubWebService.getPublicEvents()
                .map(response -> response.getBody().toString())
                .publish()
                .refCount()
                .replay(1)
        ;
    }
    return mPublicEventsObservable;
}
```

However this results in *neither* fragment/activity subscribing:

```
11-29 16:14:16.642 16530-16530/com.example.rxjavaplayground D/MainActivity: getting events from manager
...
11-29 16:14:20.661 16530-16530/com.example.rxjavaplayground D/MainFragment: getting events from manager
```




