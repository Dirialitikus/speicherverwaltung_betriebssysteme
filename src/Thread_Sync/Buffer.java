
public class Buffer {
    private boolean available = false;
    private double data;

    public synchronized void put(double x) {
        while(available) {
            try {
                wait();
            }
            catch(InterruptedException e) {}
        }
        data = x;
        available = true;
        notify();
    }

    public synchronized double get() {
        while(!available) {
            try {
                wait();
            }
            catch(InterruptedException e) {}
        }
        available = false;
        notify();
        return data;
    }
}
