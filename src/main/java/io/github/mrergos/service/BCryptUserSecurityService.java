package io.github.mrergos.service;

import io.github.mrergos.dao.UserDao;
import io.github.mrergos.entity.User;
import io.javalin.http.BadRequestResponse;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptUserSecurityService implements UserSecurityService {
    private final UserDao userDao;

    public BCryptUserSecurityService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean authUser(String login, String password) {
        User user = userDao.findUserByLogin(login);
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }

    @Override
    public Long registerUser(String login, String password) {
        if (login.isBlank() || login.length() > 15 || !login.matches("^[a-zA-Z0-9]+$")) {
            throw new BadRequestResponse("Login must contain only Alphabet symbols or digits and be from 1 to 15 characters\nЛогин должен содержать только буквы или цифры и состоять из 1–15 символов.");
        } else {
            if (!password.matches("^[a-zA-Z0-9%$*]{6,20}$")) {
                throw new BadRequestResponse("Password must contain only Alphabet symbols or digits or Special symbols and be from 6 to 20 characters.\n Пароль должен содержать только буквы, цифры или специальные символы и состоять из 6–20 символов.");
            }
            String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt());
            return userDao.save(new User(login, hashedPassword));
        }
    }

    @Override
    public boolean userExists(String login) {
        return userDao.existsByLogin(login);
    }

    @Override
    public User findUserByLogin(String login) {
        return userDao.findUserByLogin(login);
    }
}
