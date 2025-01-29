package eberware.api;

import eberware.api.core.systems.services.PasswordService;

public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return PasswordService.encode(rawPassword.toString()).get_content();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return PasswordService.matches(rawPassword.toString(), encodedPassword);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return org.springframework.security.crypto.password.PasswordEncoder.super.upgradeEncoding(encodedPassword);
    }
}
