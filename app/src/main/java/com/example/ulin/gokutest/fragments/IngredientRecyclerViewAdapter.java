package com.example.ulin.gokutest.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ulin.gokutest.R;
import com.example.ulin.gokutest.lib.Ingredient;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ingredient} and makes a call to the
 * specified {@link IngredientFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class IngredientRecyclerViewAdapter extends RecyclerView.Adapter<IngredientRecyclerViewAdapter.ViewHolder> {

    private final List<Ingredient> items;
    private final IngredientFragment.OnListFragmentInteractionListener listener;
    private Context context;

    public IngredientRecyclerViewAdapter(List<Ingredient> items, IngredientFragment.OnListFragmentInteractionListener listener, Context context) {
        this.items    = items;
        this.listener = listener;
        this.context  = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ingredient, parent, false);

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
        private final View          view;
        private final ImageView     imageView;
        private final LinearLayout  checkedView;
        private final TextView      idView;
        private final TextView      categoryView;
        private final TextView      nameView;
        private Ingredient          item;

        public ViewHolder(View view) {
            super(view);
            this.view    = view;
            imageView    = (ImageView) view.findViewById(R.id.image);
            checkedView  = (LinearLayout) view.findViewById(R.id.checked);
            idView       = (TextView) view.findViewById(R.id.ingredient_id);
            categoryView = (TextView) view.findViewById(R.id.category_id);
            nameView     = (TextView) view.findViewById(R.id.name);
        }

        public void setItem(Ingredient item){
            this.item = item;

            idView.setText(item.id);
            categoryView.setText(item.category);
            nameView.setText(item.name.toUpperCase());

            if(item.checked)
                checkedView.setBackgroundResource(R.mipmap.bg_ingredientname_checked);
            else
                checkedView.setBackgroundResource(R.mipmap.bg_ingredientname);

            Picasso.with(context).load("http://goku.ngeartstudio.com/images/" + item.image).placeholder(R.mipmap.ingredient_thumb).into(imageView);
        }

        public void cleanup(){
            Picasso.with(context).cancelRequest(imageView);
            imageView.setImageResource(R.mipmap.ingredient_thumb);
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
