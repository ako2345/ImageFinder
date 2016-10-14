package com.example.imagefinder.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.imagefinder.R;
import com.example.imagefinder.Settings;
import com.example.imagefinder.mvp.presenter.ImageListPresenter;
import com.example.imagefinder.mvp.view.ImageListView;

import java.util.List;

public class ImageListActivity extends MvpAppCompatActivity implements ImageListView {

    @InjectPresenter
    ImageListPresenter presenter;

    RecyclerView recyclerView;

    @Nullable
    View progressBar;

    private final Handler handler = new Handler();
    private ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init views
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        // setup recycler view
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);

        // load image URIs
        presenter.loadPage(Settings.KEYWORD);
    }

    @Override
    public void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(@StringRes int errorMessageResId) {
        final String errorMessage = getString(errorMessageResId);
        final String tag = ErrorDialogFragment.class.getSimpleName();
        getSupportFragmentManager()
                .beginTransaction()
                .add(ErrorDialogFragment.newInstance(errorMessage), tag)
                .commitAllowingStateLoss();
    }

    @Override
    public void hideError() {
        final String tag = ErrorDialogFragment.class.getSimpleName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showResults(List<String> imageUriList) {
        adapter.setImageUriList(imageUriList);
    }
}
