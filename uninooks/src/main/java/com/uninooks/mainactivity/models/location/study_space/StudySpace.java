package com.example.mainactivity.models.location.study_space;

import com.example.mainactivity.models.location.Location;

public class StudySpace extends Location {

    private int libraryId;

    private Integer minimumAccessAQFLevel;

    private boolean isTalkAllowed;

    private Integer floorLevel;

    private Integer capacity;

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

    public Integer getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(Integer floorLevel) {
        this.floorLevel = floorLevel;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
