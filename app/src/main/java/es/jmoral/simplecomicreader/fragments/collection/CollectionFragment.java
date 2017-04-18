package es.jmoral.simplecomicreader.fragments.collection;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import es.jmoral.simplecomicreader.R;
import es.jmoral.simplecomicreader.activities.viewer.ViewerActivity;
import es.jmoral.simplecomicreader.adapters.ComicAdapter;
import es.jmoral.simplecomicreader.fragments.BaseFragment;
import es.jmoral.simplecomicreader.models.Comic;
import es.jmoral.simplecomicreader.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment implements CollectionView {
    @BindView(R.id.recyclerViewComics) RecyclerView recyclerViewComics;

    private CollectionPresenter collectionPresenter;

    public static Fragment newInstance() {
        return new CollectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        collectionPresenter = new CollectionPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, R.layout.fragment_collection, container, savedInstanceState);
    }

    @Override
    protected void setUpViews() {
        recyclerViewComics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteComic(((ComicAdapter) recyclerViewComics.getAdapter()).getComic(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
                ((ComicAdapter) recyclerViewComics.getAdapter()).removeComic(viewHolder.getAdapterPosition());
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewComics);

        readSavedComics();
    }

    @Override
    protected void setListeners() {
        // unused
    }

    @Override
    public void readSavedComics() {
        collectionPresenter.readSavedComics();
    }

    @Override
    public void inflateCards(ArrayList<Comic> comics) {
        recyclerViewComics.setAdapter(new ComicAdapter(comics, new ComicAdapter.OnComicClickListener() {
            @Override
            public void onComicClicked(Comic comic) {
                openComic(comic);
            }
        }));
    }

    @Override
    public void addComic(File file) {
        collectionPresenter.addComic(file);
    }

    @Override
    public void updateCards(Comic comic) {
        ((ComicAdapter) recyclerViewComics.getAdapter()).insertComic(comic);
    }

    @Override
    public void openComic(Comic comic) {
        Intent intent = new Intent(getContext(), ViewerActivity.class);
        intent.putExtra(Constants.KEY_COMIC_PATH, comic.getFilePath());
        intent.putExtra(Constants.KEY_CURRENT_PAGE, comic.getCurrentPage());
        intent.putExtra(Constants.KEY_TOTAL_PAGES, comic.getNumPages());
        startActivity(intent);
    }

    @Override
    public void deleteComic(final Comic comic, final int position) {
        new MaterialDialog.Builder(getContext())
                .content(R.string.delete_comic)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        collectionPresenter.deleteComic(comic);
                        Toasty.success(getContext(), getString(R.string.comic_deleted)).show();
                    }
                })
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ((ComicAdapter) recyclerViewComics.getAdapter()).insertComicAtPosition(comic, position);
                        Toasty.info(getContext(), getString(R.string.deleted_cancelled)).show();
                    }
                })
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toasty.info(getContext(), errorMessage).show();
    }

    @Override
    public void orderComic(SortOrder sortOrder) {
        // unused
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        collectionPresenter.onDestroy();
    }
}
