package com.example.afinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InstructionsPagerAdapter extends RecyclerView.Adapter<InstructionsPagerAdapter.PageViewHolder> {

    private final Context context;
    private final int[] layouts = {
            R.layout.step_layout_1,
            R.layout.step_layout_2,
            R.layout.step_layout_3,
            R.layout.step_layout_4
    };

    public InstructionsPagerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layouts[viewType], parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        // Add specific logic for each page if needed
        // For example, set up click listeners for interactive elements

        // Demo code for SOS button in step 3
        if (position == 2) {
            View sosButton = holder.itemView.findViewById(R.id.sosButtonDemo);
            if (sosButton != null) {
                sosButton.setOnClickListener(v -> {
                    // Show a toast or dialog explaining this is a demo
                    // Toast.makeText(context, "This is a demo. In the real app, press and hold for 3 seconds.", Toast.LENGTH_SHORT).show();
                });
            }
        }

        // Demo code for feature cards in step 4
        if (position == 3) {
            View locationCard = holder.itemView.findViewById(R.id.locationSharingCard);
            View contactsCard = holder.itemView.findViewById(R.id.emergencyContactsCard);
            View tipsCard = holder.itemView.findViewById(R.id.safetyTipsCard);

            if (locationCard != null) {
                locationCard.setOnClickListener(v -> {
                    // Show preview or information about this feature
                });
            }

            if (contactsCard != null) {
                contactsCard.setOnClickListener(v -> {
                    // Show preview or information about this feature
                });
            }

            if (tipsCard != null) {
                tipsCard.setOnClickListener(v -> {
                    // Show preview or information about this feature
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}