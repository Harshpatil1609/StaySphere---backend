package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.dto.LoginDto;
import com.harshpatil.projects.airBnbProject.dto.LoginResponseDto;
import com.harshpatil.projects.airBnbProject.dto.SignupDto;
import com.harshpatil.projects.airBnbProject.dto.UserDto;
import com.harshpatil.projects.airBnbProject.security.AuthService;
import com.harshpatil.projects.airBnbProject.security.JwtService;
import com.harshpatil.projects.airBnbProject.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupDto signupDto)
    {
        return new ResponseEntity<>(userService.signUp(signupDto), HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    public ResponseEntity<LoginResponseDto> signIn(@RequestBody LoginDto loginDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        String[] tokens=authService.signIn(loginDto);

        Cookie cookie=new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse)
    {
        Cookie[] cookies=httpServletRequest.getCookies();

        String refreshToken=null;
        for(Cookie cookie:cookies)
        {
            if("refreshToken".equals(cookie.getName()))
            {
                refreshToken=cookie.getValue();
            }
        }

        if(refreshToken == null)
        {
            throw new RuntimeException("Refresh token not found");
        }

        log.info("We are in refresh token");
        LoginResponseDto loginResponseDto=authService.getRefreshToken(refreshToken);

        return ResponseEntity.ok( loginResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<@NonNull String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse)
    {
        Cookie[] cookies=httpServletRequest.getCookies();

        String refreshToken=null;

        if(cookies !=null)
        {
            for (Cookie cookie:cookies)
            {
                if("refreshToken".equals(cookie.getName()))
                {
                    refreshToken=cookie.getValue();
                    break;
                }
            }
        }

        Cookie cookie=new Cookie("refreshToken",null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok("Logout successfully");
    }
}
