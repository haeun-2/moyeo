package com.mo.moyeo.domain.box.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.box.repository.BoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxService {

    private final BoxRepository boxRepository;

    public Box getBoxById(Long boxId){
        return boxRepository.findById(boxId)
                .orElseThrow(()-> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }

    public Box getBoxWithBalanceById(Long boxId){
        return boxRepository.findWithBalancesById(boxId)
                .orElseThrow(()-> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }

    public Box getPersonalBoxByUserId(Long userId){
        return boxRepository.findPersonalBoxByOwnerId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }

    public Box getReferenceById(Long boxId){
        return boxRepository.getReferenceById(boxId);
    }

    public Long getOwnerIdById(Long boxId) {
        return boxRepository.findOwnerIdByBoxId(boxId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }

    @Transactional
    public Box save(Box box) {
        return boxRepository.save(box);
    }

}
