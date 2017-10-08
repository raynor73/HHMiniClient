package ru.ilapin.hhminiclient;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;

public class MainActivity extends ViewModelProviderActivity {

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.listContainer, new VacanciesListFragment())
					.commit();
		}
	}
}
