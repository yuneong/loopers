package com.loopers.application.user;

import com.loopers.domain.user.User;

public record UserCommand(
        String gender,
        String birth,
        String email
) {

    public static User toDomain(String userId, UserCommand command) {
        return User.save(
                userId,
                command.gender,
                command.birth,
                command.email
        );
    }

}
