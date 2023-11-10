package com.comp90018.uninooks.service.study_space;

import com.comp90018.uninooks.models.location.study_space.StudySpace;

import java.util.ArrayList;

public interface StudySpaceService {
    ArrayList<StudySpace> getAllStudySpaces() throws Exception;

}