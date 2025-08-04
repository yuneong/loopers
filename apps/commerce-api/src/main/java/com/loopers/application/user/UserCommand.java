package com.loopers.application.user;

import com.loopers.domain.user.Gender;

public record UserCommand(
        String userId,
        Gender gender,
        String birth,
        String email
) {

}
