package com.comp90018.uninooks.models.location.study_space;

import static java.util.Objects.nonNull;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.comp90018.uninooks.models.location.Location;

public class StudySpace extends Location {

    public static final Creator<StudySpace> CREATOR = new Creator<StudySpace>() {
        @Override
        public StudySpace createFromParcel(Parcel in) {
            return new StudySpace(in);
        }

        @Override
        public StudySpace[] newArray(int size) {
            return new StudySpace[size];
        }
    };
    private int libraryId;
    private Integer minimumAccessAQFLevel;
    private boolean isTalkAllowed;
    private Integer floorLevel;
    private Integer capacity;
    private double average_rating;
    private String type;

    public StudySpace() {
        super();
    }

    protected StudySpace(Parcel in) {
        super(in);

        libraryId = in.readInt();
        minimumAccessAQFLevel = in.readInt();
        type = in.readString();
        isTalkAllowed = in.readByte() != 0;
        floorLevel = in.readInt();
        capacity = in.readInt();
        average_rating = in.readDouble();
    }

    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    public Integer getMinimumAccessAQFLevel() {
        return minimumAccessAQFLevel;
    }

    public void setMinimumAccessAQFLevel(Integer minimumAccessAQFLevel) {
        this.minimumAccessAQFLevel = minimumAccessAQFLevel;
    }

    public boolean isTalkAllowed() {
        return isTalkAllowed;
    }

    public void setTalkAllowed(boolean talkAllowed) {
        isTalkAllowed = talkAllowed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public Integer getFloorLevel() {
//        return floorLevel;
//    }

//    public void setFloorLevel(Integer floorLevel) {
//        this.floorLevel = floorLevel;
//    }

//    public Integer getCapacity() {
//        return capacity;
//    }

    //    public void setCapacity(Integer capacity) {
//        this.capacity = capacity;
//    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(libraryId);

        if (nonNull(minimumAccessAQFLevel)) {
            dest.writeInt(minimumAccessAQFLevel);
        }

        dest.writeString(type);

        byte isTalkAllowedByte = (byte) (isTalkAllowed ? 1 : 0);
        dest.writeByte(isTalkAllowedByte);

        if (nonNull(floorLevel)) {
            dest.writeInt(floorLevel);
        }

        if (nonNull(capacity)) {
            dest.writeInt(capacity);
        }
    }
}
