package com.harris.uuid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
}