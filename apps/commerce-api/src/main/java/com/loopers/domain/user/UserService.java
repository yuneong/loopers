package com.loopers.domain.user;

import com.loopers.application.user.UserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User signUp(String userId, UserCommand command) {
        // command -> domain
        User user = UserCommand.toDomain(userId, command);
        // repository
        return userRepository.save(user);
    }

}
