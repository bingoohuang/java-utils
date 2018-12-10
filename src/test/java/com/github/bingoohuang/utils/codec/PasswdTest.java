package com.github.bingoohuang.utils.codec;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PasswdTest {
    @Test
    public void testBcrypt() {
        String originalPassword = "password";
        String generatedSecuredPasswordHash = Passwd.bcrypt(originalPassword);
        System.out.println(generatedSecuredPasswordHash);

        boolean matched = Passwd.bcryptMatch(originalPassword, generatedSecuredPasswordHash);
        assertThat(matched, is(true));
    }

    @Test
    public void testPbkdf2() {
        String originalPassword = "password";
        String generatedSecuredPasswordHash = Passwd.pbkdf2(originalPassword, "some");
        System.out.println(generatedSecuredPasswordHash);

        boolean matched = Passwd.pbkdf2Match(originalPassword, generatedSecuredPasswordHash);
        assertThat(matched, is(true));
    }

    @Test
    public void testSha() {
        String originalPassword = "password";
        String generatedSecuredPasswordHash = Passwd.sha512(originalPassword, "some");
        System.out.println(generatedSecuredPasswordHash);


        System.out.println(Passwd.salt());
    }
}
