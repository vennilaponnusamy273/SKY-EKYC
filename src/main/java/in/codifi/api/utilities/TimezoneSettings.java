package in.codifi.api.utilities;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import io.quarkus.runtime.StartupEvent;

@Singleton
public class TimezoneSettings {

	public void setTimezone(@Observes StartupEvent startupEvent) {
		System.setProperty("user.timezone", "Asia/Kolkata");
	}
}