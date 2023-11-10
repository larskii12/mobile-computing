package com.comp90018.uninooks.service.library;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.library.Library;
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

public class LibraryServiceImpl implements LibraryService {

    Connection connector = new DatabaseHelper().getConnector();

    /**
     * Get ten closest libraries and return to the Main UI to show
     *
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

            List<Library> allLibraries = Collections.synchronizedList(new ArrayList<>());
            ArrayList<Library> openingLibraries = new ArrayList<>();
            ArrayList<Library> closingLibraries = new ArrayList<>();

            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            ArrayList<Integer> libraryIds = new ArrayList<>();

            while (resultSet.next()) {
                libraryIds.add(Integer.parseInt(resultSet.getString("library_id")));
            }

            for (Integer libraryId : libraryIds) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        Library library = null;
                        try {
                            library = new LocationServiceImpl().findLibraryById(libraryId);
                            if (library != null) {
                                allLibraries.add(library);
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

            synchronized (allLibraries) {
                allLibraries.sort(Comparator.comparingDouble(Library::getDistanceFromCurrentPosition));
            }

            for (Library library : allLibraries) {
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
        } catch (Exception e) {
            throw new Exception();
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
     * Get top rated libraries
     *
     * @param location as current location
     * @param size     as size
     * @return top rated sorted libraries
     * @throws Exception if any exceptions
     */
    @Override
    public ArrayList<Library> getTopRatedLibraries(LatLng location, int size) throws Exception {

        try {
            String query = "SELECT review_library_id, ROUND(SUM(review_score)::decimal/COUNT(*), 1) as average_rating " + "FROM mobilecomputing.\"review\" " + "WHERE review_library_id IS NOT NULL " + "GROUP BY review_library_id " + "ORDER BY average_rating DESC;";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Library> allLibraries = Collections.synchronizedList(new ArrayList<>());
            ArrayList<Library> openingLibraries = new ArrayList<>();
            ArrayList<Library> closingLibraries = new ArrayList<>();


            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            ArrayList<Integer> libraryIds = new ArrayList<>();

            while (resultSet.next()) {
                libraryIds.add(Integer.parseInt(resultSet.getString("review_library_id")));
            }

            for (Integer libraryId : libraryIds) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        Library library = null;
                        try {
                            library = new LocationServiceImpl().findLibraryById(libraryId);
                            if (library != null) {
                                allLibraries.add(library);
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

            for (Library library : allLibraries) {
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
        } catch (Exception e) {
            throw new Exception();
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
//}

}
