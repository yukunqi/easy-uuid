package com.harris.uuid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.UUID;

/**
 * @description:
 * @author: KunQi Yu
 * @date: 2022-10-27 22:29
 **/
class UuidTest {


    @Test
    void PE_generate_one_timeBased_uuid(){
        Uuid uuid = new Uuid();
        Assertions.assertDoesNotThrow(()->UUID.fromString(uuid.toString()));
    }

    @Test
    void versionTest(){
        Uuid uuid = new Uuid();
        UUID fromString = UUID.fromString(uuid.toString());
        Assertions.assertEquals(1,fromString.version());
    }

    @Test
    void variantTest(){
        Uuid uuid = new Uuid();
        UUID fromString = UUID.fromString(uuid.toString());
        //all using IETF variant
        UUID randomUUID = UUID.randomUUID();
        Assertions.assertEquals(randomUUID.variant(),fromString.variant());
    }

    @Test
    void NodeTest_should_equal_IEEE_Mac_address() throws SocketException {
        Uuid uuid = new Uuid();
        UUID fromString = UUID.fromString(uuid.toString());

        //get MacAddress
        NetworkInterface en0 = NetworkInterface.getByName("en0");
        byte[] hardwareAddress = en0.getHardwareAddress();

        Assertions.assertEquals(6,hardwareAddress.length);

        long expected = 0;

        for (int i = 0; i < 6; i++) {
            expected = (expected << 8) | (hardwareAddress[i] & 0xff);
        }

        Assertions.assertEquals(expected,fromString.node());
    }

    @Test
    void timestampTest() throws NoSuchMethodException, InterruptedException {
        Uuid uid = new Uuid();

        long tsBefore = (long) ReflectionUtils.invokeMethod(TimeBasedUuid.class.getDeclaredMethod("getTs"), uid);

        Thread.sleep(1);

        long tsAfter = (long) ReflectionUtils.invokeMethod(TimeBasedUuid.class.getDeclaredMethod("getTs"), uid);

        System.out.println(tsAfter - tsBefore);
        Assertions.assertTrue((tsAfter - tsBefore) >= 10 * 1000);
    }
}