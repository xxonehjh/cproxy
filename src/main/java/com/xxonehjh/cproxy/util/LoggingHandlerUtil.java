package com.xxonehjh.cproxy.util;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class LoggingHandlerUtil {

	private static final LoggingHandler INFO = new LoggingHandler(LogLevel.INFO);
	private static final LoggingHandler ERROR = new LoggingHandler(LogLevel.INFO);

	public static LoggingHandler getInstance(boolean debug) {
		if (debug) {
			return INFO;
		}
		return ERROR;
	}

}
