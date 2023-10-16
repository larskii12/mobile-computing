package com.example.mainactivity.service.review;

import com.example.mainactivity.config.DatabaseHelper;
import com.example.mainactivity.models.review.Review;
import com.example.mainactivity.models.review.ReviewType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ReviewServiceImpl implements ReviewService {


    Connection connector = new DatabaseHelper().getConnector();

    /**
     * method to add a new review of an entity of a user into the database (To add review)
     *
     * @param userId     as the user id
     * @param entityId   as the entity id
     * @param type       as the review type
     * @param score      as the score
     * @throws Exception if any exceptions happens
     */
    public Review addReview(Integer userId, Integer entityId, ReviewType type, Integer score) throws Exception {

        java.sql.Time sqlTime = new java.sql.Time(System.currentTimeMillis());

        try {
            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_gym_id) VALUES (?, ?, ?, ?);";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_library_id) VALUES (?, ?, ?, ?);";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_restaurant_id) VALUES (?, ?, ?, ?);";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_study_space_id) VALUES (?, ?, ?, ?);";
                    break;
                default:
                    throw new Exception("Invalid review type. Review not created.");
            }
            PreparedStatement preparedStatement = connector.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, score);
            preparedStatement.setTime(3,  sqlTime);
            preparedStatement.setInt(4, entityId);

            // Execute query
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            int generatedKey = 0;
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }

            return getReview(generatedKey, type);
        }

        // If exception happens
        catch (Exception e) {

            // If user name has already been registered
            if (e.getMessage().contains("unique_user_username")) {
                throw new Exception("This username has been registered, please try another one.");
            }

            // If email has already been registered
            else if (e.getMessage().contains("unique_user_email")) {
                throw new Exception("This email has been registered, please try another one.");
            }

            // Unknown exceptions happens.
            throw new Exception("User added failed, please contact the IT administrator to report the issue.");
        }
    }

    /**
     * Get a user with specified email address
     *
     * @param reviewId, as the review id
     * @return Review
     */
    public Review getReview(int reviewId, ReviewType type) throws Exception {

        Review review = new Review();

        try {

            String query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, reviewId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                // Set review information
                review.setReviewId(resultSet.getInt("review_id"));
                review.setUserId(resultSet.getInt("review_user_id"));
                switch (type) {

                    case GYM: // Set gym id to the review
                        review.setGymId(resultSet.getInt("review_gym_id"));
                        break;
                    case LIBRARY: // Set library id to the review
                        review.setLibraryId(resultSet.getInt("review_library_id"));
                        break;
                    case RESTAURANT: // Set restaurant id to the review
                        review.setRestaurantId(resultSet.getInt("review_restaurant_id"));
                        break;
                    case STUDY_SPACE: // Set study space id to the review
                        review.setStudySpaceId(resultSet.getInt("review_study_space_id"));
                        break;
                    default:
                        throw new Exception("Invalid review type. Cannot retrieve review list.");
                }
                review.setScore(resultSet.getInt("review_score"));
                review.setTime(resultSet.getTime("review_time"));
                return review;
            }

        }

        // If exception happens when querying user
        catch (Exception e) {
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
        // Return user information
        return null;
    }

    /**
     * Get the list of reviews based by the person
     *
     * @param userId, as the user id
     * @return list of reviews
     */
    public List<Review> getReviewsByUser(int userId, Integer entityId, ReviewType type) throws Exception {

        List<Review> reviewList = new ArrayList<>();

        try {

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_gym_id\" IS NOT NULL";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_library_id\" = ?";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_restaurant_id\" = ?";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_study_space_id\" = ?";
                    break;
                default:
                    throw new Exception("Invalid review type. Cannot retrieve review list.");
            }


            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Iterate all the resulting rows from the query
                Review review = new Review();
                // Set review information
                review.setReviewId(resultSet.getInt("review_id"));
                review.setUserId(resultSet.getInt("review_user_id"));
                switch (type) {

                    case GYM: // Set gym id to the review
                        review.setGymId(resultSet.getInt("review_gym_id"));
                        break;
                    case LIBRARY: // Set library id to the review
                        review.setLibraryId(resultSet.getInt("review_library_id"));
                        break;
                    case RESTAURANT: // Set restaurant id to the review
                        review.setRestaurantId(resultSet.getInt("review_restaurant_id"));
                        break;
                    case STUDY_SPACE: // Set study space id to the review
                        review.setStudySpaceId(resultSet.getInt("review_study_space_id"));
                        break;
                    default:
                        throw new Exception("Invalid review type. Cannot retrieve review list.");
                }
                review.setScore(resultSet.getInt("review_score"));
                review.setTime(resultSet.getTime("review_time"));

                reviewList.add(review);
            }
            return reviewList;
        }

        // If exception happens when querying user
        catch (Exception e) {
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
    }

    /**
     * Get a user with specified email address
     *
     * @param userId, as the user id
     * @param entityId   as the entity id
     * @param type       as the review type
     * @return list of reviews
     */
    public List<Review> getReviewsByUserAndEntity(int userId, Integer entityId, ReviewType type) throws Exception {

        List<Review> reviewList = new ArrayList<>();

        try {

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_gym_id\" = ?";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_library_id\" = ?";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_restaurant_id\" = ?";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_study_space_id\" = ?";
                    break;
                default:
                    throw new Exception("Invalid review type. Cannot retrieve review list.");
            }


            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, entityId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Itereview all the resulting rows from the query
                Review review = new Review();

                // Set review information
                review.setReviewId(resultSet.getInt("review_id"));
                review.setUserId(resultSet.getInt("review_user_id"));
                switch (type) {

                    case GYM: // Set gym id to the review
                        review.setGymId(resultSet.getInt("review_gym_id"));
                        break;
                    case LIBRARY: // Set library id to the review
                        review.setLibraryId(resultSet.getInt("review_library_id"));
                        break;
                    case RESTAURANT: // Set restaurant id to the review
                        review.setRestaurantId(resultSet.getInt("review_restaurant_id"));
                        break;
                    case STUDY_SPACE: // Set study space id to the review
                        review.setStudySpaceId(resultSet.getInt("review_study_space_id"));
                        break;
                    default:
                        throw new Exception("Invalid review type. Cannot retrieve review list.");
                }
                review.setScore(resultSet.getInt("review_score"));
                review.setTime(resultSet.getTime("review_time"));

                reviewList.add(review); // add review to the list
            }
            return reviewList;
        }

        // If exception happens when querying user
        catch (Exception e) {
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
    }
}
