package ru.ilapin.hhminiclient.database;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.Date;

import io.reactivex.Observable;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {

	private DatabaseHelper mDatabaseHelper;

	@Before
	public void init() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void createAndDeleteVacancies() throws Exception {
		mDatabaseHelper.deleteAllVacancies();
		assertTrue(mDatabaseHelper.queryVacancies().size() == 0);

		final DbVacancy foo = new DbVacancy();
		foo.setId(1);
		foo.setName("Вакансия 1");
		foo.setPublishedAt(new Date());

		final DbVacancy bar = new DbVacancy();
		bar.setId(2);
		bar.setName("Вакансия 2");
		bar.setPublishedAt(new Date());

		mDatabaseHelper.createOrUpdateVacancy(foo);
		mDatabaseHelper.createOrUpdateVacancy(bar);

		assertTrue(mDatabaseHelper.queryVacancies().size() == 2);
	}

	@Test
	public void queryVacancies() throws Exception {
		mDatabaseHelper.deleteAllVacancies();
		assertTrue(mDatabaseHelper.queryVacancies().size() == 0);

		final DbVacancy foo = new DbVacancy();
		foo.setId(1);
		foo.setName("Вакансия 1");
		foo.setPublishedAt(new Date());

		final DbVacancy bar = new DbVacancy();
		bar.setId(2);
		bar.setName("Вакансия 2");
		bar.setPublishedAt(new Date());

		mDatabaseHelper.createOrUpdateVacancy(foo);
		mDatabaseHelper.createOrUpdateVacancy(bar);

		assertTrue(Observable.fromIterable(mDatabaseHelper.queryVacancies()).map(vacancy -> {
			if (vacancy.getId() == 1 && vacancy.getName().equals("Вакансия 1")) {
				return true;
			} else if (vacancy.getId() == 2 && vacancy.getName().equals("Вакансия 2")) {
				return true;
			} else {
				return false;
			}
		}).all(isVacancyValid -> isVacancyValid).blockingGet());
	}

	@Test
	public void updateVacancy() throws Exception {
		mDatabaseHelper.deleteAllVacancies();
		assertTrue(mDatabaseHelper.queryVacancies().size() == 0);

		final DbVacancy foo = new DbVacancy();
		foo.setId(1);
		foo.setName("Вакансия 1");
		foo.setPublishedAt(new Date());
		mDatabaseHelper.createOrUpdateVacancy(foo);
		assertTrue(mDatabaseHelper.queryVacancies().size() == 1);

		final DbVacancy testVacancy = mDatabaseHelper.queryVacancies().get(0);
		testVacancy.setName("Вакансия 2");
		mDatabaseHelper.createOrUpdateVacancy(testVacancy);
		assertTrue(mDatabaseHelper.queryVacancies().size() == 1);

		final DbVacancy testVacancy2 = mDatabaseHelper.queryVacancies().get(0);
		assertTrue(testVacancy2.getName().equals("Вакансия 2"));
	}
}
