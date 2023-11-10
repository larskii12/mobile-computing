package com.comp90018.uninooks.service.user;

import com.comp90018.uninooks.models.user.User;

public interface UserService {

    User logIn(String userNameOrEmail, String userPassword) throws Exception;

    boolean addUser(String userName, String userEmail, String userPassword, String userFaculty, int userAQFLevel) throws Exception;

    boolean deleteUser(int userId) throws Exception;

    User getUser(int userId) throws Exception;

    boolean updateUserName(int userId, String newUserName) throws Exception;

    boolean updateUserEmail(int userId, String newUserEmail) throws Exception;

    boolean updateUserPassword(int userId, String oldUserPassword, String newUserPassword) throws Exception;

    boolean updateUserFaculty(int userId, String newUserFaculty) throws Exception;

    boolean updateUserAQFLevel(int userId, int newAQFLevel) throws Exception;


    boolean resetUserPassword(String userNameOrEmail, String newUserPassword) throws Exception;

}
