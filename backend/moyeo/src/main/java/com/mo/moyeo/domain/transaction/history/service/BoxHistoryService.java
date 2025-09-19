package com.mo.moyeo.domain.transaction.history.service;

import com.mo.moyeo.domain.fcm.service.FcmMessagingService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.repository.BoxHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxHistoryService {

    private final BoxHistoryRepository boxHistoryRepository;
    private final FcmMessagingService fcmMessagingService;

    public void saveExchangeHistory(BoxHistory boxHistory1, BoxHistory boxHistory2) {

        boxHistoryRepository.save(boxHistory1);
        boxHistoryRepository.save(boxHistory2);

        fcmMessagingService.sendExchangeMessage(boxHistory1, boxHistory2);
    }

    public void saveHistory(BoxHistory boxHistory){

        boxHistoryRepository.save(boxHistory);

        fcmMessagingService.sendTransactionMessage(boxHistory);
    }

    public void saveHistoryWithoutNotification(BoxHistory boxHistory){

        boxHistoryRepository.save(boxHistory);
    }
}