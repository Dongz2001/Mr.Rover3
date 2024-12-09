package com.example.mrrover.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrrover.R;
import com.example.mrrover.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class adapter extends FirestoreRecyclerAdapter<UserModel, adapter.UserModelViewHolder> {

    Context context;


    public adapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {

        holder.firstName.setText(model.getFirstname());

    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }


    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView firstName;
        TextView phone;
        ImageView profile;




        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.fname);
            //phone = itemView.findViewById(R.id.)
            profile = itemView.findViewById(R.id.profile);

        }
    }
}
