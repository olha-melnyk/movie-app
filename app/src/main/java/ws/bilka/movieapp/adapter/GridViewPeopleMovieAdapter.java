package ws.bilka.movieapp.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.sephiroth.android.library.picasso.MemoryPolicy;
import it.sephiroth.android.library.picasso.Picasso;
import ws.bilka.movieapp.R;
import ws.bilka.movieapp.model.ActorMovieItem;

public class GridViewPeopleMovieAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ActorMovieItem> actorMoviesItems;

    private static final String TAG = GridViewAdapter.class.getSimpleName();

    public GridViewPeopleMovieAdapter(Activity activity, List<ActorMovieItem> actorsItems) {
        this.activity = activity;
        this.actorMoviesItems = actorsItems;
    }

    @Override
    public int getCount() {
        return actorMoviesItems.size();
    }

    @Override
    public Object getItem(int i) {
        return actorMoviesItems.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.actor_movies_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.title);

        ImageView image = (ImageView)convertView.findViewById(R.id.imageMovie);

        final ActorMovieItem itemActors = actorMoviesItems.get(position);

        name.setText(itemActors.getTitle());
        String imageMovie = itemActors.getImage();

        Picasso.with(convertView.getContext()).load("https://image.tmdb.org/t/p/w185_and_h278_bestv2" + imageMovie).placeholder(R.drawable.placeholder_small_image)
                .error(R.drawable.placeholder_small_image).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);

        return convertView;
    }
}
