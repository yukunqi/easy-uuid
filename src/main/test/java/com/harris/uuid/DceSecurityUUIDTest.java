package com.harris.uuid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @description:
 * @author: KunQi Yu
 * @date: 2022-10-30 14:30
 **/
class DceSecurityUUIDTest {


    @Test
    void generate_DCE_security_UUID(){
        DceSecurityUUID dceSecurityUUID = new DceSecurityUUID();
        Assertions.assertDoesNotThrow(()-> UUID.fromString(dceSecurityUUID.toString()));
    }


    @Test
    void versionTest(){
        DceSecurityUUID dceSecurityUUID = new DceSecurityUUID();
        UUID fromString = UUID.fromString(dceSecurityUUID.toString());
        Assertions.assertEquals(2,fromString.version());
    }

    @Test
    void variantTest(){
        DceSecurityUUID dceSecurityUUID = new DceSecurityUUID();
        UUID fromString = UUID.fromString(dceSecurityUUID.toString());
        //all using IETF variant
        UUID randomUUID = UUID.randomUUID();
        Assertions.assertEquals(randomUUID.variant(),fromString.variant());
    }
}