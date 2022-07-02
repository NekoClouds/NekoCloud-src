package me.nekocloud.core.api.utils.test.impl;

import lombok.SneakyThrows;
import me.nekocloud.core.api.utils.test.TestingFactory;
import me.nekocloud.core.api.utils.test.ThreadTest;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ThreadTestingFactory extends TestingFactory<ThreadTest> {

    public ThreadTestingFactory() {
        super(ThreadTest.class);
    }

    @Override
    @SneakyThrows
    public void executeTests(final @NotNull Object objectWithTestMethods, Consumer<Method> testMethodConsumer) {
        Method[] testMethodArray = objectWithTestMethods.getClass().getMethods();

        for (Method testMethod : testMethodArray) {
            if (!testMethodCollection.contains(testMethod)) {
                continue;
            }

            if (testMethodConsumer != null) {
                testMethodConsumer.accept(testMethod);
            }

            testMethod.invoke(objectWithTestMethods, new Object[testMethod.getParameterCount()]);
        }
    }
}
