package com.mo.moyeo.domain.exchange.reservation.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveDto;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveListDto;
import com.mo.moyeo.domain.exchange.reservation.service.ReservedExchangeService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchanges/reservations")
public class ReservedExchangeController {
    private final ReservedExchangeService reservedExchangeService;

    @PostMapping
    public ResponseEntity<?> reserveExchange(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ExchangeReserveDto exchangeReserveDto) {
        User user = customUserDetails.getUser();
        reservedExchangeService.reserveExchange(user, exchangeReserveDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boxId}")
    public ResponseEntity<List<ExchangeReserveListDto>> getReservations(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long boxId){
        User user = customUserDetails.getUser();
        return ResponseEntity.ok(reservedExchangeService.getReservations(user, boxId));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long reservationId
    ){
        User user = customUserDetails.getUser();
        reservedExchangeService.cancelReservation(user, reservationId);
        return ResponseEntity.ok().build();
    }
}
