package com.harris.uuid.namebased;

/**
 * @description:
 * @author: KunQi Yu
 * @date: 2022-11-15 10:00
 **/
public class NameBasedUuid {

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


    protected NameSpace nameSpace;


    public NameBasedUuid(NameSpace nameSpace) {
        this.nameSpace = nameSpace;
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
