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

/**
 * Activity that displays list of search results.
 */
public class ImageListActivity extends MvpAppCompatActivity implements ImageListView {
    // constants
    private static final int LIST_VISIBLE_THRESHOLD = 7;

    @InjectPresenter
    ImageListPresenter presenter;

    private ImageListAdapter adapter;

    // list state
    boolean loading = true;
    int previousTotal = 0;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    // views
    private View progressBar;
    private Button searchButton;
    EditText keywordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init views
        progressBar = findViewById(R.id.progress_bar);
        keywordEditText = (EditText) findViewById(R.id.keyword);
        searchButton = (Button) findViewById(R.id.start_search);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

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
                presenter.loadImagesByKeyword(keywordEditText.getText().toString(), true);
            }
        });

        // setup recycler view
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageListAdapter(this, new ImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                presenter.loadImagesFromPage(position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                } else if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + LIST_VISIBLE_THRESHOLD)) {
                    presenter.loadImagesByKeyword(keywordEditText.getText().toString(), false);
                    loading = true;
                }
            }
        });
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
