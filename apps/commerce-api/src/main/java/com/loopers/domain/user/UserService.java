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
    public User signUp(UserCommand command) {
        // validation existsByUserId
        String userId = command.userId();
        if (existsByUserId(userId)) {
            throw new IllegalArgumentException("User already exists with userId: " + userId);
        }

        // command -> domain
        User user = UserCommand.toDomain(command);
        // repository
        return userRepository.save(user);
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public User getMyInfo(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

}
