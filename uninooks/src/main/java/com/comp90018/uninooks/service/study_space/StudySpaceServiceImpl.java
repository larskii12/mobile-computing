package com.comp90018.uninooks.service.study_space;

import android.util.Log;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.activities.MainActivity;
import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
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

public class StudySpaceServiceImpl implements StudySpaceService {

    Connection connector = new DatabaseHelper().getConnector();

    LocationServiceImpl studySpaceFinder = new LocationServiceImpl();

    ArrayList<StudySpace> closestStudySpaces = new ArrayList<>();

    /**
     * Get ten closest study spaces and return to the Main UI to show
     * @param location as the current location
     * @return ten sorted closest study spaces by walking distance
     * @throws Exception if any exception happens
     */
    @Override
    public ArrayList<StudySpace> getClosestStudySpaces(LatLng location, int size) throws Exception {

        String query = "SELECT study_space_id, study_space_building_id FROM mobilecomputing.\"study_space\"";

        PreparedStatement preparedStatement = connector.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<StudySpace> allStudySpace = new ArrayList<>();

        // Set user information
        while (resultSet.next()) { // Ensure there's a row in the result set

            int studySpaceId = Integer.parseInt(resultSet.getString("study_space_id"));
            allStudySpace.add(studySpaceFinder.findStudySpaceById(studySpaceId));
        }

        // GIS check ordering, use on deployment
//        LatLng currentLocation = new LatLng(GPSServiceImpl.getLatestLocation().latitude, GPSServiceImpl.getLatestLocation().longitude);

        // Fake current position, use in development
        LatLng currentLocation = new LatLng(-37.8000898318753, 144.96443598212284);

        allStudySpace.sort((studySpaceOne, studySpaceTwo) -> {
            double dist1 = calculateDistance(studySpaceOne.getLocation(), currentLocation);
            double dist2 = calculateDistance(studySpaceTwo.getLocation(), currentLocation);
            return Double.compare(dist1, dist2);
        });

        // if more than requested number of spaces found, get the requested number of study spaces.
        if (allStudySpace.size() >= size) {
            allStudySpace = new ArrayList<>(allStudySpace.subList(0, size));
        }

        // Sort ten study spaces by waling distance from Google Map API
        closestStudySpaces = sortStudySpaceByWalkingDistance(currentLocation, allStudySpace);

        return closestStudySpaces;
    }

    /**
     * Get the sorted closest ten study spaces by walking distance from current location
     * @param currentLocation as current location
     * @param studySpaces as ten closest study spaces
     * @return sorted closest ten study spaces by walking distance from current location
     * @throws IOException if exception happens
     */
    private ArrayList<StudySpace> sortStudySpaceByWalkingDistance(LatLng currentLocation, ArrayList<StudySpace> studySpaces) throws IOException {

        String origin = currentLocation.latitude + "," + currentLocation.longitude;

        StringBuilder destination = new StringBuilder();

        for (StudySpace studySpace : studySpaces) {

            destination.append(studySpace.getLocation().latitude).append(",").append(String.valueOf(studySpace.getLocation().longitude)).append("|");
            System.out.println(studySpace.getName());
        }

        try {

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


                // Extract response, add the distance to study space object.

                ArrayList<Double> distances = getDistanceFromJson(response);

                int position = 0;

                // Iterate each study space and set the distance to current location
                for (StudySpace studySpace : studySpaces){

                    studySpace.setDistanceFromCurrentPosition(distances.get(position));
                    System.out.println(studySpace.getDistanceFromCurrentPosition());
                    position++;
                }

                // Sort by waling distance, if distance same, sort by name
                studySpaces.sort((studySpaceOne, studySpaceTwo) -> {
                    int distanceComparison = Double.compare(studySpaceOne.getDistanceFromCurrentPosition(), studySpaceTwo.getDistanceFromCurrentPosition());
                    if (distanceComparison != 0) {
                        return distanceComparison;
                    } else {
                        return studySpaceOne.getName().compareTo(studySpaceTwo.getName());
                    }
                });

                return studySpaces;

            } else {
                System.out.println("Get data failed, code is: " + responseCode + ". Please try again.");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<StudySpace>();
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

//    Location getStudySpaceLocation(int favouriteId, ReviewType type) throws Exception;

}