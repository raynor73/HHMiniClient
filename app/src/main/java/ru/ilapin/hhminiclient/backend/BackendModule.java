package ru.ilapin.hhminiclient.backend;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BackendModule {

	@Provides
	@Singleton
	public Backend provideBackend() {
		return new Backend();
	}
}
