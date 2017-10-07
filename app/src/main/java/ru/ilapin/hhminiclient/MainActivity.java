package ru.ilapin.hhminiclient;

import android.os.Bundle;

import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import ru.ilapin.common.android.viewmodelprovider.ViewModelProviderActivity;
import ru.ilapin.hhminiclient.networkconnection.NetworkConnectionModel;

public class MainActivity extends ViewModelProviderActivity {

	private NetworkConnectionModel mNetworkConnectionModel;

	private Disposable mConnectionSubscription;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		mNetworkConnectionModel = findVideModel(NetworkConnectionModel.class);
		if (mNetworkConnectionModel == null) {
			mNetworkConnectionModel = new NetworkConnectionModel(getApplicationContext());
			putViewModel(mNetworkConnectionModel);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		makeSubscriptions();
	}

	@Override
	protected void onPause() {
		super.onPause();

		disposeSubscriptions();
	}

	private void makeSubscriptions() {
		mConnectionSubscription = mNetworkConnectionModel.getConnectionObservable().subscribe(isConnected -> {

		});
	}

	private void disposeSubscriptions() {
		mConnectionSubscription.dispose();
	}
}
