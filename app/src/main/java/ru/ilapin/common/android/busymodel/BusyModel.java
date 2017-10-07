package ru.ilapin.common.android.busymodel;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class BusyModel {

	private boolean mIsIdle;

	private final BehaviorSubject<Boolean> mIdleObservable = BehaviorSubject.create();

	public BusyModel() {
		setIdle(true);
	}

	public Observable<Boolean> getIdleObservable() {
		return mIdleObservable;
	}

	protected boolean isIdle() {
		return mIsIdle;
	}

	protected void setIdle(final boolean isIdle) {
		if (mIsIdle == isIdle) {
			throw new RuntimeException("Idle status is already: " + mIsIdle);
		}

		mIsIdle = isIdle;
		mIdleObservable.onNext(mIsIdle);
	}
}
