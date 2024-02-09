package threads;

public class TwoThreads {

    public void printNumbers() {
        Thread evenThread = new Thread(new EvenNumbers());
        Thread oddThread = new Thread(new OddNumbers());

        evenThread.start();
        oddThread.start();
    }
}

class EvenNumbers implements Runnable {
    @Override
    public void run() {
        for (int i = 2; i <= 60; i += 2) {
            System.out.println("Even: " + i);
        }
    }
}

class OddNumbers implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 60; i += 2) {
            System.out.println("Odd: " + i);
        }
    }
}
