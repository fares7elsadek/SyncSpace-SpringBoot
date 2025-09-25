package com.fares7elsadek.syncspace.user.api.controller;

import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.user.application.Mapper.UserMapper;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import com.fares7elsadek.syncspace.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAccessService userService;
    private final UserMapper userMapper;
    @PostMapping("/userInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public String userInfo(@RequestBody UserDto userDto){
        userService.saveUser(userDto);
        System.out.println("User saved successfully");
        return "User saved successfully";
    }

    @GetMapping("/userData")
    public ResponseEntity<ApiResponse<UserDto>> userData(){
        var user = userMapper.toUserDto(userService.getCurrentUserInfo());
        return ResponseEntity.ok(ApiResponse.success("User info",user));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> userInfo(@PathVariable String userId){
        var user = userMapper.toUserDto(userService.getUserInfo(userId));
        return ResponseEntity.ok(ApiResponse.success("User info",user));
    }
}
