package ru.ilapin.hhminiclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;

public class MainActivity extends ViewModelProviderActivity implements VacanciesListFragment.Listener {

	private static final String SELECTED_VACANCY_ID_KEY = "SELECTED_VACANCY_ID";

	@Nullable
	@BindView(R.id.detailsLayout)
	ViewGroup mDetailsViewGroup;

	@Nullable
	@BindView(R.id.chooseVacancy)
	TextView mChooseVacancyTextView;

	private int mSelectedVacancyId = -1;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.container, new VacanciesListFragment())
					.commit();
		} else if (mDetailsViewGroup != null) {
			final int vacancyId = savedInstanceState.getInt(SELECTED_VACANCY_ID_KEY, -1);

			final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container, new VacanciesListFragment());
			if (vacancyId >= 0) {
				transaction.replace(R.id.detailsLayout, VacancyDetailFragment.newInstance(vacancyId));
			}
			transaction.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mChooseVacancyTextView != null) {
			if (getSupportFragmentManager().findFragmentById(R.id.detailsLayout) == null) {
				mChooseVacancyTextView.setVisibility(View.VISIBLE);
			} else {
				mChooseVacancyTextView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onVacancyIdSelected(final int id) {
		mSelectedVacancyId = id;

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (mDetailsViewGroup == null) {
			transaction
					.addToBackStack(null)
					.replace(R.id.container, VacancyDetailFragment.newInstance(id));
		} else {
			if (mChooseVacancyTextView != null) {
				mChooseVacancyTextView.setVisibility(View.GONE);
			}
			transaction.replace(R.id.detailsLayout, VacancyDetailFragment.newInstance(id));
		}
		transaction.commit();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SELECTED_VACANCY_ID_KEY, mSelectedVacancyId);
	}
}
