package com.example.mrrover.adapter;

import static java.security.AccessController.getContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mrrover.R;
import com.example.mrrover.model.DriverModel;
import com.example.mrrover.model.UserBookingModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserBookingAdapter extends RecyclerView.Adapter<UserBookingAdapter.UserBookingViewHolder>{

    private List<UserBookingModel> bookingList;

    private onBookListener onBookListener;


    public UserBookingAdapter(List<UserBookingModel> bookingList) {
        this.bookingList = bookingList;
    }

    public interface onBookListener {
        void onBook(int position);
    }

    public  void  setOnBookListener (onBookListener listener){
        this.onBookListener =  listener;
    }

    @NonNull
    @Override
    public UserBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mr_user_history, parent,false);
        return  new UserBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserBookingAdapter.UserBookingViewHolder holder, int position) {
        UserBookingModel information = bookingList.get(position);
        holder.bind(information);

    }
    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class UserBookingViewHolder  extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView service;
        private final TextView vehicle;
        private final TextView date;
        private final TextView time;
        private final TextView status;
        private  final Button cancel;
        private final ImageView profile;

        //private final ImageView profile;
        public UserBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.name12345);
            service = itemView.findViewById(R.id.service);
            vehicle  = itemView.findViewById(R.id.type);
            date  = itemView.findViewById(R.id.date);
            time  = itemView.findViewById(R.id.time);
            status  = itemView.findViewById(R.id.status);
            cancel  = itemView.findViewById(R.id.choose_button);
            profile  = itemView.findViewById(R.id.profile_image);

            cancel.setOnClickListener(  V->{
                int position = getAdapterPosition();

                if (position !=  RecyclerView.NO_POSITION && onBookListener !=null){
                    onBookListener.onBook(position);
                }

            });
        }
        public void bind(UserBookingModel  information){
            name.setText(information.getDriverName());
            service.setText(information.getService());
            vehicle.setText(information.getVehicle());
            date.setText(information.getDdate());
            time.setText(information.getTtime());
            status.setText(information.getUstatus());

            Glide.with(itemView.getContext())
                    .load(R.drawable.logodriver)
                    .placeholder(R.drawable.profile_icon)  // Provide a placeholder image
                    .error(R.drawable.profile_icon)  // Show this if loading fails
                    .into(profile);

        }
    }

}
