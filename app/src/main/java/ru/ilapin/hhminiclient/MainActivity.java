package ru.ilapin.hhminiclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;

public class MainActivity extends ViewModelProviderActivity implements VacanciesListFragment.Listener {

	@Nullable
	@BindView(R.id.detailsLayout)
	ViewGroup mDetailsViewGroup;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		if (savedInstanceState == null) {
			if (mDetailsViewGroup == null) {
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.listContainer, new VacanciesListFragment())
						.commit();
			} else {
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.listContainer, new VacanciesListFragment())
						.replace(R.id.detailsLayout, VacancyDetailFragment.newInstance(-1))
						.commit();
			}
		}
	}

	@Override
	public void onVacancyIdSelected(final int id) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.detailsLayout, VacancyDetailFragment.newInstance(id))
				.commit();
	}
}
