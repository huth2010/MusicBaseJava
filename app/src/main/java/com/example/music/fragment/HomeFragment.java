package com.example.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.activity.PlayMusicActivity;
import com.example.music.adapter.BannerSongAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.adapter.SongGridAdapter;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentHomeBinding;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.example.music.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Song> mListSong;
    private List<Song> mListSongBanner;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListSongBanner == null || mListSongBanner.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListSongBanner.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        getListSongFromFirebase("");
        initListener();

        return mFragmentHomeBinding.getRoot();
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListSong != null) mListSong.clear();
                    getListSongFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchSong());

        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        mFragmentHomeBinding.layoutViewAllPopular.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openPopularSongsScreen();
            }
        });

        mFragmentHomeBinding.layoutViewAllNewSongs.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openNewSongsScreen();
            }
        });
    }

    private void getListSongFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }

                    if (StringUtil.isEmpty(key)) {
                        mListSong.add(0, song);
                    } else {
                        if (GlobalFuntion.getTextSearch(song.getTitle()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                            mListSong.add(0, song);
                        }
                    }
                }
                displayListBannerSongs();
                displayListPopularSongs();
                displayListNewSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListBannerSongs() {
        BannerSongAdapter bannerSongAdapter = new BannerSongAdapter(getListBannerSongs(), this::goToSongDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(bannerSongAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private List<Song> getListBannerSongs() {
        if (mListSongBanner != null) {
            mListSongBanner.clear();
        } else {
            mListSongBanner = new ArrayList<>();
        }
        if (mListSong == null || mListSong.isEmpty()) {
            return mListSongBanner;
        }
        for (Song song : mListSong) {
            if (song.isFeatured() && mListSongBanner.size() < Constant.MAX_COUNT_BANNER) {
                mListSongBanner.add(song);
            }
        }
        return mListSongBanner;
    }

    private void displayListPopularSongs() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvPopularSongs.setLayoutManager(gridLayoutManager);

        SongGridAdapter songGridAdapter = new SongGridAdapter(getListPopularSongs(), this::goToSongDetail);
        mFragmentHomeBinding.rcvPopularSongs.setAdapter(songGridAdapter);
    }

    private List<Song> getListPopularSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        List<Song> allSongs = new ArrayList<>(mListSong);
        Collections.sort(allSongs, (song1, song2) -> song2.getCount() - song1.getCount());
        for (Song song : allSongs) {
            if (list.size() < Constant.MAX_COUNT_POPULAR) {
                list.add(song);
            }
        }
        return list;
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentHomeBinding.rcvNewSongs.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(getListNewSongs(), this::goToSongDetail);
        mFragmentHomeBinding.rcvNewSongs.setAdapter(songAdapter);
    }

    private List<Song> getListNewSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        for (Song song : mListSong) {
            if (song.isLatest() && list.size() < Constant.MAX_COUNT_LATEST) {
                list.add(song);
            }
        }
        return list;
    }

    private void searchSong() {
        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        if (mListSong != null) mListSong.clear();
        getListSongFromFirebase(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }
}
