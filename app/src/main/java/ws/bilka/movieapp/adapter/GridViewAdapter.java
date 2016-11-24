package ws.bilka.movieapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.List;

import it.sephiroth.android.library.picasso.MemoryPolicy;
import it.sephiroth.android.library.picasso.Picasso;
import ws.bilka.movieapp.R;
import ws.bilka.movieapp.model.ActorsItem;

public class GridViewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ActorsItem> actorsItems;

    private static final String TAG = GridViewAdapter.class.getSimpleName();

    public GridViewAdapter(Activity activity, List<ActorsItem> actorsItems) {
        this.activity = activity;
        this.actorsItems = actorsItems;
    }

    @Override
    public int getCount() {
        return actorsItems.size();
    }

    @Override
    public Object getItem(int i) {
        return actorsItems.get(i);
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
            convertView = inflater.inflate(R.layout.actors_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        TextView character = (TextView) convertView.findViewById(R.id.character);

        CircularImageView image = (CircularImageView)convertView.findViewById(R.id.circleImage);

        final ActorsItem itemActors = actorsItems.get(position);

        name.setText(itemActors.getName());
        character.setText(itemActors.getCharacter());
        String imageActor = itemActors.getPhoto();

        Picasso.with(convertView.getContext()).load("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + imageActor).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_photo).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);

        return convertView;
    }

}
