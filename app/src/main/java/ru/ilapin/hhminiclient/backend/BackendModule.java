package ru.ilapin.hhminiclient.backend;

import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class BackendModule {

	@Provides
	@Singleton
	public Backend provideBackend(final SharedPreferences preferences) {
		return new Backend(preferences);
	}
}
