package ru.ilapin.hhminiclient.vacanciesrepository;

import javax.inject.Inject;

import ru.ilapin.hhminiclient.database.DatabaseHelper;

public class VacanciesRepository {

	private DatabaseHelper mDatabaseHelper;

	@Inject
	public VacanciesRepository(final DatabaseHelper databaseHelper) {
		mDatabaseHelper = databaseHelper;
	}
}
