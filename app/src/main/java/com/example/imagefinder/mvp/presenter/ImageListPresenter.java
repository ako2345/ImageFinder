package com.example.imagefinder.mvp.presenter;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.imagefinder.R;
import com.example.imagefinder.app.BingSearchApi;
import com.example.imagefinder.mvp.model.ImageInfo;
import com.example.imagefinder.mvp.model.gson.BingSearchResults;
import com.example.imagefinder.mvp.view.ImageListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@InjectViewState
public class ImageListPresenter extends MvpPresenter<ImageListView> {
    // constants
    private static final int RESULTS_TO_LOAD = 30;

    // image data list
    List<ImageInfo> imageInfoList = new ArrayList<>();

    // search query
    @Nullable
    String keyword;

    // network stuff
    private final Gson gson = new GsonBuilder().create();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BingSearchApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    private final BingSearchApi customSearchService = retrofit.create(BingSearchApi.class);

    public void onKeywordChanged(CharSequence text) {
        final boolean searchEnabled = text != null && text.length() > 0;
        getViewState().setSearchEnabled(searchEnabled);
    }

    public void loadImages(final String keyword, final boolean isFirstSearch) {
        // show progress bar
        getViewState().hideKeyboard();
        getViewState().showProgress();

        // show cached results during the first search
        if (isFirstSearch) {
            if (keyword.equals(this.keyword)) {
                getViewState().hideProgress();
                getViewState().showResults(imageInfoList);
                return;
            } else {
                imageInfoList.clear();
            }
        }

        // load image data
        final int loadedResultsNumber = imageInfoList.size();
        Call<BingSearchResults> searchCall = customSearchService.searchImages(keyword, RESULTS_TO_LOAD, loadedResultsNumber);
        searchCall.enqueue(new Callback<BingSearchResults>() {
            @Override
            public void onResponse(Call<BingSearchResults> call, Response<BingSearchResults> response) {
                getViewState().hideProgress();
                final BingSearchResults searchResults = response.body();
                final List<BingSearchResults.Image> imageList = searchResults != null ? searchResults.value : null;
                final int imagesCount = imageList != null ? imageList.size() : 0;
                if (imagesCount == 0) {
                    if (isFirstSearch) {
                        getViewState().showError(R.string.error_nothing_found);
                    }
                } else {
                    // update cached values
                    ImageListPresenter.this.keyword = keyword;

                    // create image info list
                    for (BingSearchResults.Image image : imageList) {
                        final ImageInfo imageInfo = new ImageInfo(
                                image.thumbnailUrl,
                                image.hostPageDisplayUrl,
                                image.width,
                                image.height,
                                image.thumbnail.width,
                                image.thumbnail.height
                        );
                        imageInfoList.add(imageInfo);
                    }

                    // update view state
                    getViewState().showResults(imageInfoList);
                }
            }

            @Override
            public void onFailure(Call<BingSearchResults> call, Throwable t) {
                getViewState().hideProgress();
                getViewState().showError(R.string.error);
            }
        });
    }
}
