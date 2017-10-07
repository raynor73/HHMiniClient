package ru.ilapin.hhminiclient.backend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class Backend {

	private static final String TAG = "Backend";

	private static final String BASE_URL = "https://api.hh.ru/";

	private final HHService mHHService;

	public Backend() {
		final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
		mHHService = retrofit.create(HHService.class);
	}

	public List<BackendVacancy> searchVacancies(final String keywords) throws IOException, JSONException {
		final ResponseBody vacanciesResponseBody = mHHService.vacancies(keywords).execute().body();
		final List<BackendVacancy> vacancies = new ArrayList<>();

		if (vacanciesResponseBody != null) {
			final JSONObject vacanciesJsonObject = new JSONObject(vacanciesResponseBody.string());
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
			final JSONArray vacanciesJsonArray = vacanciesJsonObject.getJSONArray("items");
			for (int i = 0; i < vacanciesJsonArray.length(); i++) {
				final JSONObject vacancyJsonObject = vacanciesJsonArray.getJSONObject(i);

				final BackendVacancy vacancy = new BackendVacancy();
				try {
					vacancy.setId(vacancyJsonObject.getInt("id"));
					vacancy.setName(vacancyJsonObject.getString("name"));
					vacancy.setPublishedAt(dateFormat.parse(vacancyJsonObject.getString("published_at")));
					vacancy.setEmployerName(vacancyJsonObject.getJSONObject("employer").getString("name"));
					vacancy.setAreaName(vacancyJsonObject.getJSONObject("area").getString("name"));

					final JSONObject salaryJsonObject = vacancyJsonObject.getJSONObject("salary");
					vacancy.setCurrency(salaryJsonObject.getString("currency"));
					vacancy.setSalaryFrom(salaryJsonObject.optInt("from"));
					vacancy.setSalaryTo(salaryJsonObject.optInt("to"));
				} catch (final JSONException | ParseException e) {
					Log.d(TAG, e.getMessage());
					continue;
				}

				vacancies.add(vacancy);
			}
		}

		return vacancies;
	}
}
