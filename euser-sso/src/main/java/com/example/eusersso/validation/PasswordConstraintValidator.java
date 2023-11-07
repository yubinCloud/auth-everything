package com.example.eusersso.validation;

import com.example.eusersso.validation.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.passay.spring.SpringMessageResolver;

import java.util.Arrays;

/**
 * 对参数中用户密码的格式校验
 */
@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword,String> {

    private final SpringMessageResolver springMessageResolver; // 用于 passay 的国际化

    static public final int MIN_PASSWORD_LENGTH = 8;  // 密码最短长度要求
    static public final int MAX_PASSWORD_LENGTH = 30;  // 最长要求

    @Override
    public void initialize(final ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(final String password, ConstraintValidatorContext constraintValidatorContext) {
        var validator = new PasswordValidator(springMessageResolver, Arrays.asList(
                new LengthRule(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH),
//                new CharacterRule(EnglishCharacterData.UpperCase, 1),  // 至少有一个大写字母
                new CharacterRule(EnglishCharacterData.LowerCase, 1)  // 至少一个小写字母
//                new CharacterRule(EnglishCharacterData.Special, 1)     // 至少一个特殊字符
        ));
        var result = validator.validate(new PasswordData(password));
        // 修改默认的参数校验 message 为 passay 的 message
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(String.join("", validator.getMessages(result)))
                .addConstraintViolation();
        return result.isValid();
    }
}
