package com.ossian.navar.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.mapbox.mapboxsdk.geometry.LatLng;
import com.ossian.navar.NavigationComponent.NavigationActivity;
import com.ossian.navar.R;
import com.ossian.navar.model.IndividualLocation;
import com.ossian.navar.secactivities.PoisDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter to display a list of location cards on top of the map
 */
public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> implements Filterable {

    //Adaptador primario - Lista de Locaciones
    private static List<IndividualLocation> listOfLocations;
    //Adaptador secundario para SearchView
    private static List<IndividualLocation> listSearhlocations;
    //Variables internas de la clase
    private Context context;
    private int selectedTheme;
    private static ClickListener clickListener;
    private Drawable emojiForCircle = null;
    private Drawable backgroundCircle = null;
    private int upperCardSectionColor = 0;
    private int locationNameColor = 0;
    private int locationAddressColor = 0;
    private int locationPhoneNumColor = 0;
    private int locationPhoneHeaderColor = 0;
    private int locationHoursColor = 0;
    private int locationHoursHeaderColor = 0;
    private int locationDistanceNumColor = 0;
    private int milesAbbreviationColor = 0;


    public LocationRecyclerViewAdapter(List<IndividualLocation> styles,
                                       Context context, ClickListener cardClickListener, int selectedTheme) {
        this.context = context;
        this.listOfLocations = styles;
        this.selectedTheme = selectedTheme;
        this.clickListener = cardClickListener;
        //Search List Location Elements
        listSearhlocations = new ArrayList<>(styles);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int singleRvCardToUse = R.layout.single_location_map_view_rv_card;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(singleRvCardToUse, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public Filter getFilter() {
        return searchfilterpoints;
    }

    private Filter searchfilterpoints = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<IndividualLocation> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(listSearhlocations);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (IndividualLocation item : listSearhlocations) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listOfLocations.clear();
            listOfLocations.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
    //**Search End Class

    public interface ClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return listOfLocations.size();

    }

    @Override
    public void onBindViewHolder(ViewHolder card, int position) {

        IndividualLocation locationCard = listOfLocations.get(position);

        card.nameTextView.setText(locationCard.getName());
        card.addressTextView.setText(locationCard.getAddress());
        card.phoneNumTextView.setText(locationCard.getPhoneNum());
        card.hoursTextView.setText(locationCard.getHours());
        card.distanceNumberTextView.setText(locationCard.getDistance());
        //Imagen de CardView
        String imagerulcv = locationCard.getImageurlcv();
        Picasso.get().load(imagerulcv).fit().centerInside().into(card.imageCardView);
        //Imagenes
        card.imagecv = locationCard.getImageurlcv();
        card.imageone = locationCard.getImageone();
        card.imagetwo = locationCard.getImagetwo();
        card.imagethree = locationCard.getImagethree();
        //Descripcion
        card.descripcionpoi = locationCard.getDescription();
        //Location POI
        card.locationpoi = locationCard.getLocation();

        //Seleccion de tema***
        switch (selectedTheme) {
            case R.style.AppTheme_Blue:
                emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ice_cream_icon, null);
                backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_circle, null);
                setColors(R.color.colorPrimary_blue, R.color.black, R.color.black, R.color.black,
                        R.color.black, R.color.black, R.color.black, R.color.black, R.color.black);
                setAlphas(card, .41f, .48f, 100f, .48f,
                        100f,
                        .41f);
                break;
            case R.style.AppTheme_Neutral:
                emojiForCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.house_icon, null);
                backgroundCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.white_circle, null);
                setColors(R.color.colorPrimaryDark_neutral, R.color.black, R.color.black, R.color.black,
                        R.color.black, R.color.black, R.color.black, R.color.black, R.color.black);
                setAlphas(card, .37f, .37f, 100f, .37f,
                        100f,
                        .37f);
                break;
        }
        card.emojiImageView.setImageDrawable(emojiForCircle);
        card.constraintUpperColorSection.setBackgroundColor(upperCardSectionColor);
        card.backgroundCircleImageView.setImageDrawable(backgroundCircle);
        card.nameTextView.setTextColor(locationNameColor);
        card.phoneNumTextView.setTextColor(locationPhoneNumColor);
        card.hoursTextView.setTextColor(locationHoursColor);
        card.hoursHeaderTextView.setTextColor(locationHoursHeaderColor);
        card.distanceNumberTextView.setTextColor(locationDistanceNumColor);
        card.milesAbbreviationTextView.setTextColor(milesAbbreviationColor);
        card.addressTextView.setTextColor(locationAddressColor);
        card.phoneHeaderTextView.setTextColor(locationPhoneHeaderColor);
    }

    private void setColors(int colorForUpperCard, int colorForName, int colorForAddress,
                           int colorForHours, int colorForHoursHeader, int colorForPhoneNum,
                           int colorForPhoneHeader, int colorForDistanceNum, int colorForMilesAbbreviation) {
        upperCardSectionColor = ResourcesCompat.getColor(context.getResources(), colorForUpperCard, null);
        locationNameColor = ResourcesCompat.getColor(context.getResources(), colorForName, null);
        locationAddressColor = ResourcesCompat.getColor(context.getResources(), colorForAddress, null);
        locationHoursColor = ResourcesCompat.getColor(context.getResources(), colorForHours, null);
        locationHoursHeaderColor = ResourcesCompat.getColor(context.getResources(), colorForHoursHeader, null);
        locationPhoneNumColor = ResourcesCompat.getColor(context.getResources(), colorForPhoneNum, null);
        locationPhoneHeaderColor = ResourcesCompat.getColor(context.getResources(), colorForPhoneHeader, null);
        locationDistanceNumColor = ResourcesCompat.getColor(context.getResources(), colorForDistanceNum, null);
        milesAbbreviationColor = ResourcesCompat.getColor(context.getResources(), colorForMilesAbbreviation, null);
    }

    private void setAlphas(ViewHolder card, float addressAlpha, float hoursHeaderAlpha, float hoursNumAlpha, float phoneHeaderAlpha, float phoneNumAlpha, float milesAbbreviationAlpha) {
        card.addressTextView.setAlpha(addressAlpha);
        card.hoursHeaderTextView.setAlpha(hoursHeaderAlpha);
        card.hoursTextView.setAlpha(hoursNumAlpha);
        card.phoneHeaderTextView.setAlpha(phoneHeaderAlpha);
        card.phoneNumTextView.setAlpha(phoneNumAlpha);
        card.milesAbbreviationTextView.setAlpha(milesAbbreviationAlpha);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        TextView addressTextView;
        TextView phoneNumTextView;
        TextView hoursTextView;
        TextView distanceNumberTextView;
        TextView hoursHeaderTextView;
        TextView milesAbbreviationTextView;
        TextView phoneHeaderTextView;
        ConstraintLayout constraintUpperColorSection;
        CardView cardView;
        ImageView backgroundCircleImageView;
        ImageView emojiImageView;
        ImageView imageCardView;
        Button buttoncardv;
        Button buttonnav;
        //URL de imagenes
        String imagecv;
        String imageone;
        String imagetwo;
        String imagethree;
        //Descripcion
        String descripcionpoi;
        //
        LatLng locationpoi;


        ViewHolder(final View itemView) {
            super(itemView);
            imageCardView = itemView.findViewById(R.id.imagecview);
            nameTextView = itemView.findViewById(R.id.location_name_tv);
            addressTextView = itemView.findViewById(R.id.address_description_tv);
            phoneNumTextView = itemView.findViewById(R.id.location_phone_num_tv);
            phoneHeaderTextView = itemView.findViewById(R.id.phone_header_tv);
            hoursTextView = itemView.findViewById(R.id.location_hours_tv);
            backgroundCircleImageView = itemView.findViewById(R.id.background_circle);
            emojiImageView = itemView.findViewById(R.id.emoji);
            constraintUpperColorSection = itemView.findViewById(R.id.constraint_upper_color);
            distanceNumberTextView = itemView.findViewById(R.id.distance_num_tv);
            hoursHeaderTextView = itemView.findViewById(R.id.hours_header_tv);
            milesAbbreviationTextView = itemView.findViewById(R.id.miles_mi_tv);
            buttoncardv = itemView.findViewById(R.id.btnvmscv);
            buttonnav = itemView.findViewById(R.id.btnnav);
            cardView = itemView.findViewById(R.id.map_view_location_card);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    clickListener.onItemClick(getLayoutPosition());

                }

            });
            //--Boton "VER MAS"
            buttoncardv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(getLayoutPosition());

                    String namepoi = nameTextView.getText().toString();
                    String address = addressTextView.getText().toString();
                    String distance = distanceNumberTextView.getText().toString();

                    //type-poi() & kell-poi()

                    Intent intent;
                    intent = new Intent(view.getContext(), PoisDetailsActivity.class);
                    //enviando datos a POIS DETAILS ACTIVITY
                    intent.putExtra("namepoicv", namepoi);
                    intent.putExtra("direccv", address);
                    intent.putExtra("distance", distance);
                    intent.putExtra("urlcv", imagecv);
                    intent.putExtra("imgone", imageone);
                    intent.putExtra("imgtwo", imagetwo);
                    intent.putExtra("imgthree", imagethree);
                    intent.putExtra("description", descripcionpoi);
                    view.getContext().startActivity(intent);
                }
            });
            //--Boton "COMO LLEGUAR"
            buttonnav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(getLayoutPosition());

                    Intent intent;
                    intent = new Intent(view.getContext(), NavigationActivity.class);
                    intent.putExtra("poilocation", locationpoi);

                    view.getContext().startActivity(intent);

                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
