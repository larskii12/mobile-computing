package com.comp90018.uninooks.service.favorite;

import com.comp90018.uninooks.models.favorite.Favorite;
import com.comp90018.uninooks.models.review.ReviewType;

import java.util.List;

public interface FavoriteService {
    Favorite addFavorite(Integer userId, Integer entityId, ReviewType type) throws Exception;

    Favorite getFavorite(int favouriteId, ReviewType type) throws Exception;

    boolean removeFavorite(Integer userId, Integer entityId, ReviewType type) throws Exception;

    List<Favorite> getFavoritesByUser(int userId, ReviewType type) throws Exception;
}
