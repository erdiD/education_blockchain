package de.deutschebahn.ilv.app;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.time.Duration;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 25.10.2017.
 */
@PerformanceCheck
public class PerformanceInterceptor {

    @Inject
    Logger logger;

    @AroundInvoke
    public Object measure(InvocationContext context) throws Exception {
        long startTime = System.currentTimeMillis();
        Object o = context.proceed();
        long duration = System.currentTimeMillis() - startTime;

        logger.info("[measure] " + getLogMessage(context, duration));

        return o;
    }

    private String getLogMessage(InvocationContext context, long millis) {
        Duration duration = Duration.ofMillis(millis);
        return String.format("Call %s:%s takes %s %s",
                context.getTarget().getClass(), context.getMethod().getName(),
                duration.toString(), "seconds"
        );
    }

}
