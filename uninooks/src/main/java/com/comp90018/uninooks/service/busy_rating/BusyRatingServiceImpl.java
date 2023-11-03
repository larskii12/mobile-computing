package com.comp90018.uninooks.service.busy_rating;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.busy_rating.BusyRating;
import com.comp90018.uninooks.models.review.ReviewType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class BusyRatingServiceImpl implements BusyRatingService {

    Connection connector = new DatabaseHelper().getConnector();

    /**
     * Update busy rating at specific time slot
     * @param entityId as entity id
     * @param type as entity type
     * @param busyRatingScore as the score user gave
     * @return true if rate success, otherwise false
     * @throws Exception if any exception happens
     */
    public Boolean addBusyRating(int entityId, ReviewType type, double busyRatingScore) throws Exception {

        // Get the current hour
        int hour = LocalTime.now().getHour();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the time with hour only
        Time timeWithoutHour = new Time(calendar.getTimeInMillis());

        // Get date
        ZonedDateTime zonedIST = ZonedDateTime.now(ZoneId.of("Australia/Sydney"));
        Integer currentDayOfWeek = zonedIST.getDayOfWeek().getValue();

        try {
            String query = "";

            switch (type) {

                case GYM: // Add new gym busy review to the database
                    query = "UPDATE mobilecomputing.busy_rating "
                            + "SET total_score = total_score + ?, "
                            + "busy_rating_count = busy_rating_count + 1, "
                            + "average_score = (total_score + ?) / (busy_rating_count + 1) "
                            + "WHERE "
                            + "busy_rating_gym_id = ? AND busy_rating_date = ? AND busy_rating_time = ?";
                    break;

                case LIBRARY: // Add new library busy review to the database
                    query = "UPDATE mobilecomputing.busy_rating "
                            + "SET total_score = total_score + ?, "
                            + "busy_rating_count = busy_rating_count + 1, "
                            + "average_score = (total_score + ?) / (busy_rating_count + 1) "
                            + "WHERE "
                            + "busy_rating_library_id = ? AND busy_rating_date = ? AND busy_rating_time = ?";
                    break;

                case RESTAURANT: // Add new restaurant busy review to the database
                    query = "UPDATE mobilecomputing.busy_rating "
                            + "SET total_score = total_score + ?, "
                            + "busy_rating_count = busy_rating_count + 1, "
                            + "average_score = (total_score + ?) / (busy_rating_count + 1) "
                            + "WHERE "
                            + "busy_rating_restaurant_id = ? AND busy_rating_date = ? AND busy_rating_time = ?";
                    break;

                case STUDY_SPACE: // Add new study space busy review to the database
                    query = "UPDATE mobilecomputing.busy_rating "
                            + "SET total_score = total_score + ?, "
                            + "busy_rating_count = busy_rating_count + 1, "
                            + "average_score = (total_score + ?) / (busy_rating_count + 1) "
                            + "WHERE "
                            + "busy_rating_study_space_id = ? AND busy_rating_date = ? AND busy_rating_time = ?";

                    break;

                default:
                    throw new Exception("Invalid review type. Review not created.");
            }

            PreparedStatement preparedStatement = connector.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDouble(1, busyRatingScore);
            preparedStatement.setDouble(2, busyRatingScore);
            preparedStatement.setInt(3,  entityId);
            preparedStatement.setInt(4, currentDayOfWeek);
            preparedStatement.setTime(5, timeWithoutHour);

            // Execute query
            preparedStatement.executeUpdate();

            return true;
        }

        // If exception happens
        catch (Exception e) {

            // Unknown exceptions happens.
            throw new Exception("User added failed, please contact the IT administrator to report the issue.");
        }
    }


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
                busyRating.setAverageScore(resultSet.getDouble("average_score"));
                busyRating.setCount(resultSet.getInt("busy_rating_count"));
                busyRating.setTime(resultSet.getTime("busy_rating_time"));
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

    public Double getAverageScoreFromEntity(int entityId, ReviewType type) throws Exception {

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
                return resultSet.getDouble(1);
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
