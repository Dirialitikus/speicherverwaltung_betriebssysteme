


public class HalProcessor extends Thread{
    private HalInterpreter interpreter = null;
    private IOComponent[] IOComponents;


    public HalProcessor(String path, int numOfComponents) {
        setUpIOComponents(numOfComponents);
        interpreter = new HalInterpreter(path, false, IOComponents);
    }

    public void setUpIOComponents(int numOfComponents) {
        IOComponents = new IOComponent[numOfComponents];
        for(int i = 0; i < numOfComponents; i++) {
            if(i < 2) {
                IOComponents[i] = new IOStandardComponent();
                continue;
            }
            IOComponents[i] = new IOProcessorComponent();
        }
    }

    public void setIOComponentBuffer(Buffer buffer, int componentIndex) {
        ((IOProcessorComponent) IOComponents[componentIndex]).setBuffer(buffer);
    }

    public void run() {
        interpreter.start();
    }
}
