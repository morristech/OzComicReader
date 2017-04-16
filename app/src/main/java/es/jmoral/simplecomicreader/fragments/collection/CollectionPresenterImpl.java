package es.jmoral.simplecomicreader.fragments.collection;

import java.io.File;
import java.util.ArrayList;

import es.jmoral.simplecomicreader.models.Comic;

/**
 * Created by owniz on 14/04/17.
 */

class CollectionPresenterImpl implements CollectionPresenter, CollectionInteractor.OnReadSavedComicsListener, CollectionInteractor.OnRetrieveComicListener,
        CollectionInteractor.OnSaveComicListener, CollectionInteractor.OnDeleteComicListener {
    private CollectionView collectionView;
    private CollectionInteractor collectionInteractor;

    CollectionPresenterImpl(CollectionView collectionView) {
        this.collectionView = collectionView;
        this.collectionInteractor = new CollectionInteractorImpl();
    }

    @Override
    public void onComicsReadOk(ArrayList<Comic> comics) {
        collectionView.inflateCards(comics);
    }

    @Override
    public void onComicsReadError() {

    }

    @Override
    public void readSavedComics() {
        collectionInteractor.readSavedComics(((CollectionFragment) collectionView).getContext(), this);
    }

    @Override
    public void addComic(File file) {
        collectionInteractor.retrieveComic(((CollectionFragment) collectionView).getContext(), file, this);
    }

    @Override
    public void deleteComic(Comic comic) {
        collectionInteractor.deleteComic(((CollectionFragment) collectionView).getContext(), comic, this);
    }

    @Override
    public void onDestroy() {
        collectionView = null;
    }

    @Override
    public void onComicReceived(Comic comic) {
        collectionInteractor.saveComic(((CollectionFragment) collectionView).getContext(), comic, this);
    }

    @Override
    public void onComicError() {

    }

    @Override
    public void onSavedComicOk(Comic comic) {
        collectionView.updateCards(comic);
    }

    @Override
    public void onSavedComicError() {

    }

    @Override
    public void onDeleteOk() {

    }

    @Override
    public void onDeleteError() {

    }
}
