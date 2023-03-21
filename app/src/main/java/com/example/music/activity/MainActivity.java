package com.example.music.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.music.R;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.ActivityMainBinding;
import com.example.music.fragment.AllSongsFragment;
import com.example.music.fragment.ContactFragment;
import com.example.music.fragment.FeaturedSongsFragment;
import com.example.music.fragment.FeedbackFragment;
import com.example.music.fragment.HomeFragment;
import com.example.music.fragment.NewSongsFragment;
import com.example.music.fragment.PopularSongsFragment;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.example.music.utils.GlideUtils;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_HOME = 1;
    public static final int TYPE_ALL_SONGS = 2;
    public static final int TYPE_FEATURED_SONGS = 3;
    public static final int TYPE_POPULAR_SONGS = 4;
    public static final int TYPE_NEW_SONGS = 5;
    public static final int TYPE_FEEDBACK = 6;
    public static final int TYPE_CONTACT = 7;

    private int mTypeScreen = TYPE_HOME;
    private ActivityMainBinding mActivityMainBinding;
    private int mAction;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constant.CHANGE_LISTENER));
        openHomeScreen();
        initListener();
        displayLayoutBottom();
    }

    private void initToolbar(String title) {
        mActivityMainBinding.header.imgLeft.setImageResource(R.drawable.ic_menu_left);
        mActivityMainBinding.header.tvTitle.setText(title);
    }

    private void initListener() {
        mActivityMainBinding.header.imgLeft.setOnClickListener(this);
        mActivityMainBinding.header.layoutPlayAll.setOnClickListener(this);

        mActivityMainBinding.menuLeft.layoutClose.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuHome.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuAllSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuFeaturedSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuPopularSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuNewSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuFeedback.setOnClickListener(this);
        mActivityMainBinding.menuLeft.tvMenuContact.setOnClickListener(this);

        mActivityMainBinding.layoutBottom.imgPrevious.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgPlay.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgNext.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgClose.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.layoutText.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgSong.setOnClickListener(this);
    }

    private void openHomeScreen() {
        replaceFragment(new HomeFragment());
        mTypeScreen = TYPE_HOME;
        initToolbar(getString(R.string.app_name));
        displayLayoutPlayAll();
    }

    public void openPopularSongsScreen() {
        replaceFragment(new PopularSongsFragment());
        mTypeScreen = TYPE_POPULAR_SONGS;
        initToolbar(getString(R.string.menu_popular_songs));
        displayLayoutPlayAll();
    }

    public void openNewSongsScreen() {
        replaceFragment(new NewSongsFragment());
        mTypeScreen = TYPE_NEW_SONGS;
        initToolbar(getString(R.string.menu_new_songs));
        displayLayoutPlayAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.img_left:
                mActivityMainBinding.drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.tv_menu_home:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                openHomeScreen();
                break;

            case R.id.tv_menu_all_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                replaceFragment(new AllSongsFragment());
                mTypeScreen = TYPE_ALL_SONGS;
                initToolbar(getString(R.string.menu_all_songs));
                displayLayoutPlayAll();
                break;

            case R.id.tv_menu_featured_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                replaceFragment(new FeaturedSongsFragment());
                mTypeScreen = TYPE_FEATURED_SONGS;
                initToolbar(getString(R.string.menu_featured_songs));
                displayLayoutPlayAll();
                break;

            case R.id.tv_menu_popular_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                openPopularSongsScreen();
                break;

            case R.id.tv_menu_new_songs:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                openNewSongsScreen();
                break;

            case R.id.tv_menu_feedback:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                replaceFragment(new FeedbackFragment());
                mTypeScreen = TYPE_FEEDBACK;
                initToolbar(getString(R.string.menu_feedback));
                displayLayoutPlayAll();
                break;

            case R.id.tv_menu_contact:
                mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                replaceFragment(new ContactFragment());
                mTypeScreen = TYPE_CONTACT;
                initToolbar(getString(R.string.menu_contact));
                displayLayoutPlayAll();
                break;

            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;

            case R.id.img_close:
                clickOnCloseButton();
                break;

            case R.id.layout_text:
            case R.id.img_song:
                openPlayMusicActivity();
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    private void displayLayoutPlayAll() {
        switch (mTypeScreen) {
            case TYPE_ALL_SONGS:
            case TYPE_FEATURED_SONGS:
            case TYPE_POPULAR_SONGS:
            case TYPE_NEW_SONGS:
                mActivityMainBinding.header.layoutPlayAll.setVisibility(View.VISIBLE);
                break;

            default:
                mActivityMainBinding.header.layoutPlayAll.setVisibility(View.GONE);
                break;
        }
    }

    private void displayLayoutBottom() {
        if (MusicService.mPlayer == null) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mActivityMainBinding.layoutBottom.tvSongName.setText(currentSong.getTitle());
        mActivityMainBinding.layoutBottom.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mActivityMainBinding.layoutBottom.imgSong);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    private void clickOnPrevButton() {
        GlobalFuntion.startMusicService(this, Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFuntion.startMusicService(this, Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(this, Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(this, Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void clickOnCloseButton() {
        GlobalFuntion.startMusicService(this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition);
    }

    private void openPlayMusicActivity() {
        GlobalFuntion.startActivity(this, PlayMusicActivity.class);
    }

    public ActivityMainBinding getActivityMainBinding() {
        return mActivityMainBinding;
    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
}
