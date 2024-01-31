/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package sdk.chat.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.activities.BaseActivity;
import sdk.chat.ui.module.UIModule;
import sdk.chat.ui.utils.AlertUtils;
import sdk.chat.ui.utils.ToastHelper;
import sdk.guru.common.DisposableMap;

/**
 * Created by itzik on 6/17/2014.
 */
public abstract class BaseFragment extends Fragment implements Consumer<Throwable>, CompletableObserver {

    protected AlertUtils alert;

    protected View rootView;
    protected boolean tabIsVisible;
    protected DisposableMap dm = new DisposableMap();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        LayoutInflater localInflater = inflater;
        if(UIModule.config().theme != 0 && getActivity() != null) {
            final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), UIModule.config().theme);

            // clone the inflater using the ContextThemeWrapper
            localInflater = inflater.cloneInContext(contextThemeWrapper);
        }
        rootView = inflate(localInflater, container);
//        rootView = localInflater.inflate(getLayout(), container, false);

        setHasOptionsMenu(true);

        alert = new AlertUtils(new AlertUtils.Provider() {
            @Override
            public Context getContext() {
                return BaseFragment.this.getContext();
            }
            @Override
            public View getRootView() {
                return getView();
            }
        });

        return rootView;
    }

    public View inflate(@NonNull LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setupTouchUIToDismissKeyboard(View view, final Integer... exceptIDs) {
        BaseActivity.setupTouchUIToDismissKeyboard(view, (v, event) -> {
            hideKeyboard();
            return false;
        }, exceptIDs);
    }

    protected abstract @LayoutRes int getLayout();

    public void hideKeyboard() {
        BaseActivity.hideKeyboard(getActivity());
    }

    public void hideKeyboard(View focus) {
        BaseActivity.hideKeyboard(getActivity(), focus);
    }

    protected abstract void initViews();

    public void setTabVisibility (boolean isVisible) {
        tabIsVisible = isVisible;
    }

    abstract public void clearData ();
    public void safeReloadData () {
        if(getView() != null && ChatSDK.auth().isAuthenticated()) {
            reloadData();
        }
    }
    public abstract void reloadData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dm.dispose();
    }

    @Override
    public void onStart() {
        super.onStart();
        ChatSDKUI.shared().getFragmentLifecycleManager().add(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ChatSDKUI.shared().getFragmentLifecycleManager().remove(this);
    }

    public void onSubscribe(@NonNull Disposable d) {
        dm.add(d);
    }

    /**
     * Called once the deferred computation completes normally.
     */
    public void onComplete() {

    }

    /**
     * Called once if the deferred computation 'throws' an exception.
     * @param e the exception, not null.
     */
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        System.out.println("SHOW TOAST" + e.getLocalizedMessage());

        ToastHelper.show(getContext(), e.getLocalizedMessage());
    }

    public void accept(Throwable t) {
        onError(t);
    }

    /** Show a SuperToast with the given text. */
    protected void showToast(@StringRes int textResourceId){
        alert.showToast(textResourceId);
    }

    protected void showToast(String text){
        alert.showToast(text);
    }

    protected void showSnackbar(int textResourceId, int duration){
        alert.showSnackbar(textResourceId, duration);
    }

    protected void showSnackbar(int textResourceId){
        alert.showSnackbar(textResourceId);
    }

    protected void showSnackbar (String text) {
        alert.showSnackbar(text);
    }

    protected void showSnackbar (String text, int duration) {
        alert.showSnackbar(text, duration);
    }

    protected Consumer<? super Throwable> toastOnErrorConsumer () {
        return alert.toastOnErrorConsumer();
    }

    protected Consumer<? super Throwable> snackbarOnErrorConsumer () {
        return alert.snackbarOnErrorConsumer();
    }
    protected void showProgressDialog(int stringResId) {
        alert.showProgressDialog(stringResId);
    }

    protected void showProgressDialog(String message) {
        alert.showProgressDialog(message);
    }

    protected void showOrUpdateProgressDialog(String message) {
        alert.showOrUpdateProgressDialog(message);
    }

    protected void dismissProgressDialog() {
        alert.dismissProgressDialog();
    }

}


