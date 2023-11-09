package com.comp90018.uninooks.service.image;

import java.util.HashMap;

public interface ImageService {
    int fetchImage(String locationName) throws Exception;
    HashMap<String, Integer> fetchAllImages() throws Exception;
}
