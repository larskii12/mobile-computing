package com.comp90018.uninooks.service.busy_rating;

import com.comp90018.uninooks.models.busy_rating.BusyRating;
import com.comp90018.uninooks.models.review.ReviewType;

public interface BusyRatingService {

    BusyRating getBusyRating(int entityId, ReviewType type) throws Exception;

    Integer getAverageScoreFromEntity(int entityId, ReviewType type) throws Exception;
}
