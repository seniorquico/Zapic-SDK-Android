package com.zapic.sdk.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

final class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    @NonNull
    private final ArrayList<WeakReference<Activity>> activities;

    ActivityLifecycleListener() {
        this.activities = new ArrayList<>();
    }

    @MainThread
    @Override
    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
    }

    @MainThread
    @Override
    public void onActivityStarted(final Activity activity) {
        if (activity == null) {
            return;
        }

        int activityIndex = -1;
        int index = 0;
        final Iterator<WeakReference<Activity>> iterator = this.activities.iterator();
        while (iterator.hasNext()) {
            final WeakReference<Activity> reference = iterator.next();
            final Activity referent = reference.get();
            if (referent == null) {
                iterator.remove();
            } else if (activity.equals(referent)) {
                activityIndex = index;
            }

            index++;
        }

        if (activityIndex == -1) {
            this.activities.add(0, new WeakReference<>(activity));
        }
    }

    @MainThread
    @Override
    public void onActivityResumed(final Activity activity) {
        if (activity == null) {
            return;
        }

        int activityIndex = -1;
        int index = 0;
        final Iterator<WeakReference<Activity>> iterator = this.activities.iterator();
        while (iterator.hasNext()) {
            final WeakReference<Activity> reference = iterator.next();
            final Activity referent = reference.get();
            if (referent == null) {
                iterator.remove();
            } else if (activity.equals(referent)) {
                activityIndex = index;
            }

            index++;
        }

        if (activityIndex == -1) {
            this.activities.add(0, new WeakReference<>(activity));
        } else {
            final WeakReference<Activity> reference = this.activities.remove(index);
            this.activities.add(0, reference);
        }
    }

    @MainThread
    @Override
    public void onActivityPaused(final Activity activity) {
        if (activity == null) {
            return;
        }

        int activityIndex = -1;
        int index = 0;
        final Iterator<WeakReference<Activity>> iterator = this.activities.iterator();
        while (iterator.hasNext()) {
            final WeakReference<Activity> reference = iterator.next();
            final Activity referent = reference.get();
            if (referent == null) {
                iterator.remove();
            } else if (activity.equals(referent)) {
                activityIndex = index;
            }

            index++;
        }

        if (activityIndex == -1) {
            this.activities.add(0, new WeakReference<>(activity));
        } else {
            final WeakReference<Activity> reference = this.activities.remove(index);
            this.activities.add(0, reference);
        }
    }

    @MainThread
    @Override
    public void onActivityStopped(final Activity activity) {
        if (activity == null) {
            return;
        }

        final Iterator<WeakReference<Activity>> iterator = this.activities.iterator();
        while (iterator.hasNext()) {
            final WeakReference<Activity> reference = iterator.next();
            final Activity referent = reference.get();
            if (referent == null || activity.equals(referent)) {
                iterator.remove();
            }
        }

        if (this.activities.isEmpty()) {
            // TODO: Save state.
        }
    }

    @MainThread
    @Override
    public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
    }

    @MainThread
    @Override
    public void onActivityDestroyed(final Activity activity) {
    }
}
