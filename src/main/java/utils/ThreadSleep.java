package utils;

public final class ThreadSleep {
    private ThreadSleep() {}

    public  static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            //ignore this exception
        }
    }
}
