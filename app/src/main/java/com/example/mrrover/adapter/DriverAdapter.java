package com.example.mrrover.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mrrover.R;
import com.example.mrrover.model.DriverModel;

import java.util.List;


public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    private List<DriverModel> driverList;

    private  onBookListener onBookListener;



    public DriverAdapter(List<DriverModel> driverList) {

        this.driverList = driverList;
    }

    public interface onBookListener {
        void onBook(int position);
    }
     public  void  setOnBookListener (onBookListener  listener){
        this.onBookListener =  listener;
     }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drivers, parent,false);
      return  new  DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverAdapter.DriverViewHolder holder, int position) {
        DriverModel information = driverList.get(position);
        holder.bind(information);


    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class DriverViewHolder  extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView gender;
        private  final Button choose;
        private final ImageView profile;
        private final TextView rating111;
        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.location);
            choose  = itemView.findViewById(R.id.choose_button);
            profile  = itemView.findViewById(R.id.profile_image);
            rating111 = itemView.findViewById(R.id.rating1);

            choose.setOnClickListener(  V->{
                int position = getAdapterPosition();

                if (position !=  RecyclerView.NO_POSITION && onBookListener !=null){
                    onBookListener.onBook(position);
                }
            });
        }

        public void bind(DriverModel  information){
            name.setText(information.getFullName());
            gender.setText(information.getGender());

            Double rating = information.getRating();
            if (rating != null) {
                // Format to 1 decimal place, if required
                String formattedRating = String.format("%.1f", rating);
                rating111.setText(formattedRating);
            } else {
                rating111.setText("No ratings yet");
            }

           Glide.with(itemView.getContext())
                    .load(R.drawable.logodriver)
                    .placeholder(R.drawable.profile_icon)  // Provide a placeholder image
                    .error(R.drawable.profile_icon)  // Show this if loading fails
                    .into(profile);
        }
    }
}
