package com.fares7elsadek.syncspace.user.controller;

import com.fares7elsadek.syncspace.user.api.UserAccessService;
import com.fares7elsadek.syncspace.user.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAccessService userService;
    @PostMapping("/userInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public String userInfo(@RequestBody UserDto userDto){
        userService.saveUser(userDto);
        System.out.println("User saved successfully");
        return "User saved successfully";
    }
}
