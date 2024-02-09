package threads;

public class Deadlock {

    public static void main(String[] args) {
        final String resource1 = "resource1";
        final String resource2 = "resource2";

        // Thread 1
        Thread t1 = new Thread(() -> {
            synchronized (resource1) {
                System.out.println("Thread 1: Holding resource 1...");

                try { Thread.sleep(100);}
                catch (InterruptedException e) {}

                System.out.println("Thread 1: Waiting for resource 2...");

                synchronized (resource2) {
                    System.out.println("Thread 1: Holding resource 1 & 2...");
                }
            }
        });

        // Thread 2
        Thread t2 = new Thread(() -> {
            synchronized (resource2) {
                System.out.println("Thread 2: Holding resource 2...");

                try { Thread.sleep(100);}
                catch (InterruptedException e) {}

                System.out.println("Thread 2: Waiting for resource 1...");

                synchronized (resource1) {
                    System.out.println("Thread 2: Holding resource 2 & 1...");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
