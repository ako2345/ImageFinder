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
    private final Gson gson = new GsonBuilder().create();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Settings.GOOGLE_CSE_URI)
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
            GoogleCustomSearchApi customSearchService = retrofit.create(GoogleCustomSearchApi.class);
            Call<GResults> searchCall = customSearchService.searchImages(
                    keyword,
                    Settings.GOOGLE_CSE_API_KEY,
                    Settings.GOOGLE_CSE_PROJECT_ID
            );
            searchCall.enqueue(new Callback<GResults>() {
                @Override
                public void onResponse(Call<GResults> call, Response<GResults> response) {
                    GResults responseBody = response.body();

                    final int searchResultsSize = responseBody.items != null ? responseBody.items.size() : 0;
                    if (searchResultsSize > 0) {
                        // create image info list
                        List<ImageInfo> imageInfoList = new ArrayList<>(responseBody.items.size());
                        for (GResults.Item item : responseBody.items) {
                            final GResults.Item.Image image = item.image;
                            final ImageInfo imageInfo = new ImageInfo(
                                    image.thumbnailLink,
                                    image.contextLink,
                                    image.width,
                                    image.height
                            );
                            imageInfoList.add(imageInfo);
                        }

                        // update cached values
                        ImageListPresenter.this.imageInfoList = imageInfoList;
                        ImageListPresenter.this.keyword = keyword;

                        // update view
                        getViewState().hideProgress();
                        getViewState().showResults(imageInfoList);
                    } else {
                        getViewState().hideProgress();
                        getViewState().showError(R.string.error_nothing_found);
                    }
                }

                @Override
                public void onFailure(Call<GResults> call, Throwable t) {
                    getViewState().hideProgress();
                    getViewState().showError(R.string.error);
                }
            });
        }
    }
}
