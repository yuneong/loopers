package com.loopers.application.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {

    private final UserService userService;

    public UserInfo signUp(String userId, UserCommand command) {
        // service
        User user = userService.signUp(userId, command);
        // domain -> result
        return UserInfo.from(user);
    }

}
