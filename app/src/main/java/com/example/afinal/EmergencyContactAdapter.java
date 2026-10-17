package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder> {

    private final List<EmergencyContact> contactsList;
    private final Context context;

    public EmergencyContactAdapter(Context context, List<EmergencyContact> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        EmergencyContact contact = contactsList.get(position);
        holder.tvServiceName.setText(contact.getServiceName());
        holder.tvPhoneNumber.setText(contact.getPhoneNumber());

        // Set appropriate icon based on service type
        holder.ivServiceIcon.setImageResource(getServiceIcon(contact.getServiceName()));

        // Set background color based on priority
        if (contact.isPriority()) {
            holder.itemView.setBackgroundResource(R.color.colorPriorityBackground);
        }

        // Set click listeners
        holder.btnCall.setOnClickListener(v -> makeCall(contact.getPhoneNumber()));
        holder.itemView.setOnClickListener(v -> makeCall(contact.getPhoneNumber()));
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    private int getServiceIcon(String serviceName) {
        // Return appropriate icon based on service name
        switch (serviceName.toLowerCase()) {
            case "police":
                return R.drawable.ic_police;
            case "ambulance":
                return R.drawable.ic_ambulance;
            case "fire brigade":
                return R.drawable.ic_fire;
            case "national emergency number":
                return R.drawable.ic_emergency;
            case "disaster management":
                return R.drawable.ic_disaster;
            case "women helpline":
                return R.drawable.ic_women_helpline;
            case "child helpline":
                return R.drawable.ic_child_helpline;
            case "senior citizen helpline":
                return R.drawable.ic_senior_citizen;
            case "traffic helpline":
                return R.drawable.ic_traffic;
            case "railway enquiry":
                return R.drawable.ic_railway;
            case "cyber crime helpline":
                return R.drawable.ic_cyber_crime;
            case "tourist helpline":
                return R.drawable.ic_tourist;
            case "anti-poison helpline":
                return R.drawable.ic_poison;
            case "gas leakage (lpg)":
                return R.drawable.ic_gas_leakage;
            case "electricity helpline":
                return R.drawable.ic_electricity;
            default:
                return R.drawable.ic_phone;
        }
    }

    private void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(callIntent);
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView ivServiceIcon;
        TextView tvServiceName;
        TextView tvPhoneNumber;
        MaterialButton btnCall;

        ContactViewHolder(View itemView) {
            super(itemView);
            ivServiceIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}