package com.example.imagefinder.mvp.presenter;

import android.os.AsyncTask;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.imagefinder.R;
import com.example.imagefinder.Settings;
import com.example.imagefinder.mvp.model.ImageInfo;
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

    List<ImageInfo> imageInfoList;
    String keyword;

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
            final String url = Settings.SEARCH_URL_PREFIX + keyword;
            new AsyncTask<String, Void, List<ImageInfo>>() {
                @Override
                protected List<ImageInfo> doInBackground(String... params) {
                    try {
                        Document doc = Jsoup
                                .connect(params[0])
                                .maxBodySize(0)
                                .get();
                        // get div with results (its id is "res")
                        Elements results = doc.select("#res");
                        // get image tags
                        Elements images = results.select("img");
                        // get image URIs
                        if (!images.isEmpty()) {
                            List<ImageInfo> imageInfoArrayList = new ArrayList<>(images.size());
                            for (Element image : images) {
                                final String imageUri = image.attr("src");
                                final Element parent = image.parent();
                                final String pageUri = parent.attr("href").split("&")[0].split("q=")[1];
                                if (imageUri.length() > 0) {
                                    final ImageInfo imageInfo = new ImageInfo(imageUri, pageUri);
                                    imageInfoArrayList.add(imageInfo);
                                }
                            }
                            return imageInfoArrayList;
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<ImageInfo> imageInfoList) {
                    if (imageInfoList != null) {
                        ImageListPresenter.this.imageInfoList = imageInfoList;
                        ImageListPresenter.this.keyword = keyword;
                        getViewState().hideProgress();
                        getViewState().showResults(imageInfoList);
                    } else {
                        getViewState().hideProgress();
                        getViewState().showError(R.string.error);
                    }
                }
            }.execute(url);
        }
    }
}
