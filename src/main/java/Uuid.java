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
        //timestamp(time_low 32 time_mid 16 time_hi 12 = 60 bit)
        long timestamp_part = getTs();
        mbs |= timestamp_part & 0xfffffffffffffffL;

        //version(4 bit)
        byte version = 1;
        mbs = (mbs << 4) | version;

        mostSigBits = mbs;

        long lbs = 0;

        // set to IETF variant
        lbs |= 0x8;
        //clock sequence
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
            NetworkInterface en0 = NetworkInterface.getByName("en0");
            return en0.getHardwareAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
