package com.hubex.learningsystem.security.logic.serviceImpl;

import com.hubex.learningsystem.security.logic.service.AuthenticationService;
import com.hubex.learningsystem.security.models.entities.RoleEntity;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.enums.Roles;
import com.hubex.learningsystem.security.models.repositories.RoleRepository;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import com.hubex.learningsystem.security.models.requests.LoginRequest;
import com.hubex.learningsystem.security.models.requests.SignupRequest;
import com.hubex.learningsystem.security.models.responses.JwtResponse;
import com.hubex.learningsystem.security.models.responses.MessageResponse;
import com.hubex.learningsystem.security.principal.UserPrincipal;
import com.hubex.learningsystem.security.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthenticationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                                     AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public MessageResponse registerUser(SignupRequest newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return new MessageResponse("Error: Username is already taken!", "ERROR");
        }

        UserEntity user = new UserEntity(newUser.getEmail(),
                passwordEncoder.encode(newUser.getPassword()));

        Set<String> stringRoles = newUser.getRole();
        Set<RoleEntity> roles = new HashSet<>();

        if(stringRoles == null) {
            RoleEntity studentRole = roleRepository.findByName(Roles.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(studentRole);
        } else {
            stringRoles.forEach(role -> {
                switch (role) {
                    case "teacher":
                        RoleEntity teacherRole = roleRepository.findByName(Roles.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(teacherRole);
                        break;
                    default:
                        RoleEntity studentRole = roleRepository.findByName(Roles.ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(studentRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully", "SUCCESS");
    }

    @Override
    public JwtResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return new JwtResponse(jwt, principal.getId(), principal.getUsername(), roles);
    }
}
