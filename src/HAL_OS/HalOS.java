

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HalOS {

    ArrayList<HalProcessor> processors = new ArrayList<HalProcessor>();

    public static void main(String[] args) {
        HalOS os = new HalOS();
        os.setUpProcessors();
    }

    public void setUpProcessors() {
        HalProcessor newProcessor;
        int numOfIOComponents = 0;
        String line = "";
        String[] lineSplit;
        Boolean connectionsReached = false;
        File config = new File("OS-config.txt");
        try {
            Buffer threadBuffer;
            FileReader reader = new FileReader(config);
            BufferedReader buffReader = new BufferedReader(reader);
            numOfIOComponents = Integer.parseInt(buffReader.readLine());
            buffReader.readLine();
            while((line = buffReader.readLine()) != null) {
                if(!connectionsReached){
                    if(line.equals("HAL-Verbindungen:")) {
                        connectionsReached = true;
                        continue;
                    }
                    lineSplit = line.split(" ");
                    newProcessor = new HalProcessor(lineSplit[1], numOfIOComponents);
                    processors.add(newProcessor);
                    //newProcessor.run();
                    continue;
                }
                lineSplit = line.split(":| |>");
                threadBuffer = new Buffer();
                processors.get(Integer.parseInt(lineSplit[0])).setIOComponentBuffer(threadBuffer, Integer.parseInt(lineSplit[1]));
                processors.get(Integer.parseInt(lineSplit[4])).setIOComponentBuffer(threadBuffer, Integer.parseInt(lineSplit[5]));
            }
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i = 0; i < processors.size(); i++) {
            processors.get(i).run();
        }
        for(int i = 0; i < processors.size(); i++) {
            try {
                processors.get(i).join();
            }catch(Exception e) {}
        }
    }
}

