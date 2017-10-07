package ru.ilapin.hhminiclient;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SystemModule {

	private final Context mContext;

	public SystemModule(final Context context) {
		mContext = context;
	}

	@Provides
	@Singleton
	public Context provideContext() {
		return mContext;
	}
}
