package ru.ilapin.hhminiclient;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;
import ru.ilapin.hhminiclient.backend.*;
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
				mBackend.getIdleObservable(),
				StateDescriptor::new
		).subscribe(stateDescriptor -> {
			if (!stateDescriptor.isIdle()) {
				mProgressBar.setVisibility(View.VISIBLE);

				mEmptyMessageTextView.setVisibility(View.GONE);
				mVacancyNameTextView.setVisibility(View.GONE);
				mSalaryTextView.setVisibility(View.GONE);
				mAreaLabelTextView.setVisibility(View.GONE);
				mAreaTextView.setVisibility(View.GONE);
				mPublishedAtTextView.setVisibility(View.GONE);
				mDetailsTextView.setVisibility(View.GONE);
			} else {
				mProgressBar.setVisibility(View.GONE);

				if (stateDescriptor.getVacancyResult().getData() != null) {
					mEmptyMessageTextView.setVisibility(View.GONE);
					mEmptyMessageTextView.setText(null);

					mVacancyNameTextView.setVisibility(View.VISIBLE);
					mSalaryTextView.setVisibility(View.VISIBLE);
					mAreaLabelTextView.setVisibility(View.VISIBLE);
					mAreaTextView.setVisibility(View.VISIBLE);
					mPublishedAtTextView.setVisibility(View.VISIBLE);
					mDetailsTextView.setVisibility(View.VISIBLE);
				} else {
					mEmptyMessageTextView.setVisibility(View.VISIBLE);

					final String message;
					if (stateDescriptor.isConnected()) {
						message = getString(R.string.vacancy_not_found);
					} else {
						message = getString(R.string.no_network);
					}
					mEmptyMessageTextView.setText(message);

					mVacancyNameTextView.setVisibility(View.GONE);
					mSalaryTextView.setVisibility(View.GONE);
					mAreaLabelTextView.setVisibility(View.GONE);
					mAreaTextView.setVisibility(View.GONE);
					mPublishedAtTextView.setVisibility(View.GONE);
					mDetailsTextView.setVisibility(View.GONE);
				}
			}
		});

		mVacancySubscription = mBackend.getVacancyObservable().subscribe(result -> {
			if (!result.hasError()) {
				final java.text.DateFormat mDateFormat = android.text.format.DateFormat.getDateFormat(getContext());
				final Vacancy vacancy = result.getData();
				if (vacancy != null) {
					mVacancyNameTextView.setText(vacancy.getName());
					mAreaTextView.setText(vacancy.getAreaName());
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						mDetailsTextView.setText(Html.fromHtml(vacancy.getDescription(), Html.FROM_HTML_MODE_COMPACT));
					} else {
						mDetailsTextView.setText(Html.fromHtml(vacancy.getDescription()));
					}

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
	}

	private void disposeSubscriptions() {
		mConnectionSubscription.dispose();
		mVacancySubscription.dispose();
	}

	private class StateDescriptor {

		private final boolean mIsConnected;
		private final Result<Vacancy> mVacancyResult;
		private final boolean mIsIdle;

		public StateDescriptor(final boolean isConnected, final Result<Vacancy> vacancyResult,
				final boolean isIdle) {
			mIsConnected = isConnected;
			mVacancyResult = vacancyResult;
			mIsIdle = isIdle;
		}

		public boolean isConnected() {
			return mIsConnected;
		}

		public Result<Vacancy> getVacancyResult() {
			return mVacancyResult;
		}

		public boolean isIdle() {
			return mIsIdle;
		}
	}
}
