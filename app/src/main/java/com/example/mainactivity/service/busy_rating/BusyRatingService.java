package com.example.mainactivity.service.busy_rating;

import com.example.mainactivity.models.busy_rating.BusyRating;
import com.example.mainactivity.models.review.ReviewType;

public interface BusyRatingService {

    BusyRating getBusyRating(int entityId, ReviewType type) throws Exception;

    Integer getAverageScoreFromEntity(int entityId, ReviewType type) throws Exception;
}
