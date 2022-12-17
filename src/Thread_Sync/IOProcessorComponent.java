


public class IOProcessorComponent extends IOComponent{
    Buffer buffer = null;

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    public double getInput() {
        return buffer.get();
    }

    public void giveOutput() {
        buffer.put(value);
    }
}
