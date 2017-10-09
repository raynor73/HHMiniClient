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

	@Nullable
	@BindView(R.id.detailsLayout)
	ViewGroup mDetailsViewGroup;

	@Nullable
	@BindView(R.id.chooseVacancy)
	TextView mChooseVacancyTextView;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		if (savedInstanceState == null) {
			final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.listContainer, new VacanciesListFragment());
			if (mDetailsViewGroup != null) {
				transaction.replace(R.id.detailsLayout, VacancyDetailFragment.newInstance(-1));
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
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mDetailsViewGroup == null) {
			transaction
					.addToBackStack(null)
					.replace(R.id.listContainer, VacancyDetailFragment.newInstance(id));
		} else {
			if (mChooseVacancyTextView != null) {
				mChooseVacancyTextView.setVisibility(View.GONE);
			}
			transaction.replace(R.id.detailsLayout, VacancyDetailFragment.newInstance(id));
		}
		transaction.commit();
	}
}
