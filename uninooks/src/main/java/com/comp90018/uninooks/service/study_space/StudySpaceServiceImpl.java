package com.comp90018.uninooks.service.study_space;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StudySpaceServiceImpl implements StudySpaceService {

    Connection connector = new DatabaseHelper().getConnector();
//
//    /**
//     * Get ten closest study spaces and return to the Main UI to show
//     *
//     * @param location as the current location
//     * @return ten sorted closest study spaces by walking distance
//     * @throws Exception if any exception happens
//     */
//    @Override
//    public ArrayList<StudySpace> getClosestStudySpaces(LatLng location, int size) throws Exception {
//
//        try {
//            String query = "SELECT study_space_id, study_space_building_id FROM mobilecomputing.\"study_space\"";
//
//            PreparedStatement preparedStatement = connector.prepareStatement(query);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            List<StudySpace> allStudySpaces = Collections.synchronizedList(new ArrayList<>());
//            ArrayList<StudySpace> openingStudySpaces = new ArrayList<>();
//            ArrayList<StudySpace> closingStudySpaces = new ArrayList<>();
//
//
//            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//            ArrayList<Integer> studySpacedIds = new ArrayList<>();
//
//            while (resultSet.next()){
//                studySpacedIds.add(Integer.parseInt(resultSet.getString("study_space_id")));
//            }
//
//            for (Integer studySpaceId: studySpacedIds) {
//                executorService.submit(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        StudySpace studySpace = null;
//                        try {
//                            studySpace = new LocationServiceImpl().findStudySpaceById(studySpaceId);
//                            if (studySpace != null) {
//                                allStudySpaces.add(studySpace);
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//            }
//
//            executorService.shutdown();
//            try {
//                if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
//                    executorService.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                executorService.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//
//            synchronized (allStudySpaces){
//                allStudySpaces.sort(Comparator.comparingDouble(StudySpace::getDistanceFromCurrentPosition));
//            }
//
//            // Show the opening study space first
//            for (StudySpace studySpace : allStudySpaces) {
//
//                if (studySpace.isOpeningNow()) {
//                    openingStudySpaces.add(studySpace);
//                } else {
//                    closingStudySpaces.add(studySpace);
//                }
//            }
//
//            openingStudySpaces.addAll(closingStudySpaces);
//
//            if (openingStudySpaces.size() <= size) {
//
//                return openingStudySpaces;
//            }
//
//            return new ArrayList<>(openingStudySpaces.subList(0, size));
//        }
//
//        catch (Exception e) {
//            e.printStackTrace();
//            throw new Exception();
//        }
//
//        finally {
//            if (connector != null) {
//                try {
//                    connector.close();
//                } catch (Exception e) {
//                    System.out.println("Database Connection close failed.");
//                }
//            }
//        }
//    }
//
//    /**
//     * Get top rated study spaces
//     * @param location as current location
//     * @param size as wanted size
//     * @return top rated study spaces
//     * @throws Exception if any exception happens
//     */
//    @Override
//    public ArrayList<StudySpace> getTopRatedStudySpaces(LatLng location, int size) throws Exception {
//
//        try{
//            String query = "SELECT review_study_space_id, ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " +
//                    "FROM mobilecomputing.\"review\" " +
//                    "WHERE review_study_space_id IS NOT NULL " +
//                    "GROUP BY review_study_space_id " +
//                    "ORDER BY average_rating DESC;";
//
//            PreparedStatement preparedStatement = connector.prepareStatement(query);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            List<StudySpace> allStudySpaces = Collections.synchronizedList(new ArrayList<>());
//            ArrayList<StudySpace> openingStudySpaces = new ArrayList<>();
//            ArrayList<StudySpace> closingStudySpaces = new ArrayList<>();
//
//
//            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//            ArrayList<Integer> studySpacedIds = new ArrayList<>();
//
//            while (resultSet.next()){
//                studySpacedIds.add(Integer.parseInt(resultSet.getString("review_study_space_id")));
//            }
//
//            for (Integer studySpaceId: studySpacedIds) {
//                executorService.submit(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        StudySpace studySpace = null;
//                        try {
//                            studySpace = new LocationServiceImpl().findStudySpaceById(studySpaceId);
//                            if (studySpace != null) {
//                                allStudySpaces.add(studySpace);
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//            }
//
//            executorService.shutdown();
//            try {
//                if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
//                    executorService.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                executorService.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//
//            for (StudySpace studySpace : allStudySpaces) {
//
//                if (studySpace.isOpeningNow()) {
//                    openingStudySpaces.add(studySpace);
//                } else {
//                    closingStudySpaces.add(studySpace);
//                }
//            }
//
//            openingStudySpaces.addAll(closingStudySpaces);
//
//            if (openingStudySpaces.size() <= size) {
//
//                return openingStudySpaces;
//            }
//
//            return new ArrayList<>(openingStudySpaces.subList(0, size));
//        }
//
//        catch (Exception e){
//            e.printStackTrace();
//            throw new Exception();
//        }
//
//        finally {
//            if (connector != null) {
//                try {
//                    connector.close();
//                } catch (Exception e) {
//                    System.out.println("Database Connection close failed.");
//                }
//            }
//        }
//    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Google Distance Matrix API, long time waiting and high failure, not suggest using, since inside University is all direct pathway.

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /**
//     * Get the sorted closest ten study spaces by walking distance from current location
//     * @param currentLocation as current location
//     * @param studySpaces as ten closest study spaces
//     * @return sorted closest ten study spaces by walking distance from current location
//     * @throws IOException if exception happens
//     */
//    public ArrayList<StudySpace> calculateSpaceByWalkingDistance(LatLng currentLocation, ArrayList<StudySpace> studySpaces) throws IOException {
//
//        try {
//
//            String origin = currentLocation.latitude + "," + currentLocation.longitude;
//
//            StringBuilder destination = new StringBuilder();
//
//            for (StudySpace studySpace : studySpaces) {
//
//                destination.append(studySpace.getLocation().latitude).append(",").append(studySpace.getLocation().longitude).append("|");
//                System.out.println(studySpace.getName());
//            }
//
//            InputStream inputStream = MainActivity.getAppContext().getResources().openRawResource(R.raw.config);
//            Properties properties = new Properties();
//            properties.load(inputStream);
//
//            // Distance Matrix query URL
//            String requestURL = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
//                    "origins=" + origin + "&" +
//                    "destinations=" + destination + "&" +
//                    "mode=" + "walking" + "&" +
//                    "key=" + properties.getProperty("API_KEY");
//
//            URL url = new URL(requestURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            int responseCode = connection.getResponseCode();
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//
//                // output as json format and print in JSON
//                ObjectMapper mapper = new ObjectMapper();                             //
//                Object json = mapper.readValue(response.toString(), Object.class);  //
//                String jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);  //
//                Log.d("API RESPONSE", jsonStr);     //
//
//
//                // Extract response, add the distance to study space object.
//
//                ArrayList<Double> distances = getDistanceFromJson(response);
//
//                int position = 0;
//
//                // Iterate each study space and set the distance to current location
//                for (StudySpace studySpace : studySpaces){
//
//                    studySpace.setDistanceFromCurrentPosition(distances.get(position));
//                    position++;
//                }
//
//                connection.disconnect();
//                return studySpaces;
//
//            } else {
//                System.out.println("Get data failed, code is: " + responseCode + ". Please try again.");
//            }
//
//            connection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        finally {
//            if (connector != null) {
//                try {
//                    connector.close();
//                } catch (Exception e) {
//                    System.out.println("Database Connection close failed.");
//                }
//            }
//        }
//
//        return new ArrayList<StudySpace>();
//    }
//
//    /**
//     * Sort study spaces by distances
//     * @param studySpaces as study spaces
//     * @return distance sorted study spaces
//     */
//    private ArrayList<StudySpace> sortByDistance(ArrayList<StudySpace> studySpaces){
//        // Sort by walking distance, if distance same, sort by name
//        studySpaces.sort((studySpaceOne, studySpaceTwo) -> {
//            int distanceComparison = Double.compare(studySpaceOne.getDistanceFromCurrentPosition(), studySpaceTwo.getDistanceFromCurrentPosition());
//            if (distanceComparison != 0) {
//                return distanceComparison;
//            } else {
//                return studySpaceOne.getName().compareTo(studySpaceTwo.getName());
//            }
//        });
//
//        return studySpaces;
//    }
//
//
//    /**
//     * Extract distance informatin from Google API returned json response
//     * @param response Google API response Json
//     * @return arraylist of distance
//     * @throws JSONException if any exception happens
//     */
//    private ArrayList<Double> getDistanceFromJson(StringBuffer response) throws JSONException {
//
//        ArrayList<Double> distances = new ArrayList<>();
//
//        // Convert response JSON to respond String
//        String responseInString = response.toString();
//        JSONObject jsonObject = new JSONObject(responseInString);
//
//        // Get response json content
//
//        JSONArray responseElementsArray = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
//
//        for (int i = 0; i < responseElementsArray.length(); i++) {
//
//            JSONObject element = new JSONObject(responseElementsArray.get(i).toString());
//
//            if (element.getString("status").equals("OK")) {
//
//                // Add to the distance arraylist
//                distances.add(Double.parseDouble(element.getJSONObject("distance").getString("value")));
//            }
//
//            // If no data fetched or path is unavailable
//            else{
//                distances.add(-1.0);
//            }
//        }
//        return distances;
//    }

//    Location getStudySpaceLocation(int favouriteId, ReviewType type) throws Exception;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





    /**
     * Get ten closest study spaces and return to the Main UI to show
     *
     * @return ten sorted closest study spaces by walking distance
     * @throws Exception if any exception happens
     */
    public ArrayList<StudySpace> getAllStudySpaces() throws Exception {

        try {
            String query = "SELECT study_space_id, study_space_building_id FROM mobilecomputing.\"study_space\"";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<StudySpace> allStudySpaces = Collections.synchronizedList(new ArrayList<>());
            ArrayList<StudySpace> openingStudySpaces = new ArrayList<>();
            ArrayList<StudySpace> closingStudySpaces = new ArrayList<>();


            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            ArrayList<Integer> studySpacedIds = new ArrayList<>();

            while (resultSet.next()) {
                studySpacedIds.add(Integer.parseInt(resultSet.getString("study_space_id")));
            }

            for (Integer studySpaceId : studySpacedIds) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        StudySpace studySpace = null;
                        try {
                            studySpace = new LocationServiceImpl().findStudySpaceById(studySpaceId);
                            if (studySpace != null) {
                                allStudySpaces.add(studySpace);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }

            return new ArrayList<>(allStudySpaces);

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
}