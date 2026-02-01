package io.github.mrergos.service;

import io.github.mrergos.entity.User;

public interface UserSecurityService {
    boolean authUser(String login, String password);

    Long registerUser(String login, String password);

    boolean userExists(String login);

    User findUserByLogin(String login);
}
