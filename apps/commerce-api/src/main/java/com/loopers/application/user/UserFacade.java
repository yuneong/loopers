package com.loopers.application.user;

import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;

    public UserInfo signUp(UserCommand command) {
        // service
        User user = userService.signUp(command);

        // create Point for new user
        pointService.create(user.getUserId());

        // domain -> result
        return UserInfo.from(user);
    }

    public UserInfo getMyInfo(String userId) {
        // service
        User user = userService.getMyInfo(userId);
        // domain -> result
        return UserInfo.from(user);
    }

}
