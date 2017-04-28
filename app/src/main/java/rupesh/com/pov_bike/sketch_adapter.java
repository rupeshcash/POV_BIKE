package rupesh.com.pov_bike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rupesh kashyap on 4/1/2017.
 */

public class sketch_adapter extends ArrayAdapter<sketch_obj> {

    private int color;

    public sketch_adapter(Context context, ArrayList<sketch_obj> sketches, int resource) {

        super(context,0 , sketches);
        color = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.sketchlistobj, parent, false);
        }

         sketch_obj currentword = (sketch_obj) getItem(position);
        TextView mname = (TextView) listItemView.findViewById(R.id.name);
        ImageView msketch = (ImageView) listItemView.findViewById(R.id.sketche);
       // View textc = listItemView.findViewById(R.id.linearl);
        int coloractual = ContextCompat.getColor(getContext() , color);

        //textc.setBackgroundColor(coloractual);

       mname.setText(currentword.getName());


      //  File f = new File(currentword.getImageId());

        Bitmap bitmap = BitmapFactory.decodeFile(currentword.getImageId());
        msketch.setImageBitmap(bitmap);

        return listItemView;
    }



}
