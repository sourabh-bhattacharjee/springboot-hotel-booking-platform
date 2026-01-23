package com.sourabh.projects.airbnbcloneapp.security;

import com.sourabh.projects.airbnbcloneapp.dto.LoginDto;
import com.sourabh.projects.airbnbcloneapp.dto.SignUpRequestDto;
import com.sourabh.projects.airbnbcloneapp.dto.UserDto;
import com.sourabh.projects.airbnbcloneapp.entity.User;
import com.sourabh.projects.airbnbcloneapp.entity.enums.Role;
import com.sourabh.projects.airbnbcloneapp.repository.UserRepository;
import com.sourabh.projects.airbnbcloneapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto){

        User user = userRepository.findUserByEmail(signUpRequestDto.getEmail()).orElse(null);
        if(user != null){
            throw new RuntimeException("User already exists");
        }

        User newUser = modelMapper.map(signUpRequestDto,User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);
        return modelMapper.map(newUser,UserDto.class);
    }

    public String[] login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));

        User user = (User) authentication.getPrincipal();

        String arr[] = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateFreshToken(user);

        return arr;

    }

    public String refresh(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.getUserById(userId);
        String newAccessToken = jwtService.generateAccessToken(user);
        return newAccessToken;
    }
}
