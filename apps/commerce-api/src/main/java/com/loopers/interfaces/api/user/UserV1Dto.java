package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCommand;
import com.loopers.application.user.UserInfo;

public class UserV1Dto {

    public record UserRequest(
            String gender,
            String birth,
            String email
    ) {

        public static UserCommand toCommand(UserRequest request) {
            return new UserCommand(
                    request.gender,
                    request.birth,
                    request.email
            );
        }

    }

    public record UserResponse(
            String userId,
            String gender,
            String birth,
            String email
    ) {

        public static UserResponse from(UserInfo info) {
            return new UserResponse(
                    info.userId(),
                    info.gender(),
                    info.birth(),
                    info.email()
            );
        }

    }

}
