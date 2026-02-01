package io.github.mrergos.controller;

import io.github.mrergos.service.UserSecurityService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record SecurityController(UserSecurityService userSecurityService) {
    public void handleSession(@NotNull Context context) {
        String login = context.sessionAttribute("currentUser");
        if (login == null) {
            throw new UnauthorizedResponse("To upload files login is required\nДля загрузки файлов требуется вход в систему");
        }
    }

    public void handleLogout(@NotNull Context context) {
        context.req().getSession().invalidate();
    }

    public void handleAuth(@NotNull Context context) {
        String login = context.formParam("login");
        String password = context.formParam("password");

        if (userSecurityService.authUser(login, password)) {
            context.sessionAttribute("currentUser", login);
            context.sessionAttribute("userId", userSecurityService.findUserByLogin(login).getId());
            context.status(HttpStatus.OK).result("Success");
        } else {
            throw new UnauthorizedResponse("Invalid login or password\nНеверный логин или пароль");
        }
    }

    public void handleRegistration(@NotNull Context context) {
        String login = context.formParam("login");
        String password = context.formParam("password");

        if (userSecurityService.userExists(login)) {
            throw new BadRequestResponse("User is already exist\nПользователь уже существует");
        } else {
            Long userId = userSecurityService.registerUser(login, password);
            context.sessionAttribute("currentUser", login);
            context.sessionAttribute("userId", userId);
            context.status(HttpStatus.OK).result("Success");
        }
    }

    public void handleGetCurrentUser(@NotNull Context context) {
        String login = context.sessionAttribute("currentUser");

        if (login != null) {
            context.json(Map.of("login", login));
        } else {
            context.status(HttpStatus.UNAUTHORIZED);
        }
    }
}
