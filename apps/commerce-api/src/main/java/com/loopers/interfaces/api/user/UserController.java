package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCommand;
import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping("")
    @Override
    public UserV1Dto.UserResponse singUp(@RequestBody UserV1Dto.UserRequest request) {
        // request -> command
        UserCommand command = UserV1Dto.UserRequest.toCommand(request);
        // service
        UserInfo userInfo = userFacade.signUp(command);
        // result -> response
        return UserV1Dto.UserResponse.from(userInfo);
    }

}
