package ru.ilapin.hhminiclient.networkconnection;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectionReceiver extends BroadcastReceiver {

	private final NetworkConnectionModel mNetworkConnectionModel;

	public NetworkConnectionReceiver(final NetworkConnectionModel networkConnectionModel) {
		mNetworkConnectionModel = networkConnectionModel;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final ConnectivityManager connectionManager =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();

		mNetworkConnectionModel.getConnectionSubject().onNext(networkInfo != null && networkInfo.isConnected());
	}
}
