package ru.ilapin.hhminiclient.networkconnection;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import ru.ilapin.common.android.viewmodelprovider.ViewModel;

public class NetworkConnectionModel implements ViewModel {

	private final Context mContext;
	private final NetworkConnectionReceiver mReceiver;
	private final BehaviorSubject<Boolean> mConnectionObservable = BehaviorSubject.create();

	public NetworkConnectionModel(final Context context) {
		mContext = context;
		mReceiver = new NetworkConnectionReceiver(this);
		mConnectionObservable.onNext(false);

		mContext.registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public Observable<Boolean> getConnectionObservable() {
		return mConnectionObservable;
	}

	Subject<Boolean> getConnectionSubject() {
		return mConnectionObservable;
	}

	@Override
	public void onCleared() {
		mContext.unregisterReceiver(mReceiver);
	}
}