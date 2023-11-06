package com.comp90018.uninooks.service.favorite;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.review.ReviewType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FavoriteServiceImpl implements FavoriteService {

    Connection connector = new DatabaseHelper().getConnector();

    /**
     * method to add a new favourite of an entity of a user into the database (To add favourite)
     *
     * @param userId     as the user id
     * @param entityId   as the entity id
     * @param type       as the favourite type
     * @throws Exception if any exceptions happens
     */
    public Favorite addFavorite(Integer userId, Integer entityId, ReviewType type) throws Exception {

        try {
            String query;

            switch (type) {

                case GYM: // Add new gym favourite to the database
                    query = "INSERT INTO mobilecomputing.favorite (favourite_user_id, favourite_gym_id) VALUES (?, ?);";
                    break;
                case LIBRARY: // Add new library favourite to the database
                    query = "INSERT INTO mobilecomputing.favourite (favourite_user_id, favourite_library_id) VALUES (?, ?);";
                    break;
                case RESTAURANT: // Add new restaurant favourite to the database
                    query = "INSERT INTO mobilecomputing.favourite (favourite_user_id, favourite_restaurant_id) VALUES (?, ?);";
                    break;
                case STUDY_SPACE: // Add new study space favourite to the database
                    query = "INSERT INTO mobilecomputing.favourite (favourite_user_id, favourite_study_space_id) VALUES (?, ?);";
                    break;
                default:
                    throw new Exception("Invalid favourite type. Review not created.");
            }
            PreparedStatement preparedStatement = connector.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, entityId);

            // Execute query
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            int generatedKey = 0;
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
            return getFavorite(generatedKey, type);
        }

        // If exception happens
        catch (Exception e) {

            // Unknown exceptions happens.
            throw new Exception("Favorite added failed, please contact the IT administrator to report the issue.");
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
     * @param favouriteId, as the favourite id
     * @return Review
     */
    public Favorite getFavorite(int favouriteId, ReviewType type) throws Exception {

        try {
            Favorite favorite = new Favorite();

            String query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, favouriteId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                // Set favourite information
                favorite.setId(resultSet.getInt("favourite_id"));
                favorite.setUserId(resultSet.getInt("favourite_user_id"));
                switch (type) {

                    case GYM: // Set gym id to the favourite
                        favorite.setGymId(resultSet.getInt("favourite_gym_id"));
                        break;
                    case LIBRARY: // Set library id to the favourite
                        favorite.setLibraryId(resultSet.getInt("favourite_library_id"));
                        break;
                    case RESTAURANT: // Set restaurant id to the favourite
                        favorite.setRestaurantId(resultSet.getInt("favourite_restaurant_id"));
                        break;
                    case STUDY_SPACE: // Set study space id to the favourite
                        favorite.setStudySpaceId(resultSet.getInt("favourite_study_space_id"));
                        break;
                    default:
                        throw new Exception("Invalid favourite type. Cannot retrieve favorite list.");
                }
                return favorite;
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
     * Get the list of favourites based by the person
     *
     * @param userId, as the user id
     * @param type, as the type
     * @return list of favorites
     */
    public List<Favorite> getFavoritesByUser(int userId, ReviewType type) throws Exception {

        try {

            List<Favorite> favoriteList = new ArrayList<>();

            String query;

            switch (type) {

                case GYM: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_gym_id\" IS NOT NULL";
                    break;
                case LIBRARY: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_library_id\" IS NOT NULL";
                    break;
                case RESTAURANT: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_restaurant_id\" IS NOT NULL";
                    break;
                case STUDY_SPACE: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_study_space_id\" IS NOT NULL";
                    break;
                default:
                    throw new Exception("Invalid favourite type. Cannot retrieve favourite list.");
            }


            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Iterate all the resulting rows from the query
                Favorite favorite = new Favorite();
                // Set favourite information
                favorite.setId(resultSet.getInt("favourite_id"));
                favorite.setUserId(resultSet.getInt("favourite_user_id"));
                switch (type) {

                    case GYM: // Set gym id to the favourite
                        favorite.setGymId(resultSet.getInt("favourite_gym_id"));
                        break;
                    case LIBRARY: // Set library id to the favourite
                        favorite.setLibraryId(resultSet.getInt("favourite_library_id"));
                        break;
                    case RESTAURANT: // Set restaurant id to the favourite
                        favorite.setRestaurantId(resultSet.getInt("favourite_restaurant_id"));
                        break;
                    case STUDY_SPACE: // Set study space id to the favourite
                        favorite.setStudySpaceId(resultSet.getInt("favourite_study_space_id"));
                        break;
                    default:
                        throw new Exception("Invalid favourite type. Cannot retrieve favourite list.");
                }

                favoriteList.add(favorite);
            }

            return favoriteList;
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
     * Get a boolean if the place is a favorite of the user
     * @param userId as the user id
     * @param entityId as the entity id
     * @param type as type
     * @return Review
     */
    public Boolean isFavoriteByUser(int userId, int entityId, ReviewType type) throws Exception {

        try {
            String query;
            switch (type) {

                case GYM: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_gym_id\" = ?";
                    break;
                case LIBRARY: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_library_id\" = ?";
                    break;
                case RESTAURANT: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_restaurant_id\" = ?";
                    break;
                case STUDY_SPACE: // Get gym favourite to the database
                    query = "SELECT * FROM mobilecomputing.\"favourite\" WHERE \"favourite_user_id\" = ? and \"favourite_study_space_id\" = ?";
                    break;
                default:
                    throw new Exception("Invalid favourite type. Cannot retrieve favourite list.");
            }

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, entityId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                return true;
            } else {
                return false;
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
    }
}
