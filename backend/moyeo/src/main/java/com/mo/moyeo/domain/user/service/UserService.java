package com.mo.moyeo.domain.user.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.EncryptionUtil;
import com.mo.moyeo.domain.bank.service.AccountVerificationService;
import com.mo.moyeo.domain.box.box.service.BoxApplicationService;
import com.mo.moyeo.domain.user.dto.AccountChangeRequest;
import com.mo.moyeo.domain.user.dto.ChangeAccountVerificationRequest;
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
    private final BoxApplicationService boxApplicationService;
    private final EncryptionUtil encryptionUtil;
    private final AccountVerificationService accountVerificationService;

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

    /**
     * 유저 등록
     */
    @Transactional
    public void registerUser(User user) {
        User newUser = userRepository.save(user);
        boxApplicationService.createPersonalBox(newUser);
    }

    /**
     * 금융망 유저 키 복호화
     */
    private String getUserKey(User user) {
        String encryptedUserKey = user.getConnectedBankKey();

        return encryptionUtil.decrypt(encryptedUserKey);
    }

    /**
     * 1원 코드 전송
     */
    public void sendWonCodeForChange(User user, AccountChangeRequest request) {
        String userKey = getUserKey(user);
        accountVerificationService.sendWonCode(userKey, request.getBankAccount());
    }

    /**
     * 1원 코드 인증 및 계좌 변경
     */
    @Transactional
    public void verifyWonCodeForChange(User user, ChangeAccountVerificationRequest request) {
        String userKey = getUserKey(user);
        accountVerificationService.verifyWonCode(userKey, request.getBankAccount(), request.getVerificationCode());

        user.updateAccount(request.getBankCode(), request.getBankAccount());

        userRepository.save(user);
    }
}
