package io.github.mrergos.service;

public interface UserSecurityService {
    boolean authUser(String login, String password);

    void registerUser(String login, String password);

    boolean userExists(String login);
}
