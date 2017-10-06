package ru.ilapin.hhminiclient.backend;

import retrofit2.Retrofit;

public class Backend {

	private static final String BASE_URL = "https://api.hh.ru/";

	private final HHService mHHService;

	public Backend() {
		final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
		mHHService = retrofit.create(HHService.class);
	}
}
