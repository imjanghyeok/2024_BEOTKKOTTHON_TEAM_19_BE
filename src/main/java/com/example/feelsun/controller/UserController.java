package com.example.feelsun.controller;

import com.example.feelsun.config.jwt.JwtProvider;
import com.example.feelsun.config.utils.ApiResponseBuilder;
import com.example.feelsun.request.UserRequest.*;
import com.example.feelsun.response.UserResponse.*;
import com.example.feelsun.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserSignUpRequest requestDTO) {
        userService.signup(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.successWithNoContent());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest requestDTO, Errors errors, HttpServletResponse response) {
        UserLoginResponseWithToken loginDTO = userService.login(requestDTO);

        String accessToken = JwtProvider.TOKEN_PREFIX + loginDTO.getAccessToken();

        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        Cookie cookie = new Cookie("refresh-token", loginDTO.getRefreshToken());
        cookie.setHttpOnly(true); // JS를 통한 접근 방지
        cookie.setPath("/"); // 쿠키를 전송할 경로
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(loginDTO.getLoginResponseDTO()));
    }
}
