package com.sam.methodexecutor;

import io.reactivex.Observable;

/**
 * Created by b on 16/5/17.
 */
public interface MessagingService {

    <T> Observable<T> clientStream(String bla, String bla2);

    <T> void sendMessage(T thing);
}
