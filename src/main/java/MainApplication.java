public class MainApplication {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Uuid uuid = new Uuid();
            Thread.sleep(3000);
            System.out.println(uuid);
        }
    }
}
