package com.example.rxjavaplayground;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private Manager mManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = FauxDependencyInjection.injectManager();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "getting events from manager");

        mManager.getPublicEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        string -> Log.d(TAG, "got string: " + string),
                        throwable -> Log.e(TAG, "error"),
                        () -> Log.d(TAG, "complete")
                );
    }
}
