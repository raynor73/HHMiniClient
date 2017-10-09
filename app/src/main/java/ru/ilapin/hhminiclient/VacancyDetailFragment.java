package ru.ilapin.hhminiclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;
import ru.ilapin.hhminiclient.backend.Backend;
import ru.ilapin.hhminiclient.networkconnection.NetworkConnectionModel;

public class VacancyDetailFragment extends Fragment {

	@Inject
	Backend mBackend;

	private ViewModelProviderActivity mViewModelProvider;

	private NetworkConnectionModel mNetworkConnectionModel;

	private Disposable mConnectionSubscription;
	private Disposable mVacancySubscription;
	private Disposable mIdleSubscription;

	@BindView(R.id.vacancyName)
	TextView vacancyNameTextView;
	@BindView(R.id.salary)
	TextView salaryTextView;
	@BindView(R.id.areaName)
	TextView areaTextView;
	@BindView(R.id.publishedAtTextView)
	TextView publishedAtTextView;
	@BindView(R.id.details)
	TextView mDetailsTextView;

	@Override
	public void onAttach(final Context context) {
		super.onAttach(context);

		mViewModelProvider = (ViewModelProviderActivity) context;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_vacancies_list, container, false);

		ButterKnife.bind(this, rootView);
		App.getApplicationComponent().inject(this);

		mNetworkConnectionModel = mViewModelProvider.findVideModel(NetworkConnectionModel.class);
		if (mNetworkConnectionModel == null) {
			mNetworkConnectionModel = new NetworkConnectionModel(getContext().getApplicationContext());
			mViewModelProvider.putViewModel(mNetworkConnectionModel);
		}

		return rootView;
	}
}
