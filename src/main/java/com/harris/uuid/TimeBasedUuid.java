package com.harris.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Enumeration;

/**
 * @description:
 * @author: KunQi Yu
 * @date: 2022-10-30 13:30
 **/
public abstract class TimeBasedUuid {

    /*
     * The most significant 64 bits of this UUID.
     *
     * @serial
     */
    protected long mostSigBits = 0L;

    /*
     * The least significant 64 bits of this UUID.
     *
     * @serial
     */
    protected long leastSigBits = 0L;

    protected static final LocalDateTime original = LocalDateTime.of(1582, Month.OCTOBER, 15, 0, 0, 0, 0);
    static long NANOS_PER_DAY = 24 * 60 * 60 * 1000_000_000L;

    protected static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    protected long getTs() {
        //fixme 考虑算术溢出的情况 RFC4122 round A.D. 3400, depending on the specific algorithm
        LocalDateTime end = LocalDateTime.now();
        // day part
        long dayPart = end.toLocalDate().toEpochDay() - original.toLocalDate().toEpochDay();
        // nano seconds part within a day
        long timePart = end.toLocalTime().toNanoOfDay() - original.toLocalTime().toNanoOfDay();

        dayPart = Math.multiplyExact(dayPart, NANOS_PER_DAY / 100);
        timePart = timePart / 100;

        return Math.addExact(dayPart, timePart);
    }

    protected byte[] getClockSequence() {
        SecureRandom ng = Uuid.Holder.numberGenerator;

        byte[] randomBytes = new byte[2];
        ng.nextBytes(randomBytes);

        return randomBytes;
    }

    protected byte[] getNodeId() {

        try {
            NetworkInterface networkInterface = lookupForNetworkInterface();
            //fixme the first lsb of the first msb in nodeId set to one
            if (networkInterface == null || networkInterface.getHardwareAddress() == null) {
                //random get 6 bytes while not find NetworkInterface mac address
                SecureRandom ng = Uuid.Holder.numberGenerator;
                byte[] randomBytes = new byte[6];
                ng.nextBytes(randomBytes);
                return randomBytes;
            }

            return networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private NetworkInterface lookupForNetworkInterface(){
        //todo 如何正确获取Mac 地址
        // 1.可能有的系统 并没有网卡 2.不同的系统的网卡名称各不相同
        NetworkInterface networkInterface = null;
        try {
            //first
            networkInterface = NetworkInterface.getByName("en0");
            if (networkInterface != null){
                return networkInterface;
            }

            //second
            networkInterface = NetworkInterface.getByName("eth0");
            if (networkInterface != null){
                return networkInterface;
            }

            //find any one
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
            }

            return networkInterface;
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return (digits(mostSigBits >> 32, 8) + "-" +
                digits(mostSigBits >> 16, 4) + "-" +
                digits(mostSigBits, 4) + "-" +
                digits(leastSigBits >> 48, 4) + "-" +
                digits(leastSigBits, 12));
    }

    /**
     * Returns val represented by the specified number of hex digits.
     */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

}
