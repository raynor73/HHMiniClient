package ru.ilapin.hhminiclient.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private final static String DATABASE_NAME = "hhminiclient.db";
	private final static int DATABASE_VERSION = 1;

	public DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase sqLiteDatabase, final ConnectionSource connectionSource) {
		try {
			createTables(connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final ConnectionSource connectionSource, final int i, final int i1) {
		try {
			dropTables(connectionSource);
			createTables(connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void createOrUpdateVacancy(final Vacancy vacancy) throws SQLException {
		Dao<Vacancy, Integer> vacancyDao = getDao(Vacancy.class);
		vacancyDao.createOrUpdate(vacancy);
	}

	public void deleteAllVacancies() throws SQLException {
		Dao<Vacancy, Integer> vacancyDao = getDao(Vacancy.class);
		vacancyDao.deleteBuilder().delete();
	}

	public List<Vacancy> queryVacancies() throws SQLException {
		Dao<Vacancy, Integer> vacancyDao = getDao(Vacancy.class);
		QueryBuilder<Vacancy, Integer> queryBuilder = vacancyDao.queryBuilder();
		queryBuilder.orderBy(Vacancy.COLUMN_NAME_PUBLISHED_AT, false);
		return queryBuilder.query();
	}

	private void createTables(final ConnectionSource connectionSource) throws SQLException {
		TableUtils.createTable(connectionSource, Vacancy.class);
	}

	private void dropTables(final ConnectionSource connectionSource) throws SQLException {
		TableUtils.dropTable(connectionSource, Vacancy.class, true);
	}
}
