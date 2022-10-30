package com.harris.uuid;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: dce security version2 UUID with local domain and identifier
 * @author: KunQi Yu
 * @date: 2022-10-30 13:01
 **/
public class DceSecurityUUID extends TimeBasedUuid {

    private static final byte[] domain = new byte[]{24};
    private static final AtomicInteger counter= new AtomicInteger(0);

    /**
     * tick every 429.49 seconds
     */
    private static final long TICK_PER_DAY = 24 * 60 * 60 * 1000 / 429490;

    public DceSecurityUUID(){
        version2();
    }

    private void version2(){
        long mbs = 0;

        byte[] identifier = identifierFromLocalDomain();
        //32 bit identifier within domain
        for (int i = 0; i < 4; i++) {
            mbs = (mbs << 8) | (identifier[i] & 0xff);
        }

        //consider the timestamp to be a 28-bit value
        long timestampPart = getTs() & 0xfffffffffffffffL;

        //time_mid
        mbs = (mbs << 16) | ((timestampPart >>> 32) & 0xffffL);

        //version(4 bit)
        byte version = 2;
        mbs = (mbs << 4) | version;

        //time_hi
        mbs = (mbs << 12) | ((timestampPart >>> 48) & 0xfffL);

        mostSigBits = mbs;

        long lbs = 0;

        // set to IETF variant
        lbs |= 0x8;

        //local domain
        byte[] localDomain = getLocalDomain();
        for (int i = 0; i < 1; i++) {
            lbs = (lbs << 6) | (localDomain[i] & 0xff);
        }

        //clock sequence consider to be a 6-bit value
        byte[] clockSeq = getClockSequence();
        lbs = (lbs << 6) | (clockSeq[0] & 0x3f);


        //node id
        byte[] nodeId = getNodeId();

        for (int i = 0; i < 6; i++) {
            lbs = (lbs << 8) | (nodeId[i] & 0xff);
        }

        leastSigBits = lbs;
    }


    /**
     * 8 bit local domain
     * @return
     */
    protected byte[] getLocalDomain(){
        return domain;
    }

    /**
     * 32 bit meaningful id within local domain
     * @return
     */
    protected byte[] identifierFromLocalDomain(){
        int identifierWithinDomain = counter.incrementAndGet();
        return ByteBuffer.allocate(4).putInt(identifierWithinDomain).array();
    }

    @Override
    protected long getTs() {
        //fixme 考虑算术溢出的情况
        LocalDateTime end = LocalDateTime.now();
        // day part
        long dayPart = end.toLocalDate().toEpochDay() - original.toLocalDate().toEpochDay();
        // nano seconds part within a day
        long timePart = end.toLocalTime().toNanoOfDay() - original.toLocalTime().toNanoOfDay();

        dayPart = Math.multiplyExact(dayPart,TICK_PER_DAY);
        timePart = (timePart / 1000_000L) / TICK_PER_DAY;

        return Math.addExact(dayPart, timePart);
    }
}
