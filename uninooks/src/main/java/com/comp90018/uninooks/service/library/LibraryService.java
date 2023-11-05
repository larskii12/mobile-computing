package com.comp90018.uninooks.service.library;

import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.models.location.study_space.StudySpace;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface LibraryService {

    ArrayList<Library> getClosestLibraries(LatLng location, int size) throws Exception;

    ArrayList<Library> getTopRatedLibraries(LatLng location, int size) throws Exception;
}