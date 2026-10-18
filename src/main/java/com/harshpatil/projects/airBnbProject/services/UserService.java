package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.SignupDto;
import com.harshpatil.projects.airBnbProject.dto.UserDto;
import com.harshpatil.projects.airBnbProject.entity.User;
import com.harshpatil.projects.airBnbProject.entity.enums.Role;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(()->new BadCredentialsException("user with email : "+username+" not found"));

    }

    public User getUserById(Long id)
    {
        return  userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user not found with id : "+id));
    }

    public UserDto signUp(SignupDto signupDto)
    {
        Optional<User> user=userRepository.findByEmail(signupDto.getEmail());

        if(user.isPresent())
        {
            throw new BadCredentialsException("User with email already exists");
        }

        User toBeCreate=modelMapper.map(signupDto,User.class);
        toBeCreate.setRoles(Set.of(Role.GUEST));
        toBeCreate.setPassword(passwordEncoder.encode(signupDto.getPass()));

        toBeCreate=userRepository.save(toBeCreate);

        return modelMapper.map(toBeCreate,UserDto.class);
    }
}
