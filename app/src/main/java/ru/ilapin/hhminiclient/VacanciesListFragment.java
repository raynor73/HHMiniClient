package ru.ilapin.hhminiclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import butterknife.*;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;
import ru.ilapin.hhminiclient.backend.Backend;
import ru.ilapin.hhminiclient.backend.Vacancy;
import ru.ilapin.hhminiclient.networkconnection.NetworkConnectionModel;

import javax.inject.Inject;
import java.util.*;

public class VacanciesListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	private static final String SEARCH_KEYWORDS_KEY = "VacanciesListFragment.SEARCH_KEYWORDS";

	@Inject
	Backend mBackend;
	@Inject
	SharedPreferences mSharedPreferences;

	private ViewModelProviderActivity mViewModelProvider;
	private Listener mListener;

	private NetworkConnectionModel mNetworkConnectionModel;

	private Disposable mConnectionSubscription;
	private Disposable mVacanciesSubscription;
	private Disposable mIdleSubscription;

	private VacanciesListAdapter mVacanciesListAdapter;

	@BindView(R.id.vacanciesList)
	RecyclerView mVacanciesListRecyclerView;
	@BindView(R.id.vacancyKeywords)
	EditText mKeywordsEditText;
	@BindView(R.id.clearButton)
	Button mClearButton;
	@BindView(R.id.searchButton)
	Button mSearchButton;
	@BindView(R.id.swipeToRefresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.emptyListMessage)
	TextView mEmptyListMessageTextView;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		mViewModelProvider = (ViewModelProviderActivity) context;
		mListener = (Listener) context;
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_vacancies_list, container, false);

		ButterKnife.bind(this, rootView);
		App.getApplicationComponent().inject(this);

		mNetworkConnectionModel = mViewModelProvider.findVideModel(NetworkConnectionModel.class);
		if (mNetworkConnectionModel == null) {
			mNetworkConnectionModel = new NetworkConnectionModel(getContext().getApplicationContext());
			mViewModelProvider.putViewModel(mNetworkConnectionModel);
		}

		mVacanciesListAdapter = new VacanciesListAdapter();
		mVacanciesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mVacanciesListRecyclerView.setAdapter(mVacanciesListAdapter);

		mSwipeRefreshLayout.setOnRefreshListener(this);

		if (savedInstanceState == null) {
			final String keywords = mSharedPreferences.getString(SEARCH_KEYWORDS_KEY, "");
			mKeywordsEditText.setText(keywords);
			mBackend.searchVacancies(keywords);
		}

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

	@OnClick(R.id.searchButton)
	void onSearchButtonClicked() {
		doSearch();
	}

	@OnClick(R.id.clearButton)
	void onClearButtonClicked() {
		mKeywordsEditText.setText(null);
	}

	@Override
	public void onRefresh() {
		doSearch();
	}

	private void doSearch() {
		final Editable editableText = mKeywordsEditText.getText();
		final String keywords;
		if (editableText == null || TextUtils.isEmpty(editableText.toString())) {
			keywords = "";
		} else {
			keywords = editableText.toString();
		}
		mBackend.searchVacancies(keywords);
		mSharedPreferences.edit().putString(SEARCH_KEYWORDS_KEY, keywords).apply();
	}

	private void makeSubscriptions() {
		mConnectionSubscription = Observable.combineLatest(
				mNetworkConnectionModel.getConnectionObservable(),
				mBackend.getVacanciesObservable().map(vacancies -> vacancies.getData().size()),
				(isConnected, numberOfVacancies) -> {
					if (numberOfVacancies > 0) {
						return "";
					} else if (isConnected) {
						return getString(R.string.no_vacancies);
					} else {
						return getString(R.string.no_network);
					}
				}
		).subscribe(message -> {
			mEmptyListMessageTextView.setText(message);
			if (!TextUtils.isEmpty(message)) {
				mEmptyListMessageTextView.setVisibility(View.VISIBLE);
			} else {
				mEmptyListMessageTextView.setVisibility(View.GONE);
			}
		});

		mVacanciesSubscription = mBackend.getVacanciesObservable().subscribe(result -> mVacanciesListAdapter.setVacanciesList(result.getData()));

		mIdleSubscription = mBackend.getIdleObservable().subscribe(isIdle -> {
			if (isIdle) {
				mVacanciesListAdapter.setClickable(true);
				mKeywordsEditText.setEnabled(true);
				mClearButton.setEnabled(true);
				mSearchButton.setEnabled(true);
				mSwipeRefreshLayout.setRefreshing(false);
			} else {
				mVacanciesListAdapter.setClickable(false);
				mKeywordsEditText.setEnabled(false);
				mClearButton.setEnabled(false);
				mSearchButton.setEnabled(false);
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});
	}

	private void disposeSubscriptions() {
		mConnectionSubscription.dispose();
		mVacanciesSubscription.dispose();
		mIdleSubscription.dispose();
	}

	private void onVacancyClicked(final Vacancy vacancy) {
		mListener.onVacancyIdSelected(vacancy.getId());
	}

	public class VacanciesListAdapter extends RecyclerView.Adapter<VacanciesListAdapter.ViewHolder> {

		private final List<Vacancy> mVacanciesList = new ArrayList<>();
		private final LayoutInflater mInflater = LayoutInflater.from(getContext());
		private final java.text.DateFormat mDateFormat =
				android.text.format.DateFormat.getDateFormat(getContext());

		private boolean mIsClickable;

		@Override
		public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			return new ViewHolder(mInflater.inflate(R.layout.view_vacancy_list_item, parent, false));
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, final int position) {
			final Vacancy vacancy = mVacanciesList.get(position);

			holder.vacancyNameTextView.setText(vacancy.getName());
			holder.areaTextView.setText(vacancy.getAreaName());
			holder.employerTextView.setText(vacancy.getEmployerName());
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
			holder.salaryTextView.setText(salary);
			holder.publishedAtTextView.setText(mDateFormat.format(vacancy.getPublishedAt()));
		}

		@Override
		public int getItemCount() {
			return mVacanciesList.size();
		}

		public void setVacanciesList(final List<Vacancy> vacanciesList) {
			mVacanciesList.clear();
			mVacanciesList.addAll(vacanciesList);
			notifyDataSetChanged();
		}

		public void setClickable(final boolean clickable) {
			mIsClickable = clickable;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			@BindView(R.id.vacancyNameTextView)
			TextView vacancyNameTextView;
			@BindView(R.id.salaryTextView)
			TextView salaryTextView;
			@BindView(R.id.employerTextView)
			TextView employerTextView;
			@BindView(R.id.areaTextView)
			TextView areaTextView;
			@BindView(R.id.publishedAtTextView)
			TextView publishedAtTextView;

			public ViewHolder(final View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);

				itemView.setOnClickListener(view -> {
					if (mIsClickable) {
						onVacancyClicked(mVacanciesList.get(getAdapterPosition()));
					}
				});
			}
		}
	}

	public interface Listener {

		void onVacancyIdSelected(int id);
	}
}
