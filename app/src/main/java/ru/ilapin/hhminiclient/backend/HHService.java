package ru.ilapin.hhminiclient.backend;

import retrofit2.Call;
import retrofit2.http.*;

interface HHService {
	@Headers("User-Agent: HHMiniClient/1.0 (igor.lapin73@gmail.com)")
	@GET("suggests/vacancy_search_keyword?text={text}")
	Call<String> vacancySearchKeywordSuggestions(@Path("text") String text);
}
