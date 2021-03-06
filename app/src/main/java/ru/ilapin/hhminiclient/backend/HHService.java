package ru.ilapin.hhminiclient.backend;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface HHService {

	@Headers("User-Agent: HHMiniClient/1.0 (igor.lapin73@gmail.com)")
	@GET("vacancies")
	Call<ResponseBody> vacancies(@Query("text") String text);

	@Headers("User-Agent: HHMiniClient/1.0 (igor.lapin73@gmail.com)")
	@GET("vacancies/{vacancyId}")
	Call<ResponseBody> vacancy(@Path("vacancyId") int vacancyId);
}
