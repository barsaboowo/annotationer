package com.sam.methodexecutor;

import io.reactivex.Observable;

/**
 * Created by b on 17/5/17.
 */
public class AnyOldClass {

    @MessagingServiceListener(operation = "other")
    public Observable<Integer> anotherOne(Observable<String> incoming){
        return incoming.map(i->{
            System.out.println("I got a thing: " + i);
            return i.length();
        });
    }
}
