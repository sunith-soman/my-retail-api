package com.myretail.pricing.dao;

import com.myretail.pricing.dto.UserDTO;

/**
 * @author Sunith Soman
 */
public interface UserDao {
    public UserDTO getUserByUserName(String userName);
}
