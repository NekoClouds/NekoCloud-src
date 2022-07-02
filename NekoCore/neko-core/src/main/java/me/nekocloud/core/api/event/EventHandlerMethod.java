package me.nekocloud.core.api.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Getter
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@AllArgsConstructor
public class EventHandlerMethod {

    Object listener;
    Method method;

    public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        long start = System.nanoTime();
        method.invoke(listener, event);

        long elapsed = System.nanoTime() - start;

        if (elapsed > 230000000L) {
            log.info("Event {} took {}ns to process for {}!", event, elapsed, this.listener );
        }
    }
}

