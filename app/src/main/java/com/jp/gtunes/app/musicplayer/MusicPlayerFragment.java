package com.jp.gtunes.app.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jp.gtunes.R;
import com.jp.gtunes.core.fragment.BaseParamFragment;
import com.jp.gtunes.domain.GoogleFile;
import com.jp.gtunes.utils.PreferenceUtils;

import java.util.List;

public class MusicPlayerFragment extends BaseParamFragment<MusicPlayerParam> implements View.OnClickListener {
    private MediaPlayer mMediaPlayer;
    private String mAccessToken;
    private int mCurrentSongPosition;
    private List<GoogleFile> mSongList;

    private Button mBtnPlay, mBtnPause;

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_music_player;
    }

    @Override
    protected void bindView(View rootView) {
        mBtnPlay = (Button) rootView.findViewById(R.id.btn_play);
        mBtnPause = (Button) rootView.findViewById(R.id.btn_pause);

        rootView.findViewById(R.id.btn_next_song).setOnClickListener(this);
        rootView.findViewById(R.id.btn_previous_song).setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
    }

    @Override
    protected void loadParam(MusicPlayerParam param) {
        if (param != null) {
            mSongList = param.getFiles();
            mCurrentSongPosition = param.getSelectedFileIndex();
        }
    }

    @Override
    protected void loadData() {
        mAccessToken = (String) PreferenceUtils.getValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_song:
                if (mCurrentSongPosition < mSongList.size() - 1) {
                    mCurrentSongPosition++;
                    play();
                }
                break;
            case R.id.btn_previous_song:
                if (mCurrentSongPosition > 0) {
                    mCurrentSongPosition--;
                    play();
                }
                break;
            case R.id.btn_play:
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                } else {
                    play();
                }
                mBtnPause.setVisibility(View.VISIBLE);
                mBtnPlay.setVisibility(View.GONE);
                break;
            case R.id.btn_pause:
                mMediaPlayer.pause();
                mBtnPause.setVisibility(View.GONE);
                mBtnPlay.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        killMediaPlayer();
    }

    private void play() {
        String url = mSongList.get(mCurrentSongPosition).getUrl();
        try {
            killMediaPlayer();

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d("PLAYER", String.valueOf(percent));
                }
            });

            String play_url = String.format("%s&access_token=%s", url, mAccessToken);
            mMediaPlayer.setDataSource(play_url);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Can not play this file", Toast.LENGTH_SHORT).show();
        }
    }

    private void killMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
