package es.jmoral.simplecomicreader.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.prefs.Prefs;
import es.jmoral.simplecomicreader.R;
import es.jmoral.simplecomicreader.fragments.collection.CollectionView;
import es.jmoral.simplecomicreader.models.Comic;
import es.jmoral.simplecomicreader.utils.Constants;

/**
 * Created by owniz on 14/04/17.
 */

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ComicViewHolder> {
    private ArrayList<Comic> comics;
    private OnComicClickListener onComicClickListener;

    public interface OnComicClickListener {
        void onComicClicked(Comic comic);
    }

    public ComicAdapter(ArrayList<Comic> comics, OnComicClickListener onComicClickListener, CollectionView.SortOrder sortOrder) {
        this.comics = comics;
        orderComic(sortOrder, false);
        this.onComicClickListener = onComicClickListener;
    }

    @Override
    public ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ComicViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comic_cardview_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ComicViewHolder holder, int position) {
        Comic comic = comics.get(position);

        File imageFile = new File(comic.getCoverPath());

        Palette palette = Palette.from(BitmapFactory.decodeFile(imageFile.getAbsolutePath())).generate();
        Palette.Swatch swatch = palette.getVibrantSwatch();

        holder.cardView.setBackgroundColor(
                Prefs.with(holder.cardView.getContext()).readBoolean(Constants.KEY_PREFERENCES_THEME)
                ? palette.getMutedColor(Color.WHITE)
                : palette.getLightMutedColor(Color.WHITE));

        /*if (Prefs.with(holder.cardView.getContext()).readBoolean(Constants.KEY_PREFERENCES_THEME)) {
            holder.cardView.setBackgroundColor(palette.getMutedColor(Color.WHITE));
        } else {
            holder.cardView.setBackgroundColor(palette.getLightMutedColor(Color.WHITE));
        }*/

        Glide.with(holder.cardView.getContext()).load(imageFile.getAbsolutePath()).into(holder.imageViewCover);
        holder.textViewTitle.setText(comic.getTitle());
        holder.textViewTitle.setTextColor(swatch == null ? Color.DKGRAY : swatch.getTitleTextColor());
        holder.textViewPages.setText(holder.textViewPages.getContext().getString(R.string.page_of, comic.getCurrentPage(), comic.getNumPages()));
        holder.textViewPages.setTextColor(swatch == null ? Color.DKGRAY : swatch.getTitleTextColor());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(comic.getAddedTimeStamp());
        holder.textViewTimeStamp.setText(date);
        holder.textViewTimeStamp.setTextColor(swatch == null ? Color.DKGRAY : swatch.getTitleTextColor());
    }

    public void insertComic(Comic comic, CollectionView.SortOrder sortOrder) {
        comics.add(comic);
        orderComic(sortOrder, true);
    }

    public void insertComicAtPosition(Comic comic, int position) {
        comics.add(position, comic);
        notifyItemInserted(position);
    }

    public void removeComic(int position) {
        comics.remove(position);
        notifyItemRemoved(position);
    }

    public Comic getComic(int position) {
        return comics.get(position);
    }

    public void orderComic(final CollectionView.SortOrder sortOrder, boolean reorder) {
        Collections.sort(comics, new Comparator<Comic>() {
            @Override
            public int compare(Comic c1, Comic c2) {
                switch (sortOrder) {
                    case SORT_TITTLE:
                        return (c1.getTitle()).compareToIgnoreCase(c2.getTitle());
                    case SORT_NEWEST:
                        return (int) c2.getAddedTimeStamp() - (int) c1.getAddedTimeStamp();
                    case SORT_OLDEST:
                        return (int) c1.getAddedTimeStamp() - (int) c2.getAddedTimeStamp();
                    default:
                        return -1;
                }
            }
        });

        if (reorder)
            notifyItemRangeChanged(0, comics.size());
    }

    public void updateComic(Comic comic) {
        int position = comics.indexOf(comic);

        comics.set(position, comic);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return comics != null ? comics.size() : 0;
    }

    class ComicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardViewComic) CardView cardView;
        @BindView(R.id.imageViewCover) ImageView imageViewCover;
        @BindView(R.id.textViewTitle) TextView textViewTitle;
        @BindView(R.id.textViewPage) TextView textViewPages;
        @BindView(R.id.textViewTimeStamp) TextView textViewTimeStamp;

        ComicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onComicClickListener.onComicClicked(comics.get(getAdapterPosition()));
                }
            });
        }
    }
}
