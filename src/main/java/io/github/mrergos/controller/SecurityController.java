package io.github.mrergos.controller;

import io.github.mrergos.service.UserSecurityService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public record SecurityController(UserSecurityService userSecurityService) {
    public void handleSession(@NotNull Context context) {
        String login = context.sessionAttribute("currentUser");
        if (login == null) {
            throw new UnauthorizedResponse("To upload files login is required");
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
            context.status(HttpStatus.OK).result("Success");
        } else {
            throw new UnauthorizedResponse("Invalid login or password");
        }
    }

    public void handleRegistration(@NotNull Context context) {
        String login = context.formParam("login");
        String password = context.formParam("password");

        if (userSecurityService.userExists(login)) {
            throw new BadRequestResponse("User is already exist");
        } else {
            userSecurityService.registerUser(login, password);
            context.sessionAttribute("currentUser", login);
            context.status(HttpStatus.OK).result("Success");
        }
    }
}
