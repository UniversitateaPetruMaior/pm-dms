package com.openkm.util;

import java.util.Timer;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.log4j.LogManager;

public class Log4JInitializer implements LifecycleListener {
	private String configFile;
	private static Timer lwdTimer;
	private static LoggerWatchdog lwd;
	
	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		if (Lifecycle.BEFORE_START_EVENT.equals(event.getType())) {
			initializeWathdog();
		} else if (Lifecycle.BEFORE_STOP_EVENT.equals(event.getType())) {
			finalizeWathdog();
		}
	}
	
	private void initializeWathdog() {
		// Start log4j watchdog
		lwdTimer = new Timer("Logger Watchdog");
		lwd = new LoggerWatchdog(configFile);
		lwdTimer.schedule(lwd, 0, 60 * 1000); // First now, next each 1 min
		
		// shutdown log4j (and its monitor thread) on shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				finalizeWathdog();
				LogManager.shutdown();
			}
		});
	}
	
	private void finalizeWathdog() {
		// Stop log4j watchdog
		lwd.cancel();
		lwdTimer.cancel();
	}
}
