package ws.bilka.movieapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import it.sephiroth.android.library.picasso.MemoryPolicy;
import it.sephiroth.android.library.picasso.Picasso;
import ws.bilka.movieapp.DynamicImageView;
import ws.bilka.movieapp.R;
import ws.bilka.movieapp.model.NowPlayingItem;

public class NowPlayingAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<NowPlayingItem> nowPlayingItemsItems;


    public static String LOG_TAG = "my_log";

    public NowPlayingAdapter(Activity activity, List<NowPlayingItem> nowPlayingItemsItems) {
        this.activity = activity;
        this.nowPlayingItemsItems = nowPlayingItemsItems;
    }

    @Override
    public int getCount() {
        return nowPlayingItemsItems.size();
    }

    @Override
    public Object getItem(int i) {
        return nowPlayingItemsItems.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.movie_item, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setShadowLayer(50, 0, 0, Color.BLACK);

        TextView rating = (TextView) view.findViewById(R.id.rating);
        rating.setShadowLayer(30, 0, 0, Color.BLACK);

        DynamicImageView image = (DynamicImageView) view.findViewById(R.id.genreImage);

        final NowPlayingItem item = nowPlayingItemsItems.get(position);

        title.setText(item.getNowPlayingTitle());
        rating.setText(item.getNowPlayingRating());
        String url = item.getNowPlayingImage();

        Log.i(LOG_TAG, "Movie URL:" + url);
        Picasso.with(view.getContext()).load("https://image.tmdb.org/t/p/w500_and_h281_bestv2/" + url).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);

        return view;
    }
}
