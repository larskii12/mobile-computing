package com.comp90018.uninooks.service.review;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.review.Review;
import com.comp90018.uninooks.models.review.ReviewType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ReviewServiceImpl implements ReviewService {


    Connection connector = new DatabaseHelper().getConnector();

    /**
     * method to add a new review of an entity of a user into the database (To add review)
     *
     * @param userId   as the user id
     * @param entityId as the entity id
     * @param type     as the review type
     * @param score    as the score
     * @throws Exception if any exceptions happens
     */
    public Review addReview(Integer userId, Integer entityId, ReviewType type, Integer score, String comment) throws Exception {

        try {

            // Get current time
            java.sql.Timestamp currentDateTime = new java.sql.Timestamp((System.currentTimeMillis() / 1000) * 1000);

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_gym_id, comment) VALUES (?, ?, ?, ?, ?);";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_library_id, comment) VALUES (?, ?, ?, ?, ?);";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_restaurant_id, comment) VALUES (?, ?, ?, ?, ?);";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "INSERT INTO mobilecomputing.review (review_user_id, review_score, review_time, review_study_space_id, comment) VALUES (?, ?, ?, ?, ?);";
                    break;
                default:
                    throw new Exception("Invalid review type. Review not created.");
            }
            PreparedStatement preparedStatement = connector.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, score);
            preparedStatement.setTimestamp(3, currentDateTime);
            preparedStatement.setInt(4, entityId);
            preparedStatement.setString(5, comment);

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

            // Unknown exceptions happens.
            throw new Exception("User added failed, please contact the IT administrator to report the issue.");
        }

        finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }

    }

    /**
     * Get a user with specified email address
     *
     * @param reviewId, as the review id
     * @return Review
     */
    public Review getReview(int reviewId, ReviewType type) throws Exception {

        try {

            Review review = new Review();

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
                review.setTime(resultSet.getTimestamp("review_time"));

                return review;
            }

        }

        // If exception happens when querying user
        catch (Exception e) {

            throw new Exception("Some error happened, please contact the IT administrator.");
        }

        finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
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

        try {

            List<Review> reviewList = new ArrayList<>();

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_gym_id\" IS NOT NULL";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_library_id\" IS NOT NULL";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_restaurant_id\" IS NOT NULL";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_user_id\" = ? and \"review_study_space_id\" IS NOT NULL";
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
                review.setTime(resultSet.getTimestamp("review_time"));
                review.setDate(review.getTime());

                reviewList.add(review);
            }

            return reviewList;
        }

        // If exception happens when querying user
        catch (Exception e) {

            throw new Exception("Some error happened, please contact the IT administrator.");
        }

        finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }
    }

    /**
     * Get a user with specified email address
     *
     * @param entityId as the entity id
     * @param type     as the review type
     * @return list of reviews
     */
    public List<Review> getReviewsByEntity(Integer entityId, ReviewType type) throws Exception {

        try {

            List<Review> reviewList = new ArrayList<>();

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_gym_id\" = ?";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_library_id\" = ?";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_restaurant_id\" = ?";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT * FROM mobilecomputing.\"review\" WHERE \"review_study_space_id\" = ?";
                    break;
                default:
                    throw new Exception("Invalid review type. Cannot retrieve review list.");
            }


            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, entityId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Iterate all the resulting rows from the query
                Review review = new Review();

                // Set review information
                review.setReviewId(resultSet.getInt("review_id"));
                review.setUserId(resultSet.getInt("review_user_id"));
                review.setComment(resultSet.getString("comment"));
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
                review.setTime(resultSet.getTimestamp("review_time"));

                reviewList.add(review); // add review to the list
            }

            Collections.reverse(reviewList);
            return reviewList;
        }

        // If exception happens when querying user
        catch (Exception e) {

            throw new Exception("Some error happened, please contact the IT administrator.");
        }

        finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }
    }


    public double getAverageRating(Integer entityId, ReviewType type) throws SQLException {

        try {

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT gym_id,ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " +
                            "FROM mobilecomputing.\"review\" " +
                            "WHERE gym_id IS NOT NULL and gym_id = ? " +
                            "GROUP BY gym_id " +
                            "ORDER BY average_rating DESC;";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT review_library_id,ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " +
                            "FROM mobilecomputing.\"review\" " +
                            "WHERE review_library_id IS NOT NULL and review_library_id = ? " +
                            "GROUP BY review_library_id " +
                            "ORDER BY average_rating DESC;";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT restaurant_id,ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " +
                            "FROM mobilecomputing.\"review\" " +
                            "WHERE restaurant_id IS NOT NULL and restaurant_id = ? " +
                            "GROUP BY restaurant_id " +
                            "ORDER BY average_rating DESC;";
                    break;

                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT review_study_space_id, ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " +
                            "FROM mobilecomputing.\"review\" " +
                            "WHERE review_study_space_id IS NOT NULL AND review_study_space_id = ? " +
                            "GROUP BY review_study_space_id " +
                            "ORDER BY average_rating DESC;";
                    break;
                default:
                    throw new Exception("Invalid review type. Cannot retrieve review list.");
            }

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, entityId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Ensure there's a row in the result set
                Double rating = Double.parseDouble(resultSet.getString("average_rating"));

                return rating;

            }
        }

        catch (Exception e) {

            throw new RuntimeException(e);
        }

        finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }

        return 5;
    }
}
