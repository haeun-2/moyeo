package com.mo.moyeo.domain.transaction.payment.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.finance_api.AccountUtil;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
import com.mo.moyeo.domain.box.balance.service.BoxBalanceService;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.merchant.entity.Merchant;
import com.mo.moyeo.domain.merchant.service.MerchantService;
import com.mo.moyeo.domain.transaction.bank.dto.BankTransferDTO;
import com.mo.moyeo.domain.transaction.bank.service.BankApiService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import com.mo.moyeo.domain.transaction.payment.dto.PaymentRequestDto;
import com.mo.moyeo.domain.transaction.payment.dto.TokenResponse;
import com.mo.moyeo.domain.transaction.payment.entity.Payment;
import com.mo.moyeo.domain.transaction.payment.repository.PaymentRepository;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.user.entity.User;
import com.mo.moyeo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TransactionService transactionService;
    private final BoxMemberService boxMemberService;
    private final BoxBalanceService boxBalanceService;
    private final MerchantService merchantService;
    private final AccountUtil accountUtil;
    private final BankApiService bankApiService;
    private final RedisTemplate<String, String> redisTemplate;
    private final BoxService boxService;
    private final BoxHistoryService boxHistoryService;
    private final UserService userService;

    // 랜덤 시드 생성
    private static final SecureRandom random = new SecureRandom();
    private static final String TOKEN_BOX_KEY = "TOKEN_BOX_KEY";
    private static final String TOKEN_USER_KEY = "TOKEN_USER_KEY";

    public TokenResponse getQrCode(User user, Long boxId) {
        log.debug("user :{} , box : {}",user.getId(), boxId);
        validateCondition(user, boxId);
        String token ;
        try {
            token = generateToken(user, boxId);
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return new TokenResponse(token);
    }

    private void validateCondition(User user, Long boxId) {
        boxMemberService.validatePaymentPermission(boxService.getBoxById(boxId), user);
    }

    // 토큰 생성
    public String generateToken(User user, Long boxId) throws NoSuchAlgorithmException {
        byte[] salt = new byte[8]; // 8바이트 랜덤
        random.nextBytes(salt);

        String input = boxId + ":" + Base64.getEncoder().encodeToString(salt);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

        // 매핑 저장
        String tokenBoxKey = TOKEN_BOX_KEY + ":" + token; // 예: "payment_tokens:abc123"
        redisTemplate.opsForValue().set(tokenBoxKey, String.valueOf(boxId), Duration.ofSeconds(30));

        String tokenUserKey = TOKEN_USER_KEY + ":" + token; // 예: "payment_tokens:abc123"
        redisTemplate.opsForValue().set(tokenUserKey, String.valueOf(user.getId()), Duration.ofSeconds(30));
        return token;
    }

    // 토큰에서 원본 값 확인
    public Long getTokenBoxId(String token) {
        String tokenKey = TOKEN_BOX_KEY + ":" + token; // 예: "payment_tokens:abc123"
        String value = redisTemplate.opsForValue().get(tokenKey);

        if(value==null)throw new CustomException(ErrorCode.EXPIRED_PAYMENT_TOKEN);
        return Long.parseLong(value); // DB에서 조회 시 사용
    }

    public Long getTokenUserId(String token) {
        String tokenKey = TOKEN_USER_KEY + ":" + token; // 예: "payment_tokens:abc123"
        String value = redisTemplate.opsForValue().get(tokenKey);

        if(value==null)throw new CustomException(ErrorCode.EXPIRED_PAYMENT_TOKEN);
        return Long.parseLong(value); // DB에서 조회 시 사용
    }

    @Transactional
    public void payment(PaymentRequestDto paymentRequestDto) {
        Long boxId = getTokenBoxId(paymentRequestDto.token());
        Long userId = getTokenUserId(paymentRequestDto.token());
        User user = userService.getById(userId);

        //박스 찾기
        Box box = boxService.getReferenceById(boxId);
        BoxBalance boxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(box, paymentRequestDto.currencyType());
        boxMemberService.validatePaymentPermission(box, user);

        if(boxBalance.getBalance().compareTo(paymentRequestDto.amount())<0)
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        boxBalance.decreaseBalance(paymentRequestDto.amount());

        Transaction transaction = transactionService.makePaymentTransaction(box,user);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String approvalNumber = LocalDateTime.now().format(formatter) + UUID.randomUUID();

        //merchant 조회
        Merchant merchant = merchantService.getMerchantById(paymentRequestDto.merchantId());
        Payment payment = Payment.builder()
                .transaction(transaction)
                .merchant(merchant)
                .approvalNumber(approvalNumber)
                .amount(paymentRequestDto.amount())
                .status(Payment.Status.PENDING)
                .build();
        payment = paymentRepository.save(payment);

        BoxHistory boxHistory = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(paymentRequestDto.amount())
                .currencyCode(paymentRequestDto.currencyType())
                .totalAmount(boxBalance.getBalance())
                .type(Transaction.Type.PAYMENT)
                .title(merchant.getName())
                .category(merchant.getCategory())
                .createdAt(transaction.getCreatedAt())
                .build();
        boxHistoryService.saveHistory(boxHistory);

        BankTransferDTO bankTransferDTO = BankTransferDTO.builder()
                .withdrawalAccountNo(accountUtil.getAccountByType(paymentRequestDto.currencyType()))
                .depositAccountNo(merchant.getSettlementAccount())
                .depositTransactionSummary("모여")
                .transactionBalance(paymentRequestDto.amount())
                .build();

        try{
            bankApiService.paymentTransfer(paymentRequestDto.currencyType(), bankTransferDTO);
        } catch (Exception e) {
            log.debug(e.getMessage());
            payment.paymentFailed();
            payment.updateCompletedAt();
            throw e;
        }
        payment.paymentSuccess();
        payment.updateCompletedAt();
    }
}