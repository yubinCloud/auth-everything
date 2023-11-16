package com.example.eusersso.controller;

import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dto.request.UpdateEuserDto;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.EuserService;
import com.example.eusersso.util.ConstantUtil;
import com.example.eusersso.util.PasswordEncoder;
import com.example.eusersso.util.TimestampUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/common")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "【common】用户信息的管理"
)
public class AdminCommonController {

    private final EuserService euserService;

    private final EuserConverter euserConverter;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/user/update")
    @Operation(summary = "更新用户信息")
    public R<String> updateUser(
            @RequestBody @Valid UpdateEuserDto updateDto,
            @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI
    ) {
        var userDao = euserConverter.toEuserDao(updateDto);
        userDao.setPassword(passwordEncoder.encode(userDao.getPassword()));
        userDao.setLastUpdatedIuser(whoAmI);
        userDao.setLastUpdatedTime(TimestampUtil.now());
        int updateResult = euserService.updateUser(userDao);
        if (updateResult == 0) {
            return R.error("更新失败", null);
        }
        return R.ok("update success");
    }

    @DeleteMapping("/user/{username}")
    @Operation(summary = "删除用户")
    public R<String> deleteUser(@PathVariable("username") @NotBlank String username) {
        int deleteResult = euserService.deleteByUsername(username);
        if (deleteResult == 0) {
            return R.error("删除失败", null);
        }
        return R.ok("success");
    }
}
