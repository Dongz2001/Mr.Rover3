package com.example.mrrover.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mrrover.R;
import com.example.mrrover.model.DriverHistoryModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookAcceptedAdapter extends RecyclerView.Adapter<BookAcceptedAdapter.DriverHistoryViewHolder>{

    private List<DriverHistoryModel> bookingList;

    private onBookListener onBookListener;

    private UserBookingAcceptedAdapter.onChatListener onChatListener;


    public BookAcceptedAdapter(List<DriverHistoryModel> bookingList) {
        this.bookingList = bookingList;
    }

    public interface onBookListener {
        void onBook(int position);
    }

    public interface onChatListener {
        void onChat(int position);
    }

    public  void  setOnBookListener (onBookListener listener){
        this.onBookListener =  listener;
    }

    public  void  setOnChatListener (UserBookingAcceptedAdapter.onChatListener listener1){
        this.onChatListener =  listener1;
    }

    @NonNull
    @Override
    public DriverHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookingaccepted, parent,false);
        return  new DriverHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAcceptedAdapter.DriverHistoryViewHolder holder, int position) {
        DriverHistoryModel information = bookingList.get(position);
        holder.bind(information);



    }
    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class DriverHistoryViewHolder  extends RecyclerView.ViewHolder {

        private final TextView name;
        //private final TextView service;
        //private final TextView vehicle;
        private final TextView date;
        private final TextView time;
        private final ImageView chat;
        //private final TextView status;
        private  final LinearLayout book;
        private final ImageView profile;

        //private final ImageView profile;
        public DriverHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.tv_name);
            //service = itemView.findViewById(R.id.service12345);
            //vehicle  = itemView.findViewById(R.id.type12345);
            date  = itemView.findViewById(R.id.tv_date);
            time  = itemView.findViewById(R.id.tv_time);
            chat  = itemView.findViewById(R.id.chatjpeg);
            //status  = itemView.findViewById(R.id.STA123);
            book  = itemView.findViewById(R.id.confirmbooking555);
            profile  = itemView.findViewById(R.id.profile_image);

            book.setOnClickListener(  V->{
                int position = getAdapterPosition();

                if (position !=  RecyclerView.NO_POSITION && onBookListener !=null){
                    onBookListener.onBook(position);
                }

            });

            chat.setOnClickListener(  V->{
                int position = getAdapterPosition();

                if (position !=  RecyclerView.NO_POSITION && onBookListener !=null){
                    onChatListener.onChat(position);
                }

            });
        }
        public void bind(DriverHistoryModel  information){

            FirebaseFirestore database = FirebaseFirestore.getInstance();

            name.setText(information.getDriverName());
            //service.setText(information.getService());
            //vehicle.setText(information.getVehicle());
            date.setText(information.getDdate());
            time.setText(information.getTtime());
            //status.setText(information.getUstatus());

            if ("In-Progress".equals(information.getUstatus())) {
                //status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1448f8")));
            }

            Glide.with(itemView.getContext())
                    .load(R.drawable.logodriver)
                    .placeholder(R.drawable.profile_icon)  // Provide a placeholder image
                    .error(R.drawable.profile_icon)  // Show this if loading fails
                    .into(profile);

        }
    }

}
