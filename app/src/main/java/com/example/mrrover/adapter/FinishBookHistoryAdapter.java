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
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.DriverModel;

import java.util.List;

public class FinishBookHistoryAdapter extends RecyclerView.Adapter<FinishBookHistoryAdapter.DriverHistoryViewHolder>{

    private List<DriverHistoryModel> bookingList;

    private FinishBookHistoryAdapter.onBookListener onBookListener;



    public FinishBookHistoryAdapter(List<DriverHistoryModel> bookingList) {

        this.bookingList = bookingList;
    }

    public interface onBookListener {
        void onBook(int position);
    }
    public  void  setOnBookListener (FinishBookHistoryAdapter.onBookListener listener){
        this.onBookListener =  listener;
    }

    @NonNull
    @Override
    public DriverHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mr_driverhistory, parent, false);
        return  new DriverHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinishBookHistoryAdapter.DriverHistoryViewHolder holder, int position) {
        DriverHistoryModel information = bookingList.get(position);
        holder.bind(information);


    }


    public int getItemCount() {
        return bookingList.size();
    }

    public class DriverHistoryViewHolder  extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView rating;
        private final TextView comment;
        //private  final Button choose;
        /*private final TextView service;
        private final TextView vehicle;
        private final TextView date;
        private final TextView time;
        private final TextView status;
        private  final Button accept;
        private  final Button cancel;
        private final ImageView profile;*/

        //private final ImageView profile;
        public DriverHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.namevehicleowner);
            rating = itemView.findViewById(R.id.rating_value);
            comment = itemView.findViewById(R.id.comment);
            //choose = itemView.findViewById(R.id.history);
            /*service = itemView.findViewById(R.id.service12345);
            vehicle  = itemView.findViewById(R.id.type12345);
            date  = itemView.findViewById(R.id.date12345);
            time  = itemView.findViewById(R.id.time12345);
            status  = itemView.findViewById(R.id.status12345);
            accept  = itemView.findViewById(R.id.accept_button);
            cancel  = itemView.findViewById(R.id.cancel_button);
            profile  = itemView.findViewById(R.id.profile_image);*/

           /* choose.setOnClickListener(  V->{
                int position = getAdapterPosition();

                if (position !=  RecyclerView.NO_POSITION && onBookListener !=null){
                    onBookListener.onBook(position);
                }
            });*/

        }

        public void bind(DriverHistoryModel  information){
            name.setText(information.getDriverName());
            rating.setText(String.valueOf(information.getStar()));
            comment.setText(information.getComments());
            /*service.setText(information.getService());
            vehicle.setText(information.getVehicle());
            date.setText(information.getDdate());
            time.setText(information.getTtime());
            status.setText(information.getUstatus());

            Glide.with(itemView.getContext())
                    .load(R.drawable.logodriver)
                    .placeholder(R.drawable.profile_icon)  // Provide a placeholder image
                    .error(R.drawable.profile_icon)  // Show this if loading fails
                    .into(profile);*/



        }
    }
}
