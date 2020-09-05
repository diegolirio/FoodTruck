package com.example.foodtruck;

import rx.Observable;
import rx.schedulers.Schedulers;

public interface RxCommon {

        default Observable<Integer> getObservableParallel() {
            return Observable.just(1).observeOn(Schedulers.io());
        }
}
