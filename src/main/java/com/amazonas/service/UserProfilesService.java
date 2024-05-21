package com.amazonas.service;

import com.amazonas.business.userProfiles.UsersController;
import org.springframework.stereotype.Component;

@Component
public class UserProfilesService {

    private final UsersController controller;

    public UserProfilesService(UsersController userProxy) {
        this.controller = userProxy;
    }


}
