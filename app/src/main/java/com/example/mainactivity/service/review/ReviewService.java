package com.example.mainactivity.service.review;

import com.example.mainactivity.models.review.Review;
import com.example.mainactivity.models.review.ReviewType;

import java.util.List;

public interface ReviewService {

    Review addReview(Integer userId, Integer entityId, ReviewType type, Integer score) throws Exception;

    Review getReview(int ratingId, ReviewType type) throws Exception;

    List<Review> getReviewsByUser(int userId, Integer entityId, ReviewType type) throws Exception;

    List<Review> getReviewsByEntity(Integer entityId, ReviewType type) throws Exception;

}
