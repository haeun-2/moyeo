package com.mo.moyeo.domain.user.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.service.BoxService;
import com.mo.moyeo.domain.user.entity.User;
import com.mo.moyeo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BoxService boxService;

    public void validateUserEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 이메일입니다.");
        }
    }

    public void validateUserPhoneNumberNotExists(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 전화번호입니다.");
        }
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 유저를 찾을 수 없습니다."));
    }

    public User getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 전화번호로 유저를 찾을 수 없습니다."));
    }

    @Transactional
    public void registerUser(User user) {
        User newUser = userRepository.save(user);
        boxService.createPersonalBox(newUser);
    }
}
