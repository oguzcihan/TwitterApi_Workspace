package com.example.twitterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater inflater;
    List<Tweet> tweets;
    public Adapter(Context ctx,List<Tweet>tweets){
        this.inflater=LayoutInflater.from(ctx);
        this.tweets=tweets;
    }
    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.custom_list_layout,parent,false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        holder.tweetId.setText(tweets.get(position).getId());
        holder.tweetText.setText(tweets.get(position).getTweetText());
    }
    @Override
    public int getItemCount() {
        return tweets.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tweetId,tweetText;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tweetId=itemView.findViewById(R.id.tweetId);
            tweetText=itemView.findViewById(R.id.tweetText);

        }
    }
}
