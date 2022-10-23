import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

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
        long timestamp_part = getTs() & 0xfffffffffffffffL;

        //time_low
        mbs |= (timestamp_part & 0xffffffffL);

        //time_mid
        mbs = (mbs << 16) | ((timestamp_part >>> 32) & 0xffffL);

        //version(4 bit)
        byte version = 1;
        mbs = (mbs << 4) | version;

        //time_hi
        mbs = (mbs << 12) | ((timestamp_part >>> 48) & 0xfffL);

        mostSigBits = mbs;

        long lbs = 0;

        // set to IETF variant
        lbs |= 0x8;
        //clock sequence consider to be a 14-bit value
        byte[] clock_seq = getRandomClockSequence();
        lbs = (lbs << 6) | (clock_seq[0] & 0x3f);
        lbs = (lbs << 6) | (clock_seq[1] & 0xff);

        //node id
        byte[] nodeId = getNodeId();

        for (int i = 0; i < 6; i++) {
            lbs = (lbs << 8) | (nodeId[i] & 0xff);
        }

        leastSigBits = lbs;
    }

    private long getTs() {
        //fixme 暂时使用微秒(1000纳秒) 单位来代替 rfc 4122 中的时间戳的间隔单位 100纳秒
        return original.until(LocalDateTime.now(), ChronoUnit.MICROS);
    }

    private byte[] getRandomClockSequence() {
        SecureRandom ng = Uuid.Holder.numberGenerator;

        byte[] randomBytes = new byte[2];
        ng.nextBytes(randomBytes);

        return randomBytes;
    }


    private byte[] getNodeId() {
        try {
            //todo 如何正确获取Mac 地址
            // 1.可能有的系统 并没有网卡 2.不同的系统的网卡名称各不相同
            NetworkInterface en0 = NetworkInterface.getByName("en0");
            return en0.getHardwareAddress();
        } catch (Exception e) {
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
