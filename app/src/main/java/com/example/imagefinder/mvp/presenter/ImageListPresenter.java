package com.example.imagefinder.mvp.presenter;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.Patterns;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.imagefinder.R;
import com.example.imagefinder.api.BingSearchApi;
import com.example.imagefinder.mvp.model.ImageInfo;
import com.example.imagefinder.mvp.model.gson.BingSearchResults;
import com.example.imagefinder.mvp.view.ImageListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Presenter updates view and handles events from user.
 */
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

    /**
     * Notifies the presenter about new value of key word.
     *
     * @param text New value of key word.
     */
    public void onKeywordChanged(CharSequence text) {
        final boolean searchEnabled = text != null && text.length() > 0;
        getViewState().setSearchEnabled(searchEnabled);
    }

    /**
     * Loads list of search results for the entered key word.
     *
     * @param keyword       Key word.
     * @param isFirstSearch Flag that indicates if search for a key word is performed for the first time.
     */
    public void loadImagesByKeyword(final String keyword, final boolean isFirstSearch) {
        // show progress bar
        getViewState().hideKeyboard();
        getViewState().showProgress();

        if (isFirstSearch) {
            if (keyword.equals(this.keyword)) {
                // show cached search results during the first search
                getViewState().hideProgress();
                getViewState().showResults(imageInfoList);
                return;
            } else {
                // clear the list of search results
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
                    // update cached key word
                    ImageListPresenter.this.keyword = keyword;

                    // create search results list
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
                // show error
                getViewState().hideProgress();
                getViewState().showError(R.string.error);
            }
        });
    }

    /**
     * Loads list of image URLs from the page associated with the image in search results.
     *
     * @param position Position of an image in the list.
     */
    public void loadImagesFromPage(final int position) {
        getViewState().showProgress();
        final String hostPageUrl = imageInfoList.get(position).contextLink;
        final String excludedImageUrl = imageInfoList.get(position).thumbnailLink;
        new AsyncTask<String, Void, Pair<List<String>, Integer>>() {
            @Override
            protected Pair<List<String>, Integer> doInBackground(String... strings) {
                final String hostPageUrl = strings[0];
                final String excludedImageUrl = strings[1];
                final String uriString = hostPageUrl.startsWith("http") ? hostPageUrl : "http://" + hostPageUrl;
                final Uri uri = Uri.parse(uriString);
                try {
                    // load HTML code
                    final Document doc = Jsoup.connect(uri.toString()).get();

                    // find img tags
                    final Elements images = doc.select("img");

                    // create list of image URLs
                    List<String> results = new ArrayList<>(images.size());
                    for (Element image : images) {
                        // get src attribute of img tag
                        String imageUrl = image.attr("src");

                        // correct relative URLs
                        if (imageUrl.startsWith("/")) {
                            imageUrl = uri.getScheme() + "://" + uri.getHost() + imageUrl;
                        }

                        // exclude the original image URL and validate it
                        if (!excludedImageUrl.equals(imageUrl) && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                            results.add(imageUrl);
                        }
                    }
                    return new Pair<>(results, 0);
                } catch (HttpStatusException e) {
                    if (e.getStatusCode() == 404) {
                        return new Pair<>(null, R.string.error_404);
                    } else {
                        return new Pair<>(null, R.string.error);
                    }
                } catch (IOException e) {
                    return new Pair<>(null, R.string.error);
                }
            }

            @Override
            protected void onPostExecute(Pair<List<String>, Integer> results) {
                getViewState().hideProgress();
                List<String> imagesUrlList = results.first;
                if (imagesUrlList == null) {
                    // show error
                    getViewState().showError(results.second);
                } else {
                    if (imagesUrlList.isEmpty()) {
                        // image URL list is empty
                        getViewState().showError(R.string.error_nothing_found);
                    } else {
                        // show found images
                        imageInfoList.get(position).setAssociatedImagesList(imagesUrlList);
                        getViewState().showResults(imageInfoList);
                    }
                }
            }
        }.execute(hostPageUrl, excludedImageUrl);
    }
}
