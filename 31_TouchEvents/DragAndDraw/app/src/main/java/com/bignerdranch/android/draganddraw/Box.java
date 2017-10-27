package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class Box implements Parcelable{
    private PointF mOrigin;
    private PointF mCurrent;
    private int mAngle;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        mAngle = angle;
    }

    public PointF getCenter() {
        float middleX = (mCurrent.x + mOrigin.x) / 2.0f;
        float middleY = (mCurrent.y + mOrigin.y) / 2.0f;
        return new PointF(middleX, middleY);
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mOrigin.writeToParcel(dest, flags);
        mCurrent.writeToParcel(dest, flags);
    }

    // This is used for creating new box from Parcel object in createFromParcel function below
    private Box (Parcel in) {
        mOrigin.readFromParcel(in);
        mCurrent.readFromParcel(in);
    }

    public static final Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {
        /**
         * Return a new box from the data in the specified parcel.
         */
        public Box createFromParcel(Parcel in) {
            Box b = new Box(in);
            return b;
        }

        /**
         * Return an array of boxes of the specified size.
         */
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };
}
