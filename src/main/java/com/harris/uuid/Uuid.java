package com.harris.uuid;

/**
 * time-based version-1 UUID
 */
public class Uuid extends TimeBasedUuid {

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
        byte[] clockSeq = getClockSequence();
        lbs = (lbs << 6) | (clockSeq[0] & 0x3f);
        lbs = (lbs << 6) | (clockSeq[1] & 0xff);

        //node id
        byte[] nodeId = getNodeId();

        for (int i = 0; i < 6; i++) {
            lbs = (lbs << 8) | (nodeId[i] & 0xff);
        }

        leastSigBits = lbs;
    }
}
