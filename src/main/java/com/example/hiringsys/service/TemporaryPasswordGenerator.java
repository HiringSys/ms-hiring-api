package com.example.hiringsys.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TemporaryPasswordGenerator {

    private static final int PASSWORD_LENGTH = 16;
    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIAL = "!@#$%*-_";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private final SecureRandom secureRandom = new SecureRandom();

    public String gerar() {
        char[] password = new char[PASSWORD_LENGTH];
        password[0] = randomCharacter(UPPERCASE);
        password[1] = randomCharacter(LOWERCASE);
        password[2] = randomCharacter(DIGITS);
        password[3] = randomCharacter(SPECIAL);

        for (int index = 4; index < password.length; index++) {
            password[index] = randomCharacter(ALL_CHARACTERS);
        }
        shuffle(password);
        return new String(password);
    }

    private char randomCharacter(String characters) {
        return characters.charAt(secureRandom.nextInt(characters.length()));
    }

    private void shuffle(char[] characters) {
        for (int index = characters.length - 1; index > 0; index--) {
            int target = secureRandom.nextInt(index + 1);
            char current = characters[index];
            characters[index] = characters[target];
            characters[target] = current;
        }
    }
}
