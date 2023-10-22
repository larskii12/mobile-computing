package com.example.mainactivity.service.busy_rating;

import com.example.mainactivity.config.DatabaseHelper;
import com.example.mainactivity.models.busy_rating.BusyRating;
import com.example.mainactivity.models.review.ReviewType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BusyRatingServiceImpl implements BusyRatingService {

    Connection connector = new DatabaseHelper().getConnector();

    /**
     * Get a user with specified email address
     *
     * @param entityId
     * @return Review
     */

    public BusyRating getBusyRating(int entityId, ReviewType type) throws Exception {

        BusyRating busyRating = new BusyRating();

        try {

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT * FROM mobilecomputing.\"busy_rating\" WHERE " +
                            "\"busy_rating_gym_id\" = ?";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT * FROM mobilecomputing.\"busy_rating\" WHERE " +
                            "\"busy_rating_library_id\" = ?";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT * FROM mobilecomputing.\"busy_rating\" WHERE " +
                            "\"busy_rating_restaurant_id\" = ?";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT * FROM mobilecomputing.\"busy_rating\" WHERE " +
                            "\"busy_rating_study_space_id\" = ?";
                    break;
                default:
                    throw new Exception("Invalid review type. Cannot retrieve busy rating list.");
            }

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, entityId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                // Set review information
                busyRating.setBusyRatingId(resultSet.getInt("busy_rating_id"));
                switch (type) {

                    case GYM: // Set gym id to the review
                        busyRating.setGymId(resultSet.getInt("busy_rating_gym_id"));
                        break;
                    case LIBRARY: // Set library id to the review
                        busyRating.setLibraryId(resultSet.getInt("busy_rating_library_id"));
                        break;
                    case RESTAURANT: // Set restaurant id to the review
                        busyRating.setRestaurantId(resultSet.getInt("busy_rating_restaurant_id"));
                        break;
                    case STUDY_SPACE: // Set study space id to the review
                        busyRating.setStudySpaceId(resultSet.getInt("busy_rating_study_space_id"));
                        break;
                    default:
                        throw new Exception("Invalid review type. Cannot retrieve review list.");
                }
                busyRating.setTotalScore(resultSet.getInt("total_score"));
                busyRating.setAverageScore(resultSet.getInt("average_score"));
                busyRating.setCount(resultSet.getInt("busy_rating_count"));
                busyRating.setTime(resultSet.getTime("review_time"));
                return busyRating;
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
     * Get a user with specified email address
     *
     * @param entityId
     * @return Review
     */

    public Integer getAverageScoreFromEntity(int entityId, ReviewType type) throws Exception {

        // converting to IST
        ZonedDateTime zonedIST = ZonedDateTime.now(ZoneId.of("Australia/Sydney"));
        //System.out.println(zonedIST.getHour());
        Integer currentDayOfWeek = zonedIST.getDayOfWeek().getValue();

        try {

            String query;

            switch (type) {

                case GYM: // Add new gym review to the database
                    query = "SELECT average_score FROM mobilecomputing.busy_rating WHERE " +
                            "busy_rating_gym_id = ? and busy_rating_date = ? and "+
                            "EXTRACT(HOUR FROM busy_rating_time) = ?";
                    break;
                case LIBRARY: // Add new library review to the database
                    query = "SELECT average_score FROM mobilecomputing.busy_rating WHERE " +
                            "busy_rating_library_id = ? and busy_rating_date = ? and "+
                            "EXTRACT(HOUR FROM busy_rating_time) = ?";
                    break;
                case RESTAURANT: // Add new restaurant review to the database
                    query = "SELECT average_score FROM mobilecomputing.busy_rating WHERE " +
                            "busy_rating_restaurant_id = ? and busy_rating_date = ? and "+
                            "EXTRACT(HOUR FROM busy_rating_time) = ?";
                    break;
                case STUDY_SPACE: // Add new study space review to the database
                    query = "SELECT average_score FROM mobilecomputing.busy_rating WHERE " +
                            "busy_rating_study_space_id = ? and busy_rating_date = ? and "+
                            "EXTRACT(HOUR FROM busy_rating_time) = ?";
                    break;
                default:
                    throw new Exception("Invalid review type. Cannot retrieve busy rating list.");
            }

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, entityId);
            preparedStatement.setInt(2, currentDayOfWeek);
            preparedStatement.setInt(3, zonedIST.getHour());

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                return resultSet.getInt(1);
            }
        }

        // If exception happens when querying user
        catch (Exception e) {
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
        // Return user information
        return null;
    }
}
