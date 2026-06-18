package com.example.russiancalendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.russiancalendar.R;
import com.example.russiancalendar.models.Event;

import java.util.List;
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Context context;
    private List<Event> events;
    private OnEventLongClickListener longClickListener;

    // Icons cycle
    private final int[] icons = {
            R.drawable.ic_heart,
            R.drawable.ic_heart,
            R.drawable.ic_heart,
            R.drawable.ic_heart
    };

    public interface OnEventLongClickListener {
        void onLongClick(Event event, int position);
    }

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    public void setOnEventLongClickListener(OnEventLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvTime.setText(event.getTime());
        holder.tvName.setText(event.getName());
        holder.ivIcon.setImageResource(icons[position % icons.length]);

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(event, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTime, tvName;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvName = itemView.findViewById(R.id.tvEventName);
        }
    }
}
