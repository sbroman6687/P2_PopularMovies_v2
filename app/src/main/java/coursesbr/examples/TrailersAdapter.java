package coursesbr.examples;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Soledad on 6/17/2016.
 */
public class TrailersAdapter extends ArrayAdapter<MovieTrailers> {
    /**
     * This is my own custom constructor
     * The context (current context) is used to inflate the layout file, and the list (MovieReviews)
     * is the list data to populate into the listview
     */

    public TrailersAdapter(Activity context, List<MovieTrailers> result) {
        super(context,0,result);
    }

    /**
     * Provides a view for an AdapterView (ListView in this case)
     * position: The AdapterView position that is requesting a view
     * convertView: The recycled view to populate.
     * parent: The parent ViewGroup that is used for inflation
     * return: The View for the position in the AdapterView
     */

    @Override

    public View getView(int position, View convertView, ViewGroup parent){

        MovieTrailers result = getItem(position);

        //2. Adapters recycle views to AdapterViews. If this is a new View Object, then inflate the layout.
        //If not, this view already has the layout inflate from previous call to getView, we just modify the view widgets.

        if (convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item,parent,false);
            TextView TrailerName = (TextView)convertView.findViewById(R.id.trailer_item_textTitle);
            TrailerName.setText(result.name);

            //imagen en el trailer???
        }

        return convertView;

    }

}
