package es.jmoral.simplecomicreader.activities.viewer;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import es.jmoral.simplecomicreader.database.ComicDBHelper;
import es.jmoral.simplecomicreader.models.Comic;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by owniz on 16/04/17.
 */

class ViewerInteractorImpl implements ViewerInteractor {
    @Override
    public void readComic(String comicPath, int numPages, OnReadComicListener onReadComicListener) {
        ArrayList<String> pages = new ArrayList<>();

        for (int i = 0; i < numPages; i++) {
            pages.add(comicPath + "/" + i + ".png");
        }

        onReadComicListener.onReadComicOk(pages);
    }

    @Override
    public void setCurrentPage(@NonNull Context context, Comic comic) {
        cupboard().withDatabase(ComicDBHelper.getComicDBHelper(context).getWritableDatabase()).put(comic);

        //ContentValues values = new ContentValues(1);
        //values.put("currentPage", pageOnExits);
        //cupboard().withDatabase(ComicDBHelper.getComicDBHelper(context).getWritableDatabase()).update(Comic.class, values, "_id = ?", String.valueOf(comic.get_id()));
    }
}
