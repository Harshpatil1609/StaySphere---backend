package com.harshpatil.projects.airBnbProject.security;

import com.harshpatil.projects.airBnbProject.dto.LoginDto;
import com.harshpatil.projects.airBnbProject.dto.LoginResponseDto;
import com.harshpatil.projects.airBnbProject.entity.User;
import com.harshpatil.projects.airBnbProject.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String[] signIn(LoginDto loginDto)
    {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPass())
        );

        User user=(User) authentication.getPrincipal();

        String[] arr = new String[2];
        arr[0] = jwtService.generateAssessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public LoginResponseDto getRefreshToken(String refreshToken)
    {
        Long id=jwtService.getUserIdFromToken(refreshToken);

        User user=userService.getUserById(id);

        String accessToken=jwtService.generateAssessToken(user);

        return new LoginResponseDto(accessToken);
    }
}
