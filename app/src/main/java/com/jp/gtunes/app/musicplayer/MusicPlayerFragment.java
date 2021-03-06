package com.jp.gtunes.app.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jp.gtunes.R;
import com.jp.gtunes.app.base.GtunesFragmentActivity;
import com.jp.gtunes.core.fragment.BaseParamFragment;
import com.jp.gtunes.domain.GoogleFile;
import com.jp.gtunes.utils.PreferenceUtils;

import java.util.List;

public class MusicPlayerFragment extends BaseParamFragment<MusicPlayerParam> implements View.OnClickListener {
    private MediaPlayer mMediaPlayer;
    private String mAccessToken;
    private int mCurrentSongPosition;
    private boolean mPlaybackEnabled = false;
    private List<GoogleFile> mSongList;

    private ImageButton mBtnPlay, mBtnPause, mBtnPlayback, mBtnNext, mBtnPrevious;
    private TextView mTextMusicTitle;
    private TextView mTextCurrentTime, mTextTotalDuration;

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_music_player;
    }

    @Override
    protected void bindView(View rootView) {
        mBtnPlay = (ImageButton) rootView.findViewById(R.id.btn_play);
        mBtnPause = (ImageButton) rootView.findViewById(R.id.btn_pause);
        mBtnPlayback = (ImageButton) rootView.findViewById(R.id.btn_playback);
        mBtnNext = (ImageButton) rootView.findViewById(R.id.btn_next_song);
        mBtnPrevious = (ImageButton) rootView.findViewById(R.id.btn_previous_song);

        mTextMusicTitle = (TextView) rootView.findViewById(R.id.music_title_text);
        mTextCurrentTime = (TextView) rootView.findViewById(R.id.music_current_time_text);
        mTextTotalDuration = (TextView) rootView.findViewById(R.id.music_total_duration_text);

        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnPlayback.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnPrevious.setOnClickListener(this);
    }

    @Override
    protected void loadParam(MusicPlayerParam param) {
        if (param != null) {
            mSongList = param.getFiles();
            mCurrentSongPosition = param.getSelectedFileIndex();

            mBtnPause.setVisibility(View.VISIBLE);
            mBtnPlay.setVisibility(View.GONE);
            play();
        }
    }

    @Override
    protected void loadData() {
        mAccessToken = (String) PreferenceUtils.getValue(getActivity(), "access_token", "", PreferenceUtils.PREFERENCE_TYPE_STRING);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GtunesFragmentActivity) getActivity()).updateScreenTitle("");
        ((GtunesFragmentActivity) getActivity()).setButtonBackEnabled(true);
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
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);
                } else {
                    play();
                }
                break;
            case R.id.btn_pause:
                mMediaPlayer.pause();
                mBtnPause.setVisibility(View.GONE);
                mBtnPlay.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_playback:
                mPlaybackEnabled = !mPlaybackEnabled;
                if (mPlaybackEnabled) {
                    mBtnPlayback.setBackgroundResource(R.drawable.bg_pink_circle);
                } else {
                    mBtnPlayback.setBackgroundResource(R.drawable.bg_gray_circle);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void play() {
        mTextMusicTitle.setText(mSongList.get(mCurrentSongPosition).getFileNameWithoutExtension());
        if (mCurrentSongPosition <= 0) {
            mBtnPrevious.setBackgroundResource(R.drawable.bg_gray_circle);
            mBtnNext.setBackgroundResource(R.drawable.bg_green_circle);
        } else if (mCurrentSongPosition >= mSongList.size() - 1) {
            mBtnNext.setBackgroundResource(R.drawable.bg_gray_circle);
            mBtnPrevious.setBackgroundResource(R.drawable.bg_green_circle);
        } else {
            mBtnNext.setBackgroundResource(R.drawable.bg_green_circle);
            mBtnPrevious.setBackgroundResource(R.drawable.bg_green_circle);
        }

        String url = mSongList.get(mCurrentSongPosition).getUrl();
        try {
            killMediaPlayer();

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);

                    if (mp.getDuration() > 0) {
                        int minute = mp.getDuration() / (60 * 1000);
                        int second = (mp.getDuration() / 1000) % 60;
                        mTextTotalDuration.setText(String.format("%02d:%02d", minute, second));
                    } else {
                        mTextTotalDuration.setText("--:--");
                    }
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
                    int minute = mp.getCurrentPosition() / (60 * 1000);
                    int second = (mp.getCurrentPosition() / 1000) % 60;
                    mTextCurrentTime.setText(String.format("%02d:%02d", minute, second));
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mBtnPlay.setVisibility(View.VISIBLE);
                    mBtnPause.setVisibility(View.GONE);

                    if (mCurrentSongPosition < mSongList.size() - 1) {
                        mCurrentSongPosition++;
                        play();
                    } else if (mPlaybackEnabled) {
                        mCurrentSongPosition = 0;
                        play();
                    }
                }
            });

            String play_url = String.format("%s&access_token=%s", url, mAccessToken);
            mMediaPlayer.setDataSource(play_url);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
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
