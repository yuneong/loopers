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
        User user = User.create(command.userId(), command.gender(), command.birth(), command.email());
        // repository
        return userRepository.save(user);
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public User getMyInfo(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new NullPointerException("User not found with userId: " + userId));
    }

}
