package com.scu.ds.dfs.dfscoordinator.controller;

import com.scu.ds.dfs.dfscoordinator.model.LoginRequest;
import com.scu.ds.dfs.dfscoordinator.model.User;
import com.scu.ds.dfs.dfscoordinator.service.UserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final PasswordEncoder encoder;

    @PostMapping("/signup")
    @ApiResponses(@ApiResponse(code = 200, response = User.class, message = ""))
    public ResponseEntity signup(@RequestBody User user) {
        if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())) {
            throw new BadRequestException("username, password, name are required fields");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        User createdUser = userService.createProfile(user);

        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        if (StringUtils.isBlank(loginRequest.getUsername())
                || StringUtils.isBlank(loginRequest.getPassword())) {
            throw new BadRequestException("username, password are required fields");
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = this.authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
