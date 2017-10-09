package ru.ilapin.hhminiclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;

public class VacancyDetailsActivity extends ViewModelProviderActivity {

	private static final String VACANCY_ID_KEY = "ru.ilapin.hhminiclient.VacancyDetailsActivity.VACANCY_ID";

	public static void startWithId(final Context context, final int id) {
		final Intent intent = new Intent(context, VacancyDetailsActivity.class);
		intent.putExtra(VACANCY_ID_KEY, id);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vacancy_details);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.container,
							VacancyDetailFragment.newInstance(getIntent().getIntExtra(VACANCY_ID_KEY, -1))
					).commit();
		}
	}
}
