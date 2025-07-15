package com.loopers.application.user;

import com.loopers.domain.user.User;

public record UserCommand(
        String userId,
        String gender,
        String birth,
        String email
) {

    public static User toDomain(UserCommand command) {
        return User.create(
                command.userId,
                command.gender,
                command.birth,
                command.email
        );
    }

}
