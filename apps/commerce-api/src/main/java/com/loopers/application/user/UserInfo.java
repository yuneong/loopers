package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;

public record UserInfo(
        String userId,
        Gender gender,
        String birth,
        String email
) {

    public static UserInfo from(User user) {
        return new UserInfo(
                user.getUserId(),
                user.getGender(),
                user.getBirth(),
                user.getEmail()
        );
    }

}
