package com.example.eusersso.controller;

import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "用户信息的管理"
)
public class AdminController {

    private final UserService userService;

    @PostMapping("/user/create")
    @Operation(summary = "创建用户")
    public R<String> createUser(
            @RequestBody @Valid NewUserDto userDto,
            @RequestHeader("User") String whoAmI
    ) {
        EuserDao euserDao = new EuserDao();
        euserDao.setUsername(userDto.getUsername());
        euserDao.setPassword(userDto.getPassword());
        euserDao.setChecked(userDto.isChecked());
        euserDao.setCreatedBy(whoAmI);
        euserDao.setNote(userDto.getNote());
        int result = userService.insertOne(euserDao);
        if (result >= 1) {
            return R.ok("success");
        }
        return new R<>(R.CODE_ERROR, "插入失败，请稍后尝试", "fail");
    }
}
