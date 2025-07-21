package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;

public record UserCommand(
        String userId,
        Gender gender,
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
