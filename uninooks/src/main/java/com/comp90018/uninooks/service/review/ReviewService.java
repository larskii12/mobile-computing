package com.comp90018.uninooks.service.review;

import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;

import java.util.List;

public interface ReviewService {

    Review addReview(Integer userId, Integer entityId, ReviewType type, Integer score, String comment) throws Exception;

    Review getReview(int ratingId, ReviewType type) throws Exception;

    List<Review> getReviewsByUser(int userId, Integer entityId, ReviewType type) throws Exception;

    List<Review> getReviewsByEntity(Integer entityId, ReviewType type) throws Exception;

}
