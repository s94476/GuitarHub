package com.example.guitarhub.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarhub.R;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> posts = new ArrayList<>();

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        // Use more specific change events if you can, but for simplicity, we\'ll use notifyDataSetChanged() here.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.songTextView.setText(post.getSongTitle());
        holder.postedByTextView.setText("Posted by: " + post.getOwnerNickname());
        holder.artistTextView.setText(post.getArtist());
        holder.recommendedLevelTextView.setText("Recommended Level: " + post.getRecommendedLevel());
        holder.gainTextView.setText("Gain: " + post.getGain().getIntensity() + "/10");
        holder.trebleTextView.setText("Treble: " + post.getTreble().getIntensity() + "/10");
        holder.bassTextView.setText("Bass: " + post.getBass().getIntensity() + "/10");
        holder.middleTextView.setText("Middle: " + post.getMiddle().getIntensity() + "/10");
        holder.ampNameTextView.setText("Amp: " + post.getAmpName());
        holder.ampPositionTextView.setText("Position: " + post.getAmpPosition());
        holder.toneTextView.setText("Tone: " + post.getTone() + "/10");

        // Clear previous effects
        holder.effect1.setVisibility(View.GONE);
        holder.effect2.setVisibility(View.GONE);
        holder.effect3.setVisibility(View.GONE);
        holder.effect4.setVisibility(View.GONE);

        List<Effect> effects = post.getEffects();
        if (effects != null && !effects.isEmpty()) {
            if (effects.size() > 0) {
                holder.effect1.setText(effects.get(0).getName() + ": " + effects.get(0).getIntensity() + "/10");
                holder.effect1.setVisibility(View.VISIBLE);
            }
            if (effects.size() > 1) {
                holder.effect2.setText(effects.get(1).getName() + ": " + effects.get(1).getIntensity() + "/10");
                holder.effect2.setVisibility(View.VISIBLE);
            }
            if (effects.size() > 2) {
                holder.effect3.setText(effects.get(2).getName() + ": " + effects.get(2).getIntensity() + "/10");
                holder.effect3.setVisibility(View.VISIBLE);
            }
            if (effects.size() > 3) {
                holder.effect4.setText(effects.get(3).getName() + ": " + effects.get(3).getIntensity() + "/10");
                holder.effect4.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView songTextView;
        TextView postedByTextView;
        TextView artistTextView;
        TextView recommendedLevelTextView;
        TextView gainTextView;
        TextView trebleTextView;
        TextView bassTextView;
        TextView middleTextView;
        TextView ampNameTextView;
        TextView ampPositionTextView;
        TextView toneTextView;
        TextView effect1;
        TextView effect2;
        TextView effect3;
        TextView effect4;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            songTextView = itemView.findViewById(R.id.tv_song_title);
            postedByTextView = itemView.findViewById(R.id.tv_posted_by);
            artistTextView = itemView.findViewById(R.id.tv_artist);
            recommendedLevelTextView = itemView.findViewById(R.id.tv_recommended_level);
            gainTextView = itemView.findViewById(R.id.tv_gain);
            trebleTextView = itemView.findViewById(R.id.tv_treble);
            bassTextView = itemView.findViewById(R.id.tv_bass);
            middleTextView = itemView.findViewById(R.id.tv_middle);
            ampNameTextView = itemView.findViewById(R.id.tv_amp_name);
            ampPositionTextView = itemView.findViewById(R.id.tv_amp_position);
            toneTextView = itemView.findViewById(R.id.tv_tone);
            effect1 = itemView.findViewById(R.id.tv_effect1);
            effect2 = itemView.findViewById(R.id.tv_effect2);
            effect3 = itemView.findViewById(R.id.tv_effect3);
            effect4 = itemView.findViewById(R.id.tv_effect4);
        }
    }
}
