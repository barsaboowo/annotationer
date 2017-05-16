package com.sam.methodexecutor;


import io.reactivex.Observable;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by b on 16/5/17.
 */
public class MyExample {

    public static void main(String[] args) {
        new MessagingServiceAnnotationProcessor().process();
        Stream.generate(()-> UUID.randomUUID().toString())
                .parallel().limit(100).forEach(MessagingServiceAnnotationProcessor.dummyStream::onNext);
    }

    @MessagingServiceListener(operation = "operationThing")
    public Observable<String> doStuff(Observable<String> input){
        return input.map(i-> {
            System.out.println("doStuff got a thing: " + i);
            return "it worked: " + i;
        });
    }
}
