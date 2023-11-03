package com.comp90018.uninooks.models.busy_rating;

import androidx.annotation.NonNull;

import java.sql.Time;

public class BusyRating {

    @NonNull
    int busyRatingId;

    Integer libraryId;

    Integer restaurantId;

    Integer studySpaceId;

    Integer gymId;

    Integer date;

    Time time;

    Integer totalScore;

    Double averageScore;

    Integer count;

    public int getBusyRatingId() {
        return busyRatingId;
    }

    public void setBusyRatingId(int busyRatingId) {
        this.busyRatingId = busyRatingId;
    }

    public Integer getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Integer libraryId) {
        this.libraryId = libraryId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getStudySpaceId() {
        return studySpaceId;
    }

    public void setStudySpaceId(Integer studySpaceId) {
        this.studySpaceId = studySpaceId;
    }

    public Integer getGymId() {
        return gymId;
    }

    public void setGymId(Integer gymId) {
        this.gymId = gymId;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
