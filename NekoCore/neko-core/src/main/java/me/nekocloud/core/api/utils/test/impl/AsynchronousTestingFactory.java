package me.nekocloud.core.api.utils.test.impl;

import me.nekocloud.base.util.query.AsyncUtil;
import me.nekocloud.core.api.utils.test.AsyncTest;
import me.nekocloud.core.api.utils.test.TestingFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class AsynchronousTestingFactory extends TestingFactory<AsyncTest> {

    public AsynchronousTestingFactory() {
        super(AsyncTest.class);
    }

    @Override
    public void executeTests(final @NotNull Object objectWithTestMethods, Consumer<Method> testMethodConsumer) {
        Method[] testMethodArray = objectWithTestMethods.getClass().getMethods();

        for (Method testMethod : testMethodArray) {
            if (!testMethodCollection.contains(testMethod)) {
                continue;
            }

            AsyncUtil.submitThrowsAsync(() -> {

                if (testMethodConsumer != null) {
                    testMethodConsumer.accept(testMethod);
                }

                testMethod.invoke(objectWithTestMethods, new Object[testMethod.getParameterCount()]);
            });
        }
    }

}
