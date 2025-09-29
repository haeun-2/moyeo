package com.mo.moyeo.domain.transaction.history.service;

import com.mo.moyeo.domain.fcm.service.FcmMessagingService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.repository.BoxHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxHistoryService {

    private final BoxHistoryRepository boxHistoryRepository;
    private final FcmMessagingService fcmMessagingService;

    @Transactional
    public void saveExchangeHistory(BoxHistory boxHistory1, BoxHistory boxHistory2) {

        boxHistoryRepository.save(boxHistory1);
        boxHistoryRepository.save(boxHistory2);

        try {
            fcmMessagingService.sendExchangeMessage(boxHistory1, boxHistory2);
        }  catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void saveHistory(BoxHistory boxHistory){

        boxHistoryRepository.save(boxHistory);

        try {
            fcmMessagingService.sendTransactionMessage(boxHistory);
        }  catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void saveHistoryWithoutNotification(BoxHistory boxHistory){

        boxHistoryRepository.save(boxHistory);
    }
}