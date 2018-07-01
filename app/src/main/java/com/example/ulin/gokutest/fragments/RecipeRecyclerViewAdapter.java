package com.example.ulin.gokutest.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ulin.gokutest.R;
import com.example.ulin.gokutest.lib.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe} and makes a call to the
 * specified {@link RecipeFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private final List<Recipe> items;
    private final RecipeFragment.OnListFragmentInteractionListener listener;
    private Context context;

    public RecipeRecyclerViewAdapter(List<Recipe> items, RecipeFragment.OnListFragmentInteractionListener listener, Context context) {
        this.items    = items;
        this.listener = listener;
        this.context  = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(items.get(position));

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public void onViewRecycled(final ViewHolder holder) {
        holder.cleanup();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View       view;
        private final ImageView  imageView;
        private final TextView   nameView;
        private final TextView   timeView;
        private final RatingBar  ratingBar;
        private Recipe           item;

        public ViewHolder(View view) {
            super(view);
            this.view   = view;
            imageView   = (ImageView) view.findViewById(R.id.image);
            nameView    = (TextView) view.findViewById(R.id.name);
            timeView    = (TextView) view.findViewById(R.id.time);
            ratingBar   = (RatingBar) view.findViewById(R.id.rating);
        }

        public void setItem(Recipe item){
            this.item = item;

            String timeString = String.valueOf(item.time) + " " + context.getString(R.string.minutes);

            nameView.setText(item.name);
            timeView.setText(timeString);
            ratingBar.setRating(item.rating);
            Picasso.with(context).load("http://goku.ngeartstudio.com/images/" + item.image).placeholder(R.mipmap.default_thumb).into(imageView);
        }

        public void cleanup(){
            Picasso.with(context).cancelRequest(imageView);
            imageView.setImageResource(R.mipmap.default_thumb);
        }

        public View getView(){
            return view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }
}
