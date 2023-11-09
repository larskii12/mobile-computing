package com.comp90018.uninooks.service.image;

import com.comp90018.uninooks.R;

import java.util.HashMap;

public class ImageServiceImpl implements ImageService {
    HashMap<String, Integer> imageToLocation;

    public ImageServiceImpl() {
        imageToLocation = retrieveAllImages();
    }

    private HashMap<String, Integer> retrieveAllImages() {
        HashMap<String, Integer> images = new HashMap<>();

        images.put("BAILLIEU LIBRARY", R.drawable.baillieu_lib);
        images.put("Baillieu Library After Hours Study Zone", R.drawable.baillieu_lib);
        images.put("ERC LIBRARY", R.drawable.erc);
        images.put("ERC Library After Hours Study Zone", R.drawable.erc);
        images.put("BROWNLESS BIOMEDICAL LIBRARY", R.drawable.brownless);
        images.put("Brownless Biomedical Library After Hours Study Zone", R.drawable.brownless);
        images.put("Architecture, Building and Planning Library", R.drawable.msd);
        images.put("Alan Gilbert G02 & G26", R.drawable.alan_gilbert);
        images.put("FBE Building 215 & 227", R.drawable.fbe);
        images.put("FBE Learning Space", R.drawable.fbe);
        images.put("The Spot Learning Space", R.drawable.the_spot);
        images.put("Kwong Lee Dow Learning Space", R.drawable.kwong_lee_dow);
        images.put("100 Leicester Street 103", R.drawable.leicester);
        images.put("Arts West Learning Space", R.drawable.arts_west);
        images.put("Old Arts 207A, 210A", R.drawable.old_arts);
        images.put("Glyn Davis Learning Space", R.drawable.msd);
        images.put("Western Edge Biosciences Learning Space", R.drawable.western_edge);
        images.put("Arts and Culture Building Learning Space", R.drawable.arts_cultural);
        images.put("Student Pavilion Learning Space", R.drawable.student_pav);
        images.put("Block D Study Space", R.drawable.block_d);
        images.put("Old Metallurgy Study Space", R.drawable.old_metallurgy);
        images.put("Old Engineering Study Space", R.drawable.old_engineering);

        return images;
    }

    /**
     * Fetches the image linked to the location name
     * @param locationName name of the location to be fetched
     * @return resource ID
     * @throws Exception
     */
    @Override
    public int fetchImage(String locationName) throws Exception {
        return imageToLocation.get(locationName);
    }

    /**
     * Fetch all location images
     * @return hashmap linking image to each location
     * @throws Exception
     */
    @Override
    public HashMap<String, Integer> fetchAllImages() throws Exception {
        return imageToLocation;
    }
}
