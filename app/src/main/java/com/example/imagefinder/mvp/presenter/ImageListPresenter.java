package com.example.imagefinder.mvp.presenter;

import android.os.AsyncTask;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.imagefinder.R;
import com.example.imagefinder.Settings;
import com.example.imagefinder.mvp.view.ImageListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@InjectViewState
public class ImageListPresenter extends MvpPresenter<ImageListView> {

    List<String> uriList;
    String keyword;

    public void onKeywordChanged(CharSequence text) {
        final boolean searchEnabled = text != null && text.length() > 0;
        getViewState().setSearchEnabled(searchEnabled);
    }

    public void searchImages(final String keyword) {
        getViewState().showProgress();
        getViewState().hideError();

        if (uriList != null && keyword.equals(this.keyword)) {
            getViewState().hideProgress();
            getViewState().showResults(uriList);
        } else {
            final String url = Settings.URL + keyword;
            new AsyncTask<String, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(String... params) {
                    try {
                        Document doc = Jsoup
                                .connect(params[0])
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .maxBodySize(0)
                                .get();
                        // get div with results (its id is "res")
                        Elements results = doc.select("#res");
                        // get image tags
                        Elements images = results.select("img");
                        // get image URIs
                        if (!images.isEmpty()) {
                            List<String> imageUriList = new ArrayList<>();
                            for (Element image : images) {
                                imageUriList.add(image.attr("src"));
                            }
                            return imageUriList;
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<String> imageUriList) {
                    if (imageUriList != null) {
                        uriList = imageUriList;
                        ImageListPresenter.this.keyword = keyword;
                        getViewState().hideProgress();
                        getViewState().showResults(imageUriList);
                    } else {
                        getViewState().hideProgress();
                        getViewState().showError(R.string.error);
                    }
                }
            }.execute(url);
        }
    }
}
