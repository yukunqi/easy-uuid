package com.harris.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Enumeration;

public class Uuid {
    /*
     * The most significant 64 bits of this UUID.
     *
     * @serial
     */
    private long mostSigBits = 0L;

    /*
     * The least significant 64 bits of this UUID.
     *
     * @serial
     */
    private long leastSigBits = 0L;

    static long NANOS_PER_DAY = 24 * 60 * 60 * 100_000_000L;

    private static final LocalDateTime original = LocalDateTime.of(1582, Month.OCTOBER, 15, 0, 0, 0, 0);

    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    public Uuid() {
        version1();
    }

    public void version1() {
        long mbs = 0;
        //consider the timestamp to be a 60-bit value
        long timestampPart = getTs() & 0xfffffffffffffffL;

        //time_low
        mbs |= (timestampPart & 0xffffffffL);

        //time_mid
        mbs = (mbs << 16) | ((timestampPart >>> 32) & 0xffffL);

        //version(4 bit)
        byte version = 1;
        mbs = (mbs << 4) | version;

        //time_hi
        mbs = (mbs << 12) | ((timestampPart >>> 48) & 0xfffL);

        mostSigBits = mbs;

        long lbs = 0;

        // set to IETF variant
        lbs |= 0x8;
        //clock sequence consider to be a 14-bit value
        byte[] clockSeq = getRandomClockSequence();
        lbs = (lbs << 6) | (clockSeq[0] & 0x3f);
        lbs = (lbs << 6) | (clockSeq[1] & 0xff);

        //node id
        byte[] nodeId = getNodeId();

        for (int i = 0; i < 6; i++) {
            lbs = (lbs << 8) | (nodeId[i] & 0xff);
        }

        leastSigBits = lbs;
    }

    private long getTs() {
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

    private byte[] getRandomClockSequence() {
        SecureRandom ng = Uuid.Holder.numberGenerator;

        byte[] randomBytes = new byte[2];
        ng.nextBytes(randomBytes);

        return randomBytes;
    }


    private byte[] getNodeId() {
        //todo 如何正确获取Mac 地址
        // 1.可能有的系统 并没有网卡 2.不同的系统的网卡名称各不相同
        try {
            NetworkInterface networkInterface = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
            }

            if (networkInterface == null) {
                //random get 6 bytes while not find NetworkInterface
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
