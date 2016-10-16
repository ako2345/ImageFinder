package com.example.imagefinder.mvp.presenter;

import android.support.annotation.NonNull;
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

    @Nullable
    List<ImageInfo> imageInfoList;
    @Nullable
    String keyword;
    @NonNull
    private final Gson gson = new GsonBuilder().create();
    @NonNull
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BingSearchApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public void onKeywordChanged(CharSequence text) {
        final boolean searchEnabled = text != null && text.length() > 0;
        getViewState().setSearchEnabled(searchEnabled);
    }

    public void searchImages(final String keyword) {
        getViewState().hideKeyboard();
        getViewState().showProgress();
        getViewState().hideError();

        if (imageInfoList != null && keyword.equals(this.keyword)) {
            getViewState().hideProgress();
            getViewState().showResults(imageInfoList);
        } else {
            BingSearchApi customSearchService = retrofit.create(BingSearchApi.class);
            Call<BingSearchResults> searchCall = customSearchService.searchImages(keyword, 30, 0);
            searchCall.enqueue(new Callback<BingSearchResults>() {
                @Override
                public void onResponse(Call<BingSearchResults> call, Response<BingSearchResults> response) {
                    final BingSearchResults searchResults = response.body();
                    final List<BingSearchResults.Image> imageList = searchResults != null ? searchResults.value : null;
                    final int imagesCount = imageList != null ? imageList.size() : 0;
                    if (imagesCount == 0) {
                        ImageListPresenter.this.imageInfoList = null;
                        getViewState().hideProgress();
                        getViewState().showError(R.string.error_nothing_found);
                    } else {
                        // create image info list
                        final List<ImageInfo> imageInfoList = new ArrayList<>(imagesCount);
                        for (BingSearchResults.Image image : imageList) {
                            final ImageInfo imageInfo = new ImageInfo(
                                    image.thumbnailUrl,
                                    image.hostPageUrl,
                                    image.width,
                                    image.height
                            );
                            imageInfoList.add(imageInfo);
                        }

                        // update cached values
                        ImageListPresenter.this.imageInfoList = imageInfoList;
                        ImageListPresenter.this.keyword = keyword;

                        // update view state
                        getViewState().hideProgress();
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
}
