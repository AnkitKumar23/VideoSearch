package com.github.ankitkumar23.videosearch.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ankitkumar23.videosearch.R;
import com.github.ankitkumar23.videosearch.model.QueryResult;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<QueryResult.Result> {

    private final static String TAG = "CustomAdapter";
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflator;

    public CustomAdapter(Context context, int resource, List<QueryResult.Result> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /**
     * Method for loading item layouts in list view
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflator.inflate(mResource, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.logoView = (ImageView) convertView.findViewById(R.id.logoView);
            holder.ratingView = (TextView) convertView.findViewById(R.id.ratingView);
            holder.likesView = (TextView) convertView.findViewById(R.id.likesView);
            holder.viewsView = (TextView) convertView.findViewById(R.id.viewsView);
            holder.durationView = (TextView) convertView.findViewById(R.id.durationView);
            holder.postedView = (TextView) convertView.findViewById(R.id.postedView);

            holder.descView = (TextView) convertView.findViewById(R.id.descView);
            holder.titleView = (TextView) convertView.findViewById(R.id.titleView);
            holder.userNameView = (TextView) convertView.findViewById(R.id.userNameView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        final TextView desc = holder.descView;
        final TextView title = holder.titleView;
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(desc.getVisibility() == View.VISIBLE) {
                    desc.setVisibility(View.GONE);
                    title.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.ic_arrow_drop_down_black_24dp,0);
                } else {
                    desc.setVisibility(View.VISIBLE);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_arrow_drop_up_black_24dp, 0);
                }
            }
        });
        QueryResult.Result res = getItem(position);
        //Log.d(TAG, "Getting the view at position:" + position);
        String imageUrl = "";
        int REQUIRED_IMAGE_INDEX = 1;
        if(res != null &&
                res.pictures != null &&
                res.pictures.sizes != null &&
                res.pictures.sizes.size() >= REQUIRED_IMAGE_INDEX+1) {
            imageUrl = res.pictures.sizes.get(REQUIRED_IMAGE_INDEX).link;
            Picasso.with(mContext)
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .tag(mContext)
                    .into(holder.imageView);
        }

        holder.ratingView.setText(res.content_rating.get(0)+" content");
        holder.likesView.setText(NumberFormat.getInstance().format(res.metadata.connections.likes.total)+" likes");
        holder.viewsView.setText(NumberFormat.getInstance().format(res.stats.plays)+" views");
        holder.durationView.setText(DateUtils.formatElapsedTime(res.duration));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        formatter.setLenient(false);
        try {
            //Log.d(TAG, "created on: "+ res.created_time);
            Date postedDate = formatter.parse(res.created_time);
            holder.postedView.setText("Posted on "+DateUtils.getRelativeTimeSpanString(postedDate.getTime()));

        } catch (ParseException e) {
            Log.d(TAG, "Exception: " + e.toString());
        }

        //holder.titleView.setTypeface(holder.titleView.getTypeface(), Typeface.BOLD);
        holder.titleView.setText(res.name);
        holder.descView.setText(res.description);
        holder.userNameView.setText(res.user.name);

        String logoUrl = "";
        if(res != null &&
                res.user != null &&
                res.user.pictures != null &&
                res.user.pictures.sizes != null &&
                res.user.pictures.sizes.size() >= 1) {
            logoUrl = res.user.pictures.sizes.get(0).link;
            Picasso.with(mContext)
                    .load(logoUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .tag(mContext)
                    .into(holder.logoView);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView,logoView;
        TextView ratingView, likesView, viewsView,
                durationView, titleView, descView,
                postedView, userNameView;
    }
}
