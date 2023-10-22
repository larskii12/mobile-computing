package com.example.mainactivity.service.favorite;

import com.example.mainactivity.models.favorite.Favorite;
import com.example.mainactivity.models.review.ReviewType;

import java.util.List;

public interface FavoriteService {
    Favorite addFavorite(Integer userId, Integer entityId, ReviewType type) throws Exception;

    Favorite getFavorite(int favouriteId, ReviewType type) throws Exception;

    List<Favorite> getFavoritesByUser(int userId, ReviewType type) throws Exception;

    Boolean isFavoriteByUser(int userId, int entityId, ReviewType type) throws Exception;
}
