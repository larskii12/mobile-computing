package com.comp90018.uninooks.service.library;

import android.util.Log;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.activities.MainActivity;
import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

public class LibraryServiceImpl implements LibraryService{

    Connection connector = new DatabaseHelper().getConnector();

    ArrayList<Library> closestLibraries = new ArrayList<>();

    ArrayList<Library> topRatedLibraries = new ArrayList<>();

    /**
     * Get ten closest libraries and return to the Main UI to show
     * @param location as the current location
     * @return ten sorted closest libraries by walking distance
     * @throws Exception if any exception happens
     */
    @Override
    public ArrayList<Library> getClosestLibraries(LatLng location, int size) throws Exception {

        try {
            String query = "SELECT library_id, library_building_id FROM mobilecomputing.\"library\"";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<Library> allLibraries = new ArrayList<>();
            ArrayList<Library> openingLibraries = new ArrayList<>();
            ArrayList<Library> closingLibraries = new ArrayList<>();

            // Set user information
            while (resultSet.next()) { // Ensure there's a row in the result set

                int libraryId = Integer.parseInt(resultSet.getString("library_id"));

                Library library = new LocationServiceImpl().findLibraryById(libraryId);

                // Exclude the closed libraries
                if (library != null) {
                    allLibraries.add(new LocationServiceImpl().findLibraryById(libraryId));
                }
            }

            // Get current Position
            LatLng currentLocation = GPSServiceImpl.getCurrentLocation();

            allLibraries.sort((libraryOne, libraryTwo) -> {
                double dist1 = calculateDistance(libraryOne.getLocation(), currentLocation);
                double dist2 = calculateDistance(libraryTwo.getLocation(), currentLocation);
                return Double.compare(dist1, dist2);
            });

            // Sort ten libraries by waling distance from Google Map API
            closestLibraries = calculateSpaceByWalkingDistance(currentLocation, allLibraries);
            sortByDistance(closestLibraries);

            for (Library library : closestLibraries) {
                if (library.isOpeningNow()) {
                    openingLibraries.add(library);
                } else {
                    closingLibraries.add(library);
                }
            }

            openingLibraries.addAll(closingLibraries);

            if (openingLibraries.size() <= size) {
                return openingLibraries;
            }

            return new ArrayList<>(openingLibraries.subList(0, size));
        }

        catch (Exception e){
            throw new Exception();
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
     * Get the sorted closest ten libraries by walking distance from current location
     * @param currentLocation as current location
     * @param libraries as ten closest libraries
     * @return sorted closest ten libraries by walking distance from current location
     * @throws IOException if exception happens
     */
    public ArrayList<Library> calculateSpaceByWalkingDistance(LatLng currentLocation, ArrayList<Library> libraries) throws IOException {

        try {

            String origin = currentLocation.latitude + "," + currentLocation.longitude;

            StringBuilder destination = new StringBuilder();

            for (Library library : libraries) {

                destination.append(library.getLocation().latitude).append(",").append(library.getLocation().longitude).append("|");
                System.out.println(library.getName());
            }

            InputStream inputStream = MainActivity.getAppContext().getResources().openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(inputStream);

            // Distance Matrix query URL
            String requestURL = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                    "origins=" + origin + "&" +
                    "destinations=" + destination + "&" +
                    "mode=" + "walking" + "&" +
                    "key=" + properties.getProperty("API_KEY");

            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                // output as json format and print in JSON
                ObjectMapper mapper = new ObjectMapper();                             //
                Object json = mapper.readValue(response.toString(), Object.class);  //
                String jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);  //
                Log.d("API RESPONSE", jsonStr);     //


                // Extract response, add the distance to library object.

                ArrayList<Double> distances = getDistanceFromJson(response);

                int position = 0;

                // Iterate each library and set the distance to current location
                for (Library library : libraries){

                    library.setDistanceFromCurrentPosition(distances.get(position));
                    position++;
                }

                return libraries;

            } else {
                System.out.println("Get data failed, code is: " + responseCode + ". Please try again.");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<Library>();
    }

    /**
     * Sort study spaces by distances
     * @param libraries as study spaces
     * @return distance sorted study spaces
     */
    private ArrayList<Library> sortByDistance(ArrayList<Library> libraries){
        // Sort by walking distance, if distance same, sort by name
        libraries.sort((studySpaceOne, studySpaceTwo) -> {
            int distanceComparison = Double.compare(studySpaceOne.getDistanceFromCurrentPosition(), studySpaceTwo.getDistanceFromCurrentPosition());
            if (distanceComparison != 0) {
                return distanceComparison;
            } else {
                return studySpaceOne.getName().compareTo(studySpaceTwo.getName());
            }
        });

        return libraries;
    }


    /**
     * Extract distance informatin from Google API returned json response
     * @param response Google API response Json
     * @return arraylist of distance
     * @throws JSONException if any exception happens
     */
    private ArrayList<Double> getDistanceFromJson(StringBuffer response) throws JSONException {

        ArrayList<Double> distances = new ArrayList<>();

        // Convert response JSON to respond String
        String responseInString = response.toString();
        JSONObject jsonObject = new JSONObject(responseInString);

        // Get response json content

        JSONArray responseElementsArray = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");

        for (int i = 0; i < responseElementsArray.length(); i++) {

            JSONObject element = new JSONObject(responseElementsArray.get(i).toString());

            if (element.getString("status").equals("OK")) {

                // Add to the distance arraylist
                distances.add(Double.parseDouble(element.getJSONObject("distance").getString("value")));
            }

            // If no data fetched or path is unavailable
            else{
                distances.add(-1.0);
            }
        }
        return distances;
    }

    /**
     * Calculate distance between two points, sphere considered.
     *
     * @param source      source point
     * @param destination destination point
     * @return distance between two points
     */
    private double calculateDistance(LatLng source, LatLng destination) {

        // Earth radius in meters
        final int R = 6371000;

        double latDistance = Math.toRadians(destination.latitude - source.latitude);
        double lonDistance = Math.toRadians(destination.longitude - source.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(source.latitude)) * Math.cos(Math.toRadians(destination.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;
        return distance;
    }


    /**
     * Get top rated libraries
     * @param location as current location
     * @param size as size
     * @return top rated sorted libraries
     * @throws Exception if any exceptions
     */
    @Override
    public ArrayList<Library> getTopRatedLibraries(LatLng location, int size) throws Exception {

        try {
            String query = "SELECT review_library_id, ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " +
                    "FROM mobilecomputing.\"review\" " +
                    "WHERE review_library_id IS NOT NULL " +
                    "GROUP BY review_library_id " +
                    "ORDER BY average_rating DESC;";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<Library> allLibraries = new ArrayList<>();
            ArrayList<Library> openingLibraries = new ArrayList<>();
            ArrayList<Library> closingLibraries = new ArrayList<>();

            // Set user information
            while (resultSet.next()) { // Ensure there's a row in the result set

                int libraryId = Integer.parseInt(resultSet.getString("review_library_id"));

                Library library = new LocationServiceImpl().findLibraryById(libraryId);
                library.setAverage_rating(Double.parseDouble(resultSet.getString("average_rating")));

                allLibraries.add(library);
            }

            // Get current Position
            LatLng currentLocation = GPSServiceImpl.getCurrentLocation();

            // Calculate the distance for each top rated study spaces
            topRatedLibraries = calculateSpaceByWalkingDistance(currentLocation, allLibraries);

            // Return opening study space first
            for (Library library : topRatedLibraries) {
                if (library.isOpeningNow()) {
                    openingLibraries.add(library);
                } else {
                    closingLibraries.add(library);
                }
            }

            openingLibraries.addAll(closingLibraries);

            if (openingLibraries.size() <= size) {
                return openingLibraries;
            }

            return new ArrayList<>(openingLibraries.subList(0, size));
        }

        catch (Exception e){
            throw new Exception();
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
