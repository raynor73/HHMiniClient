package ru.ilapin.hhminiclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;
import ru.ilapin.hhminiclient.backend.Backend;
import ru.ilapin.hhminiclient.backend.BackendVacancy;
import ru.ilapin.hhminiclient.networkconnection.NetworkConnectionModel;

import javax.inject.Inject;
import java.util.Locale;

public class VacancyDetailFragment extends Fragment {

	private static final String VACANCY_ID_KEY = "VACANCY_ID";

	@Inject
	Backend mBackend;

	private ViewModelProviderActivity mViewModelProvider;

	private NetworkConnectionModel mNetworkConnectionModel;

	private Disposable mConnectionSubscription;
	private Disposable mVacancySubscription;
	private Disposable mIdleSubscription;

	@BindView(R.id.vacancyName)
	TextView mVacancyNameTextView;
	@BindView(R.id.salary)
	TextView mSalaryTextView;
	@BindView(R.id.areaName)
	TextView mAreaTextView;
	@BindView(R.id.areaLabel)
	TextView mAreaLabelTextView;
	@BindView(R.id.publishedAt)
	TextView mPublishedAtTextView;
	@BindView(R.id.details)
	TextView mDetailsTextView;
	@BindView(R.id.progressBar)
	ProgressBar mProgressBar;
	@BindView(R.id.emptyMessage)
	TextView mEmptyMessageTextView;

	public static VacancyDetailFragment newInstance(final int vacancyId) {
		final Bundle arguments = new Bundle();
		final VacancyDetailFragment fragment = new VacancyDetailFragment();

		arguments.putInt(VACANCY_ID_KEY, vacancyId);
		fragment.setArguments(arguments);

		return fragment;
	}

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		mViewModelProvider = (ViewModelProviderActivity) context;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_vacancy, container, false);

		ButterKnife.bind(this, rootView);
		App.getApplicationComponent().inject(this);

		mNetworkConnectionModel = mViewModelProvider.findVideModel(NetworkConnectionModel.class);
		if (mNetworkConnectionModel == null) {
			mNetworkConnectionModel = new NetworkConnectionModel(getContext().getApplicationContext());
			mViewModelProvider.putViewModel(mNetworkConnectionModel);
		}

		mBackend.getVacancy(getArguments().getInt(VACANCY_ID_KEY));

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		makeSubscriptions();
	}

	@Override
	public void onPause() {
		super.onPause();

		disposeSubscriptions();
	}

	private void makeSubscriptions() {
		mConnectionSubscription = Observable.combineLatest(
				mNetworkConnectionModel.getConnectionObservable(),
				mBackend.getVacancyObservable(),
				(isConnected, vacancy) -> {
					if (vacancy.getData() != null) {
						return "";
					} else if (isConnected) {
						return getString(R.string.choose_vacancy);
					} else {
						return getString(R.string.no_network);
					}
				}
		).subscribe(message -> {
			mEmptyMessageTextView.setText(message);
			if (!TextUtils.isEmpty(message)) {
				mEmptyMessageTextView.setVisibility(View.VISIBLE);

				mVacancyNameTextView.setVisibility(View.GONE);
				mSalaryTextView.setVisibility(View.GONE);
				mAreaLabelTextView.setVisibility(View.GONE);
				mAreaTextView.setVisibility(View.GONE);
				mPublishedAtTextView.setVisibility(View.GONE);
				mDetailsTextView.setVisibility(View.GONE);
			} else {
				mEmptyMessageTextView.setVisibility(View.GONE);

				mVacancyNameTextView.setVisibility(View.VISIBLE);
				mSalaryTextView.setVisibility(View.VISIBLE);
				mAreaLabelTextView.setVisibility(View.VISIBLE);
				mAreaTextView.setVisibility(View.VISIBLE);
				mPublishedAtTextView.setVisibility(View.VISIBLE);
				mDetailsTextView.setVisibility(View.VISIBLE);
			}
		});

		mVacancySubscription = mBackend.getVacancyObservable().subscribe(result -> {
			if (!result.hasError()) {
				final java.text.DateFormat mDateFormat = android.text.format.DateFormat.getDateFormat(getContext());
				final BackendVacancy vacancy = result.getData();
				if (vacancy != null) {
					mVacancyNameTextView.setText(vacancy.getName());
					mAreaTextView.setText(vacancy.getAreaName());
					mDetailsTextView.setText(vacancy.getDescription());

					final String salary;
					if (vacancy.getSalaryFrom() > 0 && vacancy.getSalaryTo() > 0) {
						salary = String.format(
								Locale.US,
								"%d - %d %s",
								vacancy.getSalaryFrom(),
								vacancy.getSalaryTo(),
								vacancy.getCurrency()
						);
					} else if (vacancy.getSalaryTo() > 0) {
						salary = String.format(
								Locale.US,
								"%d %s",
								vacancy.getSalaryTo(),
								vacancy.getCurrency()
						);
					} else if (vacancy.getSalaryFrom() > 0) {
						salary = String.format(
								Locale.US,
								"%d %s",
								vacancy.getSalaryFrom(),
								vacancy.getCurrency()
						);
					} else {
						salary = getString(R.string.n_a);
					}
					mSalaryTextView.setText(salary);
					mPublishedAtTextView.setText(mDateFormat.format(vacancy.getPublishedAt()));
				}
			}
		});

		mIdleSubscription = mBackend.getIdleObservable().subscribe(isIdle -> {
			if (isIdle) {
				mProgressBar.setVisibility(View.GONE);
			} else {
				mProgressBar.setVisibility(View.VISIBLE);
			}
		});
	}

	private void disposeSubscriptions() {
		mConnectionSubscription.dispose();
		mVacancySubscription.dispose();
		mIdleSubscription.dispose();
	}
}
