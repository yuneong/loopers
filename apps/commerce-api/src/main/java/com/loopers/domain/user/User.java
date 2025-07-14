package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    private String userId;
    private String gender;
    private String birth;
    private String email;

    private String REGEX_USER_ID = "^[a-zA-Z0-9]{1,10}$";
    private String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static User save(String userId, String gender, String birth, String email) {
        User user = new User();

        // 유효성 검사
        user.validateUserId(userId);
        user.validateBirth(birth);
        user.validateEmail(email);

        user.userId = userId;
        user.gender = gender;
        user.birth = birth;
        user.email = email;

        return user;
    }

    public void validateUserId(String userId) {
        if (userId == null || !userId.matches(REGEX_USER_ID)) {
            throw new IllegalArgumentException("유저 아이디는 영문 및 숫자 10자 이내여야 합니다.");
        }
    }

    public void validateBirth(String birth) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthDate = LocalDate.parse(birth, formatter);

            if(!birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("생일은 오늘보다 미래의 날짜일 수 없습니다.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("생일 형식은 yyyy-MM-dd 이어야 합니다.");
        }
    }

    public void validateEmail(String email) {
        if (!email.matches(REGEX_EMAIL)) {
            throw new IllegalArgumentException("이메일 형식은 xx@yy.zz 이어야 합니다.");
        }
    }

}
