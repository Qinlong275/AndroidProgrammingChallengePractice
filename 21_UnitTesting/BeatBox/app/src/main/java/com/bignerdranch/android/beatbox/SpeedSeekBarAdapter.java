package com.bignerdranch.android.beatbox;

import android.databinding.BaseObservable;
import android.widget.SeekBar;

/**
 * Created by 秦龙 on 2017/9/20.
 */

public class SpeedSeekBarAdapter extends BaseObservable{

    private BeatBox mBeatBox;

    private int mMinSeekBar;
    private int mMaxSeekBar;
    private int mRangeSeekBar;
    private float mMinSpeed;
    private float mMaxSpeed;
    private float mRangeSpeed;

    public SpeedSeekBarAdapter(BeatBox beatBox, SeekBar seekBar) {
        mBeatBox = beatBox;
        mMinSeekBar = 0;
        mMaxSeekBar = seekBar.getMax();
        mRangeSeekBar = mMaxSeekBar - mMinSeekBar;
        mMinSpeed = BeatBox.MIN_PLAYBACK_SPEED;
        mMaxSpeed = BeatBox.MAX_PLAYBACK_SPEED;
        mRangeSpeed = mMaxSpeed - mMinSpeed;
    }

    public float getSpeed() {
        return mBeatBox.getPlaybackSpeed();
    }

    public int getSeekBarValue() {
        int seekBarValue = Math.round((mBeatBox.getPlaybackSpeed() - mMinSpeed) / mRangeSpeed * mRangeSeekBar + mMinSeekBar);

        if (seekBarValue > mMaxSeekBar) {
            return mMaxSeekBar;
        } else if (seekBarValue < mMinSeekBar) {
            return mMinSeekBar;
        } else {
            return seekBarValue;
        }
    }

    public void setSpeed(int seekBarValue) {
        float speed = ((float) seekBarValue - mMinSeekBar) / mRangeSeekBar * mRangeSpeed + mMinSpeed;
        mBeatBox.setPlaybackSpeed(speed);
        notifyChange();
    }

    public void changeSpeed(SeekBar seekBar, int progress, boolean fromUser) {
        setSpeed(progress);
    }

}
