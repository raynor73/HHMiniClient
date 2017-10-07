package ru.ilapin.hhminiclient.vacanciesrepository;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import ru.ilapin.common.android.busymodel.BusyModel;
import ru.ilapin.hhminiclient.backend.Backend;
import ru.ilapin.hhminiclient.backend.BackendVacancy;
import ru.ilapin.hhminiclient.database.DatabaseHelper;

public class VacanciesRepository extends BusyModel {

	private static final String TAG = "VacanciesRepository";

	private final DatabaseHelper mDatabaseHelper;
	private final Backend mBackend;

	private final BehaviorSubject<List<Vacancy>> mVacanciesSubject = BehaviorSubject.create();

	@Inject
	public VacanciesRepository(final DatabaseHelper databaseHelper, final Backend backend) {
		mDatabaseHelper = databaseHelper;
		mBackend = backend;

		mVacanciesSubject.onNext(new ArrayList<>());
	}

	public void searchVacancies(final String keywords) {
		if (!isIdle()) {
			Log.e(TAG, "Can't search vacancies while busy");
			return;
		}

		setIdle(false);

		Observable.<List<BackendVacancy>>create(subscriber -> {
			subscriber.onNext(mBackend.searchVacancies(keywords));
		})
				// TODO Continue here... How to update/delete existing Vacancies?
				.takeWhile(backendVacancies -> backendVacancies.size() > 0)
				.flatMap(Observable::fromIterable)
				.map(backendVacancy -> {
					final Vacancy vacancy = new Vacancy();

					vacancy.setId(backendVacancy.getId());
					vacancy.setName(backendVacancy.getName());
					vacancy.setPublishedAt(backendVacancy.getPublishedAt());
					vacancy.setCurrency(backendVacancy.getCurrency());
					vacancy.setSalaryFrom(backendVacancy.getSalaryFrom());
					vacancy.setSalaryTo(backendVacancy.getSalaryTo());
					vacancy.setAreaName(backendVacancy.getAreaName());
					vacancy.setEmployerName(backendVacancy.getEmployerName());

					return vacancy;
				})
				.toList()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(mVacanciesSubject::onNext);
	}
}
