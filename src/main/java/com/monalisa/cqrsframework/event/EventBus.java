package com.monalisa.cqrsframework.event;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Event Bus
 */
@Setter
@Component
public class EventBus implements IEventBus {
    Logger logger = LoggerFactory.getLogger(EventBus.class);

    @Autowired
    private EventHub eventHub;

    @Override
    public void fire(Object event) {
        eventHub.handleEvent(event);
    }

    @Override
    public void asyncFire(Object event) {
        executor.execute(new EventTask(eventHub, event));
    }

    private static class EventTask implements Runnable {
        private Object event;
        private EventHub eventHub;

        public EventTask(EventHub eventHub, Object event) {
            this.eventHub = eventHub;
            this.event = event;
        }

        @Override
        public void run() {
            eventHub.handleEvent(event);
        }
    }

    private Executor executor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new EventHandlerThreadFactory());

    private static class EventHandlerThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "EventHandler-" + count.getAndIncrement());
        }
    }

/*
    Exception will be thrown out

    private void handleException(EventHandlerI handler, Event event, Exception exception) {
        logger.error("Process event error, EventHandler:["+handler.getClass()+"] Event:"+event+" ErrorMessage:"+exception.getMessage(), exception);
    }
 */
}
