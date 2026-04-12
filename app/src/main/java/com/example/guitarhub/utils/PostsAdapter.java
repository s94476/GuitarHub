package com.example.guitarhub.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarhub.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> allPosts = new ArrayList<>();
    private List<Post> filteredPosts = new ArrayList<>();
    private Set<String> favoritePostIds = new HashSet<>();
    private OnFavoriteClickListener onFavoriteClickListener;
    private boolean showingFavoritesOnly = false;
    private String currentQuery = "";

    public void setPosts(List<Post> posts) {
        this.allPosts = new ArrayList<>(posts);
        applyFilter();
    }

    public void setFavoritePostIds(List<String> favoritePostIds) {
        this.favoritePostIds.clear();
        if (favoritePostIds != null) {
            this.favoritePostIds.addAll(favoritePostIds);
        }
        applyFilter();
    }

    public void setShowingFavoritesOnly(boolean favoritesOnly) {
        this.showingFavoritesOnly = favoritesOnly;
        applyFilter();
    }

    public void filter(String query) {
        this.currentQuery = query != null ? query.toLowerCase() : "";
        applyFilter();
    }

    private void applyFilter() {
        filteredPosts = allPosts.stream()
                .filter(post -> {
                    // Filter by favorites if active
                    if (showingFavoritesOnly && !favoritePostIds.contains(post.getPostId())) {
                        return false;
                    }
                    
                    // Filter by search query
                    if (currentQuery.isEmpty()) {
                        return true;
                    }
                    
                    boolean matchSong = post.getSongTitle() != null && post.getSongTitle().toLowerCase().contains(currentQuery);
                    boolean matchArtist = post.getArtist() != null && post.getArtist().toLowerCase().contains(currentQuery);
                    boolean matchAmp = post.getAmpName() != null && post.getAmpName().toLowerCase().contains(currentQuery);
                    boolean matchUser = post.getOwnerNickname() != null && post.getOwnerNickname().toLowerCase().contains(currentQuery);
                    boolean matchGenre = post.getGenre() != null && post.getGenre().toLowerCase().contains(currentQuery);
                    
                    return matchSong || matchArtist || matchAmp || matchUser || matchGenre;
                })
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(String postId, boolean isFavorite);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = filteredPosts.get(position);

        holder.songTextView.setText(post.getSongTitle());
        holder.postedByTextView.setText("Posted by: " + post.getOwnerNickname());
        holder.artistTextView.setText(post.getArtist());
        holder.genreTextView.setText("Genre: " + (post.getGenre() != null ? post.getGenre() : "N/A"));
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

        boolean isFavorite = favoritePostIds.contains(post.getPostId());
        holder.favoriteIcon.setSelected(isFavorite);

        holder.favoriteIcon.setOnClickListener(v -> {
            if (onFavoriteClickListener != null && post.getPostId() != null) {
                boolean newFavoriteState = !isFavorite;
                onFavoriteClickListener.onFavoriteClick(post.getPostId(), newFavoriteState);

                if (newFavoriteState) {
                    favoritePostIds.add(post.getPostId());
                } else {
                    favoritePostIds.remove(post.getPostId());
                }
                
                // If we are showing only favorites, removing a favorite should remove it from the view
                if (showingFavoritesOnly && !newFavoriteState) {
                    applyFilter();
                } else {
                    notifyItemChanged(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredPosts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView songTextView;
        TextView postedByTextView;
        TextView artistTextView;
        TextView genreTextView;
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
        ImageView favoriteIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            songTextView = itemView.findViewById(R.id.tv_song_title);
            postedByTextView = itemView.findViewById(R.id.tv_posted_by);
            artistTextView = itemView.findViewById(R.id.tv_artist);
            genreTextView = itemView.findViewById(R.id.tv_genre);
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
            favoriteIcon = itemView.findViewById(R.id.iv_favorite);
        }
    }
}
