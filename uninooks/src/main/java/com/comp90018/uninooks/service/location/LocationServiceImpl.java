package com.comp90018.uninooks.service.location;


import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.location.LocationType;
import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.models.location.restaurant.Restaurant;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.models.review.ReviewType;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.review.ReviewServiceImpl;
import com.comp90018.uninooks.service.time.TimeServiceImpl;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LocationServiceImpl implements LocationService {

    Connection connector = new DatabaseHelper().getConnector();

    public Object findLocationById(int locationId, LocationType locationType) throws Exception {

        switch (locationType) {
            case LIBRARY:
                return findLibraryById(locationId);
            case RESTAURANT:
                return findRestaurantById(locationId);
            case STUDY_SPACE:
                return findStudySpaceById(locationId);
            default:
                throw new Exception("Invalid location type. Location not found.");
        }
    }

    public Library findLibraryById(int locationId) throws Exception {

        try {
            Library library = new Library();

            // Get date of a week
            int currentDayOfWeek = new TimeServiceImpl().getWeekDate();

            // Get the current time
            Time currentTime = new TimeServiceImpl().getAEDTTime();

            String query = "SELECT * FROM mobilecomputing.library l " + "join mobilecomputing.opening_hours o on l.library_id = o.library_id " + "join mobilecomputing.\"building\" b ON l.library_building_id = b.building_id " + "WHERE l.library_id = ? and o.date = " + currentDayOfWeek;


            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, locationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // Ensure there's a row in the result set

                // Set library information
                library.setId(resultSet.getInt("library_id"));
                library.setBuildingId(resultSet.getInt("library_building_id"));
                library.setName(resultSet.getString("library_name"));

                library.setOpenTime(resultSet.getTime("opening_time"));
                library.setCloseTime(resultSet.getTime("closing_time"));

                library.setLocation(new LatLng(Double.parseDouble(resultSet.getString("building_latitude")), Double.parseDouble(resultSet.getString("building_longitude"))));
                library.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), library.getLocation()));

                library.setAverage_rating(new ReviewServiceImpl().getAverageRating(locationId, ReviewType.LIBRARY));

                library.setType("LIBRARY");

                library.setIsOpenToday(library.getOpenTime() != null);

                // Set is open now or not
                if (library.getOpenTime() != null && currentTime.after(library.getOpenTime()) && currentTime.before(library.getCloseTime())) {
                    library.setIsOpeningNow(true);
                    library.setIsOpenToday(true);
                }

                library.setHasQuietZones(resultSet.getBoolean("library_has_quiet_zones"));

                return library;
            }

        } catch (Exception e) { // If exception happens when querying library

            throw new Exception("Some error happened, please contact the IT administrator.");
        } finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }

        return null;
    }

    public Restaurant findRestaurantById(int locationId) throws Exception {

        try {

            int currentDayOfWeek = new TimeServiceImpl().getWeekDate();

            Restaurant restaurant = new Restaurant();

            String query = "SELECT * FROM mobilecomputing.restaurant r join mobilecomputing.opening_hours o " + "on r.restaurant_id = o.restaurant_id " + "WHERE r.restaurant_id = ? and o.date = " + currentDayOfWeek;

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, locationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                restaurant.setId(resultSet.getInt("restaurant_id"));
                restaurant.setBuildingId(resultSet.getInt("restaurant_building_id"));
                restaurant.setName(resultSet.getString("restaurant_name"));

                restaurant.setOpenTime(resultSet.getTime("opening_time"));
                restaurant.setCloseTime(resultSet.getTime("closing_time"));

                Time currentTime = new TimeServiceImpl().getAEDTTime();
                if (restaurant.getOpenTime() != null && currentTime.after(restaurant.getOpenTime()) && currentTime.before(restaurant.getCloseTime())) {
                    restaurant.setIsOpeningNow(true);
                    restaurant.setIsOpenToday(true);
                }

                restaurant.setFloorLevel(resultSet.getInt("restaurant_floor_level"));
                restaurant.setHasVegetarianOptions(resultSet.getBoolean("restaurant_vegetarian_options"));

                return restaurant;
            }

        } catch (Exception e) { // If exception happens when querying restaurant
            throw new Exception("Some error happened, please contact the IT administrator.");
        } finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (SQLException e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }

        return null;
    }

    public StudySpace findStudySpaceById(int locationId) throws Exception {

        try {

            StudySpace studySpace = new StudySpace();

            // Get date and time
            int currentDayOfWeek = new TimeServiceImpl().getWeekDate();
            Time currentTime = new TimeServiceImpl().getAEDTTime();

            String query = "SELECT * FROM mobilecomputing.study_space s " + "join mobilecomputing.opening_hours o  " + "" + "on s.study_space_id = o.study_space_id " + "join mobilecomputing.\"building\" b ON s.study_space_building_id = b.building_id " + "WHERE s.study_space_id = ? and o.date = " + currentDayOfWeek;

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, locationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // Ensure there's a row in the result set

                // Set study space information
                studySpace.setId(resultSet.getInt("study_space_id"));
                studySpace.setBuildingId(resultSet.getInt("study_space_building_id"));
                studySpace.setName(resultSet.getString("study_space_name"));

                studySpace.setOpenTime(resultSet.getTime("opening_time"));
                studySpace.setCloseTime(resultSet.getTime("closing_time"));

                studySpace.setLocation(new LatLng(Double.parseDouble(resultSet.getString("building_latitude")), Double.parseDouble(resultSet.getString("building_longitude"))));
                studySpace.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), studySpace.getLocation()));

                studySpace.setAverage_rating(new ReviewServiceImpl().getAverageRating(locationId, ReviewType.STUDY_SPACE));

                studySpace.setType("STUDY_SPACE");

                studySpace.setIsOpenToday(studySpace.getOpenTime() != null);

                if (studySpace.getOpenTime() != null && currentTime.after(studySpace.getOpenTime()) && currentTime.before(studySpace.getCloseTime())) {
                    studySpace.setIsOpeningNow(true);
                    studySpace.setIsOpenToday(true);
                }

