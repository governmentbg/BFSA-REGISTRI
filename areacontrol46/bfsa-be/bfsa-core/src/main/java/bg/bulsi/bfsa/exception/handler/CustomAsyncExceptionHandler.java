package bg.bulsi.bfsa.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(final Throwable throwable, final Method method, final Object... obj) {
		log.error("Method name - " + method.getName(), throwable);
		for (Object param : obj) {
			log.error("Parameter value - " + param);
		}
	}
}