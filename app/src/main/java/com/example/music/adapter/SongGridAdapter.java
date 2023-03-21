package com.example.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.databinding.ItemSongGridBinding;
import com.example.music.listener.IOnClickSongItemListener;
import com.example.music.model.Song;
import com.example.music.utils.GlideUtils;

import java.util.List;

public class SongGridAdapter extends RecyclerView.Adapter<SongGridAdapter.SongGridViewHolder> {

    private final List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;

    public SongGridAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongGridBinding itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongGridViewHolder(itemSongGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongGridViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongGridBinding.imgSong);
        holder.mItemSongGridBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongGridBinding.tvArtist.setText(song.getArtist());

        holder.mItemSongGridBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class SongGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongGridBinding mItemSongGridBinding;

        public SongGridViewHolder(ItemSongGridBinding itemSongGridBinding) {
            super(itemSongGridBinding.getRoot());
            this.mItemSongGridBinding = itemSongGridBinding;
        }
    }
}
