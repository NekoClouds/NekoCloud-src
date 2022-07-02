package me.nekocloud.core.api.utils.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.api.utils.test.impl.AsynchronousTestingFactory;
import me.nekocloud.core.api.utils.test.impl.ThreadTestingFactory;

@RequiredArgsConstructor
public enum Testing {

    ASYNCHRONOUS(
            new AsynchronousTestingFactory()
    ),

    THREAD(
            new ThreadTestingFactory()
    );


    @Getter
    private final TestingFactory factory;
}
