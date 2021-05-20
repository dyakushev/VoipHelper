package ru.bia.voip.vc.util;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;


public class RandomPasswordGenerator {
    @Value("${cucm.user.password.length}")
    private Integer passwordLength;
    @Value("${cucm.user.password.mincodepoint}")
    private Integer minimumCodePoint;
    @Value("${cucm.user.password.maxcodepoint}")
    private Integer maximumCodePoint;

    public String generateRandomSpecialCharacters() {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(minimumCodePoint, maximumCodePoint)
                .build();
        return pwdGenerator.generate(passwordLength);
    }
}
