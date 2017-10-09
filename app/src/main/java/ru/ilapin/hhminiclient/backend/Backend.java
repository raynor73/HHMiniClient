package ru.ilapin.hhminiclient.backend;

import android.content.SharedPreferences;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.ResponseBody;
import org.json.*;
import retrofit2.Retrofit;
import ru.ilapin.common.android.busymodel.BusyModel;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Backend extends BusyModel {

	private static final String TAG = "Backend";

	private static final String BASE_URL = "https://api.hh.ru/";

	private static final String SEARCH_KEYWORDS_KEY = "Backend.SEARCH_KEYWORDS";
	private static final String SEARCH_RESPONSE_KEY = "Backend.SEARCH_RESPONSE";
	private static final String VACANCY_ID_KEY = "Backend.VACANCY_ID";
	private static final String VACANCY_RESPONSE_KEY = "Backend.VACANCY_RESPONSE";

	private final HHService mHHService;
	private final BehaviorSubject<Result<List<BackendVacancy>>> mVacanciesSubject = BehaviorSubject.create();
	private final BehaviorSubject<Result<BackendVacancy>> mVacancyDetailsSubject = BehaviorSubject.create();

	private final SharedPreferences mSharedPreferences;

	@Inject
	public Backend(final SharedPreferences preferences) {
		mSharedPreferences = preferences;

		final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
		mHHService = retrofit.create(HHService.class);
		mVacanciesSubject.onNext(new Result<>(new ArrayList<>(), false));
		mVacancyDetailsSubject.onNext(new Result<>(null, false));
	}

	public Observable<Result<List<BackendVacancy>>> getVacanciesObservable() {
		return mVacanciesSubject;
	}

	public Observable<Result<BackendVacancy>> getVacancyObservable() {
		return mVacancyDetailsSubject;
	}

	public void searchVacancies(final String keywords) {
		if (!isIdle()) {
			Log.e(TAG, "Can't search vacancies while busy");
			return;
		}

		setIdle(false);

		Observable.<Result<ResponseBody>>create(subscriber -> subscriber.onNext(new Result<>(mHHService.vacancies(keywords).execute().body(), false)))
				.onErrorReturn(throwable -> new Result<>(null, true))
				.map(result -> {
					if (result.getData() == null) {
						if (mSharedPreferences.contains(SEARCH_KEYWORDS_KEY) && mSharedPreferences.getString(SEARCH_KEYWORDS_KEY, "").equals(keywords)) {
							return new Result<>(parseVacancies(mSharedPreferences.getString(SEARCH_RESPONSE_KEY, "")), false);
						} else {
							return new Result<List<BackendVacancy>>(new ArrayList<>(), true);
						}
					} else {
						final String responseString = result.getData().string();

						final SharedPreferences.Editor editor = mSharedPreferences.edit();
						editor.putString(SEARCH_KEYWORDS_KEY, keywords);
						editor.putString(SEARCH_RESPONSE_KEY, responseString);
						editor.apply();

						return new Result<>(parseVacancies(responseString), false);
					}
				})
				.onErrorReturn(throwable -> new Result<>(new ArrayList<>(), true))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					mVacanciesSubject.onNext(result);
					setIdle(true);
				});
	}

	public void getVacancy(final int id) {
		if (!isIdle()) {
			Log.e(TAG, "Can't load vacancy while busy");
			return;
		}

		setIdle(false);

		Observable.<Result<ResponseBody>>create(subscriber -> subscriber.onNext(new Result<>(mHHService.vacancy(id).execute().body(), false)))
				.onErrorReturn(throwable -> new Result<>(null, true))
				.map(result -> {
					if (result.getData() == null) {
						if (mSharedPreferences.contains(VACANCY_ID_KEY) && mSharedPreferences.getInt(VACANCY_ID_KEY, -1) == id) {
							return new Result<>(parseVacancyDetails(mSharedPreferences.getString(VACANCY_RESPONSE_KEY, "")), false);
						} else {
							return new Result<BackendVacancy>(null, true);
						}
					} else {
						final String responseString = result.getData().string();

						final SharedPreferences.Editor editor = mSharedPreferences.edit();
						editor.putInt(VACANCY_ID_KEY, id);
						editor.putString(VACANCY_RESPONSE_KEY, responseString);
						editor.apply();

						return new Result<>(parseVacancyDetails(responseString), false);
					}
				})
				.onErrorReturn(throwable -> new Result<>(null, true))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					mVacancyDetailsSubject.onNext(result);
					setIdle(true);
				});
	}

	private BackendVacancy parseVacancyDetails(final String responseString) throws JSONException {
		final JSONObject vacancyJsonObject = new JSONObject(responseString);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

		final BackendVacancy vacancy = new BackendVacancy();
		try {
			vacancy.setId(vacancyJsonObject.getInt("id"));
			vacancy.setName(vacancyJsonObject.getString("name"));
			vacancy.setPublishedAt(dateFormat.parse(vacancyJsonObject.getString("published_at")));
			vacancy.setEmployerName(vacancyJsonObject.getJSONObject("employer").getString("name"));
			vacancy.setAreaName(vacancyJsonObject.getJSONObject("area").getString("name"));
			vacancy.setDescription(vacancyJsonObject.getString("description"));

			final JSONObject salaryJsonObject = vacancyJsonObject.getJSONObject("salary");
			vacancy.setCurrency(salaryJsonObject.getString("currency"));
			vacancy.setSalaryFrom(salaryJsonObject.optInt("from"));
			vacancy.setSalaryTo(salaryJsonObject.optInt("to"));
		} catch (final JSONException | ParseException e) {
			Log.d(TAG, e.getMessage());
			return null;
		}

		return vacancy;
	}

	private List<BackendVacancy> parseVacancies(final String responseString) throws JSONException {
		final List<BackendVacancy> vacancies = new ArrayList<>();

		final JSONObject vacanciesJsonObject = new JSONObject(responseString);
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

		return vacancies;
	}
}
