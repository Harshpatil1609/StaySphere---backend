package com.harshpatil.projects.airBnbProject.security;

import com.harshpatil.projects.airBnbProject.entity.User;
import com.harshpatil.projects.airBnbProject.services.UserService;
import jakarta.persistence.Column;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor

public class JwtAuthFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader=request.getHeader("Authorization");

        if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer"))
        {
            filterChain.doFilter(request,response);
            return;
//            it move the request ti next filter
        }

        String token=requestTokenHeader.substring(7);


        try {
            String tokenType=jwtService.getTokenType(token);

            if(!"access".equals(tokenType))
            {
                filterChain.doFilter(request,response);
                return;
            }

            Long userId=jwtService.getUserIdFromToken(token);
            System.out.println(userId);

            if(userId !=null && SecurityContextHolder.getContext().getAuthentication()==null)
            {
//                check user is present in the db authentication wowoek here
                User user=userService.getUserById(userId);

//                authentication Object
//                check if the user should be allowed
                UsernamePasswordAuthenticationToken authenticationToken=
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request,response);
        }
        catch (Exception e)
        {
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
