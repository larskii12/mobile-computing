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
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            ArrayList<Integer> studySpacedIds = new ArrayList<>();

            while (resultSet.next()) {
                studySpacedIds.add(Integer.parseInt(resultSet.getString("study_space_id")));
            }

            for (Integer studySpaceId : studySpacedIds) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        StudySpace studySpace;
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