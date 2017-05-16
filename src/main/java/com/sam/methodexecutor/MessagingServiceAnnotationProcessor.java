package com.sam.methodexecutor;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class MessagingServiceAnnotationProcessor {

    @Value("${packages.to.scan}")
    private String packagesToScan = "com.sam.methodexecutor";

    public static final PublishSubject<String> dummyStream = PublishSubject.create();

    private final List<Disposable> streamsToDispose = Lists.newArrayList();

    @Autowired
    private final MessagingService messagingService = new MessagingService() {
        @Override
        public <T> Observable<T> clientStream(String bla, String bla2) {
            return (Observable<T>) dummyStream.map(s -> bla + " " + s);
        }

        @Override
        public <T> void sendMessage(T thing) {
            System.out.println("Sending a thing: " + thing);
        }
    };

    @PostConstruct
    public void process() {
        new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packagesToScan))
                .addScanners(new MethodAnnotationsScanner()))
                .getMethodsAnnotatedWith(MessagingServiceListener.class)
                .forEach(
                        method -> {
                            try {
                                MessagingServiceListener s
                                        = method.getDeclaredAnnotation(MessagingServiceListener.class);
                                Observable invoke = (Observable) method.invoke(
                                        method.getDeclaringClass().newInstance(),
                                        messagingService.clientStream(s.operation(), "bla"));

                                streamsToDispose.add(invoke.subscribe(i -> messagingService.sendMessage(i)));
                                System.out.println(s);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

    }

    @PreDestroy
    public void dispose() {
        streamsToDispose.forEach(Disposable::dispose);
    }
}
