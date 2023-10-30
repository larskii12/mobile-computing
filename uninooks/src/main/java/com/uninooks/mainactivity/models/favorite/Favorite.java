package com.example.mainactivity.models.favorite;

public class Favorite {

    private int id;

    private int userId;

    private Integer libraryId;

    private Integer studySpaceId;

    private Integer restaurantId;

    private Integer gymId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Integer libraryId) {
        this.libraryId = libraryId;
    }

    public Integer getStudySpaceId() {
        return studySpaceId;
    }

    public void setStudySpaceId(Integer studySpaceId) {
        this.studySpaceId = studySpaceId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getGymId() {
        return gymId;
    }

    public void setGymId(Integer gymId) {
        this.gymId = gymId;
    }
}
