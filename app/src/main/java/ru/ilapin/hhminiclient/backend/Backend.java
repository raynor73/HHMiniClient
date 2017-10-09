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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import ru.ilapin.common.android.busymodel.BusyModel;

public class Backend extends BusyModel {

	private static final String TAG = "Backend";

	private static final String BASE_URL = "https://api.hh.ru/";

	private final HHService mHHService;
	private final BehaviorSubject<Result<List<BackendVacancy>>> mVacanciesSubject = BehaviorSubject.create();
	private final BehaviorSubject<Result<BackendVacancy>> mVacancyDetailsSubject = BehaviorSubject.create();

	public Backend() {
		final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
		mHHService = retrofit.create(HHService.class);
		mVacanciesSubject.onNext(new Result<>(new ArrayList<>(), false));
		mVacancyDetailsSubject.onNext(new Result<>(null, false));
	}

	public Observable<Result<List<BackendVacancy>>> getVacanciesObservable() {
		return mVacanciesSubject;
	}

	public void searchVacancies(final String keywords) {
		if (!isIdle()) {
			Log.e(TAG, "Can't search vacancies while busy");
			return;
		}

		setIdle(false);

		Observable.<Result<List<BackendVacancy>>>create(subscriber -> subscriber.onNext(new Result<>(makeSearchVacanciesRequest(keywords), false)))
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

		Observable.<Result<BackendVacancy>>create(subscriber -> subscriber.onNext(new Result<>(makeVacancyRequest(id), false)))
				.onErrorReturn(throwable -> new Result<>(null, true))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					mVacancyDetailsSubject.onNext(result);
					setIdle(true);
				});
	}

	private BackendVacancy makeVacancyRequest(final int id) throws IOException, JSONException {
		final ResponseBody vacancyResponseBody = mHHService.vacancy(id).execute().body();

		if (vacancyResponseBody != null) {
			final JSONObject vacancyJsonObject = new JSONObject(vacancyResponseBody.string());
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

		return null;
	}

	private List<BackendVacancy> makeSearchVacanciesRequest(final String keywords) throws IOException, JSONException {
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
