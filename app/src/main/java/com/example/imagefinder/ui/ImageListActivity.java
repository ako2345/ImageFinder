package com.example.imagefinder.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.imagefinder.R;
import com.example.imagefinder.mvp.model.ImageInfo;
import com.example.imagefinder.mvp.presenter.ImageListPresenter;
import com.example.imagefinder.mvp.view.ImageListView;

import java.util.List;

public class ImageListActivity extends MvpAppCompatActivity implements ImageListView {

    @InjectPresenter
    ImageListPresenter presenter;

    RecyclerView recyclerView;

    private View progressBar;
    EditText keywordEditText;
    Button searchButton;
    private ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init views
        progressBar = findViewById(R.id.progress_bar);
        keywordEditText = (EditText) findViewById(R.id.keyword);
        searchButton = (Button) findViewById(R.id.start_search);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        // setup edit text
        keywordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.onKeywordChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // setup search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.searchImages(keywordEditText.getText().toString());
            }
        });

        // setup recycler view
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setSearchEnabled(boolean enabled) {
        searchButton.setEnabled(enabled);
    }

    @Override
    public void hideKeyboard() {
        final View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
    public void showResults(List<ImageInfo> imageInfoList) {
        adapter.setImageInfoList(imageInfoList);
    }
}
