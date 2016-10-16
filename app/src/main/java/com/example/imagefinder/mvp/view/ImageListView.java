package com.example.imagefinder.mvp.view;

import android.support.annotation.StringRes;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.imagefinder.mvp.model.ImageInfo;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ImageListView extends MvpView {

    void setSearchEnabled(boolean enabled);

    void hideKeyboard();

    void showProgress();

    void hideProgress();

    @StateStrategyType(SkipStrategy.class)
    void showError(@StringRes int errorMessageResId);

    @StateStrategyType(SkipStrategy.class)
    void hideError();

    void showResults(List<ImageInfo> imageInfoList);

}
