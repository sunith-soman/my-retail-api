package com.myretail.myretailapi.dao;

import com.myretail.myretailapi.dto.UserDTO;

/**
 * @author Sunith Soman
 */
public interface UserDao {
    public UserDTO getUserByUserName(String userName);
}
