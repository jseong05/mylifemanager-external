package com.mlm.external;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AiMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<AiMessage> messageList;

    public AiMessageAdapter(List<AiMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        AiMessage msg = messageList.get(position);
        if ("user".equals(msg.getSender())) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_AI;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AiMessage msg = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).textView.setText("나: " + msg.getText());
            ((UserViewHolder) holder).textView.setTextColor(0xFF1A73E8);
        } else if (holder instanceof AiViewHolder) {
            ((AiViewHolder) holder).textView.setText("AI: " + msg.getText());
            ((AiViewHolder) holder).textView.setTextColor(0xFF2E7D32);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateData(List<AiMessage> newList) {
        this.messageList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        AiViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
