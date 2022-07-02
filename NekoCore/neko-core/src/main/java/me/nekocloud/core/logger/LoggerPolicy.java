package me.nekocloud.core.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.EventRoute;

public class LoggerPolicy implements AsyncQueueFullPolicy {

    @Override
    public EventRoute getRoute(final long backgroundThreadId, final Level level) {
        return EventRoute.ENQUEUE;
    }
}