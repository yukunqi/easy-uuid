import java.net.NetworkInterface;

public class MainApplication {

    public static void main(String[] args) {
        // 1000 0000 0000 0101 -> 1111 1111 1111 1011
        // 1000 0101 -> 1111 1011
        byte a = 1;
        System.out.println(Integer.toBinaryString(a & 0xff));
        System.out.println(Integer.toBinaryString(a & 0xff));

        long l1 = 0x8;
        long l2 = 0x80;

        System.out.println(l1);
        System.out.println(l2);


        mac();

        Uuid id = new Uuid();
        System.out.println(id);
    }

    public static void mac() {
        try {

            NetworkInterface eth0 = NetworkInterface.getByName("en0");
            byte[] hardwareAddress = eth0.getHardwareAddress();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