//                Array daysDb = resultSet.getArray("study_space_opening_days");
//                Integer[] days = (Integer[]) daysDb.getArray();
//                studySpace.setOpeningDays(days);
//
//                Array busyDb = resultSet.getArray("study_space_busy_hours");
//                Time[] busyHours = (Time[]) busyDb.getArray();
//                studySpace.setBusyHours(busyHours);

                studySpace.setLibraryId(resultSet.getInt("study_space_library_id"));
//               studySpace.setCapacity(resultSet.getInt("study_space_capacity"));
//                studySpace.setFloorLevel(resultSet.getInt(0));
                studySpace.setMinimumAccessAQFLevel(resultSet.getInt("study_space_minimum_access_AQF_level"));
                studySpace.setTalkAllowed(resultSet.getBoolean("study_space_talk_allowed"));

                return studySpace;
            }

        } catch (Exception e) { // If exception happens when querying study space
            e.printStackTrace();

            throw new Exception("Some error happened, please contact the IT administrator.");
        } finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception e) {
                    System.out.println("Database Connection close failed.");
                }
            }
        }

        return null;
    }


    public List<Location> findAllLocations(String locationType, String name, boolean isAscending) throws Exception {

        try {

            List<Location> allLocations = new ArrayList<>();

            String searchName = "";
            if (name != null) {
                searchName = name.toLowerCase();
            }

            LocalDate currentDate = LocalDate.now();
            int currentDayOfWeek = new TimeServiceImpl().getWeekDate();

            String query;
            PreparedStatement preparedStatement;
            if (locationType.equals("STUDY")) {
                // Query for extracting all filtered libraries
                if (!searchName.equals("")) {
                    query = "SELECT * FROM mobilecomputing.library l  " + "" + "join mobilecomputing.opening_hours o on l.library_id = o.library_id " + "join mobilecomputing.\"building\" b ON l.library_building_id = b.building_id " + "WHERE lower(library_name) like '%" + searchName + "%' and o.date = " + currentDayOfWeek;
                } else {
                    query = "SELECT * FROM mobilecomputing.library l " + "" + "join mobilecomputing.opening_hours o on l.library_id = o.library_id " + "join mobilecomputing.\"building\" b ON l.library_building_id = b.building_id " + "WHERE o.date = " + currentDayOfWeek;
                }

                preparedStatement = connector.prepareStatement(query);
                System.out.println(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) { // Iterate all the resulting rows from the query
                    Location location = new Library();
                    // Set location information
                    location.setId(resultSet.getInt("library_id"));
                    location.setName(resultSet.getString("library_name"));
                    location.setBuildingId(resultSet.getInt("library_building_id"));

                    location.setOpenTime(resultSet.getTime("opening_time"));
                    location.setCloseTime(resultSet.getTime("closing_time"));

                    location.setLocation(new LatLng(resultSet.getDouble("building_latitude"), resultSet.getDouble("building_longitude")));

                    location.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), location.getLocation()));

                    ((Library) location).setHasQuietZones(resultSet.getBoolean("library_has_quiet_zones"));
                    location.setType("LIBRARY");

                    location.setAverage_rating(new ReviewServiceImpl().getAverageRating(location.getId(), ReviewType.LIBRARY));

                    Time currentTime = new TimeServiceImpl().getAEDTTime();
                    if (location.getOpenTime() != null && currentTime.after(location.getOpenTime()) && currentTime.before(location.getCloseTime())) {
                        location.setIsOpeningNow(true);
                        location.setIsOpenToday(true);
                    }

                    allLocations.add(location);
                }


                // Query for extracting all filtered study spaces
                if (!searchName.equals("")) {
                    query = "SELECT * FROM mobilecomputing.study_space s  " + "" + "join mobilecomputing.opening_hours o " + "on s.study_space_id = o.study_space_id " + "join mobilecomputing.\"building\" b ON s.study_space_building_id = b.building_id " + "WHERE lower(study_space_name) like '%" + searchName + "%' and o.date = " + currentDayOfWeek;
                } else {
                    query = "SELECT * FROM mobilecomputing.study_space s join mobilecomputing.opening_hours o " + "on s.study_space_id = o.study_space_id " + "join mobilecomputing.\"building\" b ON s.study_space_building_id = b.building_id " + "WHERE o.date = " + currentDayOfWeek;
                }

                preparedStatement = connector.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) { // Iterate all the resulting rows from the query
                    Location location = new StudySpace();
                    // Set location information
                    location.setId(resultSet.getInt("study_space_id"));
                    location.setName(resultSet.getString("study_space_name"));
                    location.setBuildingId(resultSet.getInt("study_space_building_id"));

                    location.setOpenTime(resultSet.getTime("opening_time"));
                    location.setCloseTime(resultSet.getTime("closing_time"));

                    location.setLocation(new LatLng(resultSet.getDouble("building_latitude"), resultSet.getDouble("building_longitude")));
                    location.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), location.getLocation()));

                    location.setAverage_rating(new ReviewServiceImpl().getAverageRating(location.getId(), ReviewType.STUDY_SPACE));

                    Time currentTime = new TimeServiceImpl().getAEDTTime();
                    if (location.getOpenTime() != null && currentTime.after(location.getOpenTime()) && currentTime.before(location.getCloseTime())) {
                        location.setIsOpeningNow(true);
                        location.setIsOpenToday(true);
                    }

                    location.setType("STUDY_SPACE");
                    ((StudySpace) location).setMinimumAccessAQFLevel(resultSet.getInt("study_space_minimum_access_AQF_level"));
                    ((StudySpace) location).setTalkAllowed(resultSet.getBoolean("study_space_talk_allowed"));


                    location.setType("STUDY_SPACE");

                    allLocations.add(location);
                }

                return allLocations;

            } else if (locationType.equals("FOOD")) {
                if (!searchName.equals("")) {
                    query = "SELECT * FROM mobilecomputing.restaurant r join mobilecomputing.opening_hours o " + "on r.restaurant_id = o.restaurant_id " + "WHERE lower(restaurant_name) like '%" + searchName + "%' and o.date = " + currentDayOfWeek;
                } else {
                    query = "SELECT * FROM mobilecomputing.restaurant r join mobilecomputing.opening_hours o " + "on r.restaurant_id = o.restaurant_id " + "WHERE o.date = " + currentDayOfWeek;
                }

                preparedStatement = connector.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) { // Iterate all the resulting rows from the query
                    Location location = new Restaurant();
                    // Set review information
                    location.setId(resultSet.getInt("restaurant_id"));
                    location.setName(resultSet.getString("restaurant_name"));
                    location.setBuildingId(resultSet.getInt("restaurant_building_id"));

                    location.setOpenTime(resultSet.getTime("opening_time"));
                    location.setCloseTime(resultSet.getTime("closing_time"));

                    location.setType("RESTAURANT");

                    Time currentTime = new TimeServiceImpl().getAEDTTime();
                    if (location.getOpenTime() != null && currentTime.after(location.getOpenTime()) && currentTime.before(location.getCloseTime())) {
                        location.setIsOpeningNow(true);
                        location.setIsOpenToday(true);
                    }

                    location.setAverage_rating(new ReviewServiceImpl().getAverageRating(location.getId(), ReviewType.RESTAURANT));

                    allLocations.add(location);
                }

                return allLocations;

            } else if (locationType.equals("ALL")) {
                if (!searchName.equals("")) {
                    query = "SELECT * FROM mobilecomputing.library l join mobilecomputing.opening_hours o on l.library_id = o.library_id " + "WHERE lower(library_name) like '%" + searchName + "%' and o.date = " + currentDayOfWeek;
                } else {
                    query = "SELECT * FROM mobilecomputing.library l join mobilecomputing.opening_hours o on l.library_id = o.library_id " + "WHERE o.date = " + currentDayOfWeek;
                }

                preparedStatement = connector.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) { // Iterate all the resulting rows from the query
                    Location location = new Library();
                    // Set review information
                    location.setId(resultSet.getInt("library_id"));
                    location.setName(resultSet.getString("library_name"));
                    location.setBuildingId(resultSet.getInt("library_building_id"));

                    location.setOpenTime(resultSet.getTime("opening_time"));
                    location.setCloseTime(resultSet.getTime("closing_time"));

                    location.setLocation(new LatLng(resultSet.getDouble("building_latitude"), resultSet.getDouble("building_longitude")));
                    location.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), location.getLocation()));

                    location.setType("LIBRARY");

                    location.setAverage_rating(new ReviewServiceImpl().getAverageRating(location.getId(), ReviewType.LIBRARY));

                    Time currentTime = new TimeServiceImpl().getAEDTTime();
                    if (location.getOpenTime() != null && currentTime.after(location.getOpenTime()) && currentTime.before(location.getCloseTime())) {
                        location.setIsOpeningNow(true);
                        location.setIsOpenToday(true);
                    }

                    allLocations.add(location);
                }

                // Query for extracting all filtered study spaces
                if (!searchName.equals("")) {
                    query = "SELECT * FROM mobilecomputing.study_space s join mobilecomputing.opening_hours o " + "on s.study_space_id = o.study_space_id " + "WHERE lower(study_space_name) like '%" + searchName + "%' and o.date = " + currentDayOfWeek;
                } else {
                    query = "SELECT * FROM mobilecomputing.study_space s join mobilecomputing.opening_hours o " + "on s.study_space_id = o.study_space_id " + "WHERE o.date = " + currentDayOfWeek;
                }

                preparedStatement = connector.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) { // Iterate all the resulting rows from the query
                    Location location = new StudySpace();
                    // Set location information
                    location.setId(resultSet.getInt("study_space_id"));
                    location.setName(resultSet.getString("study_space_name"));
                    location.setBuildingId(resultSet.getInt("study_space_building_id"));

                    location.setOpenTime(resultSet.getTime("opening_time"));
                    location.setCloseTime(resultSet.getTime("closing_time"));

                    location.setType("STUDY_SPACE");

                    Time currentTime = new TimeServiceImpl().getAEDTTime();
                    if (location.getOpenTime() != null && currentTime.after(location.getOpenTime()) && currentTime.before(location.getCloseTime())) {
                        location.setIsOpeningNow(true);
                        location.setIsOpenToday(true);
                    }

                    location.setLocation(new LatLng(resultSet.getDouble("building_latitude"), resultSet.getDouble("building_longitude")));
                    location.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), location.getLocation()));

                    location.setAverage_rating(new ReviewServiceImpl().getAverageRating(location.getId(), ReviewType.STUDY_SPACE));

                    allLocations.add(location);
                }

                if (!searchName.equals("")) {
                    query = "SELECT * FROM mobilecomputing.restaurant r join mobilecomputing.opening_hours o " + "on r.restaurant_id = o.restaurant_id " + "WHERE lower(restaurant_name) like '%" + searchName + "%' and o.date = " + currentDayOfWeek;
                } else {
                    query = "SELECT * FROM mobilecomputing.restaurant r join mobilecomputing.opening_hours o " + "on r.restaurant_id = o.restaurant_id and o.date = " + currentDayOfWeek;
                }

                preparedStatement = connector.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) { // Iterate all the resulting rows from the query

                    Location location = new Restaurant();
                    // Set review information
                    location.setId(resultSet.getInt("restaurant_id"));
                    location.setName(resultSet.getString("restaurant_name"));
                    location.setBuildingId(resultSet.getInt("restaurant_building_id"));

                    location.setOpenTime(resultSet.getTime("opening_time"));
                    location.setCloseTime(resultSet.getTime("closing_time"));

                    location.setType("RESTAURANT");

                    location.setLocation(new LatLng(resultSet.getDouble("building_latitude"), resultSet.getDouble("building_longitude")));
                    location.setDistanceFromCurrentPosition(calculateDistance(GPSServiceImpl.getCurrentLocation(), location.getLocation()));

                    location.setAverage_rating(new ReviewServiceImpl().getAverageRating(location.getId(), ReviewType.RESTAURANT));

                    Time currentTime = new TimeServiceImpl().getAEDTTime();
                    if (location.isOpenToday() && currentTime.after(location.getOpenTime()) && currentTime.before(location.getCloseTime())) {
                        location.setIsOpeningNow(true);
                        location.setIsOpenToday(true);
                    }

                    allLocations.add(location);
                }
                return allLocations;

            } else {
                throw new Exception("Does not exist!!");
            }

        } catch (Exception e) { // If exception happens when querying library
            throw new Exception("Some error happened, please contact the IT administrator.");
        } finally {
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
     * Calculate distance between two points, sphere considered.
     *
     * @param source      source point
     * @param destination destination point
     * @return distance between two points
     */
    public int calculateDistance(LatLng source, LatLng destination) {

        // Earth radius in meters
        final int R = 6371000;

        double latDistance = Math.toRadians(destination.latitude - source.latitude);
        double lonDistance = Math.toRadians(destination.longitude - source.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(source.latitude)) * Math.cos(Math.toRadians(destination.latitude)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;
        return (int) distance;
    }
}
