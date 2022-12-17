



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;




public class HalInterpreter {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Boolean debug = false;
        if(args.length >= 2 && args[1].equals("debug")) {
            debug = true;
        }
    }

    private boolean terminated = false;

    private long startTime = 0;

    private IOComponent[] components;

    private HalMMU MMU = new HalMMU();

    private ArrayList<HalInstruction> instructions = new ArrayList<HalInstruction>();
    private HalInstruction currentInstruction;

    private Double[] registers = new Double[16];
    private int PC = 0;
    private double ACC = 0.0;
    private Double IO0 = 0.0;
    private Double IO1 = 0.0;

    private Boolean debuger;
    private String programmPath;

    public HalInterpreter(String path, boolean debug, IOComponent components[]) {
        debuger = debug;
        programmPath = path;
        this.components = components;
    }

    public void start() {
        startTime = System.nanoTime();
        loadInstructions(programmPath);
        loadRegister();
        fetch();
        executeInstruction();
        while(!terminated) {
            fetch();
            terminated = executeInstruction();
        }
    }

    public void stop() {
        long endTime = System.nanoTime();
        System.out.println("Time elapsed: " + Long.toString((endTime-startTime) / 10000000) + "ms");
    }

    public int loadInstructions(String programName){
        File programm = new File(programName);
        FileReader reader;
        BufferedReader buffReader = null;
        try {
            reader = new FileReader(programm);
            buffReader = new BufferedReader(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String zeile;
        try {
            String command = "";
            String parameter = "";
            HalInstruction currentInstruction;
            while ((zeile = buffReader.readLine()) != null) {
                String[] temp = zeile.split(" ");
                command = temp[1];
                if(temp.length == 2) {
                    parameter = "-1";
                }
                else if(temp.length == 3) {
                    parameter = temp[2];
                }
                currentInstruction = new HalInstruction(command, Double.parseDouble(parameter));
                instructions.add(currentInstruction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instructions.size()+1;
    }

    public void loadRegister(){
        for(int i = 0; i < 12; i++) {
            registers[i] = 0.0;
        }
    }

    public void fetch() {
        currentInstruction = instructions.get(PC);
        PC++;
    }

    public boolean executeInstruction() {

        if(!terminated) {
            switch (currentInstruction.getCommand()) {
                case "START": start(); break;
                case "STOP": stop(); return true;
                case "OUT": out(); break;
                case "IN": in(); break;
                case "LOAD": load(); break;
                case "LOADNUM": loadNum(); break;
                case "STORE": store(); break;
                case "JUMPNEG": jumpNeg(); break;
                case "JUMPPOS": jumpPos(); break;
                case "JUMPNULL": jumpNull(); break;
                case "JUMP": jump(); break;
                case "ADD": add(); break;
                case "ADDNUM": addNum(); break;
                case "SUB": sub(); break;
                case "MUL": mul(); break;
                case "DIV": div(); break;
                case "SUBNUM": subNum(); break;
                case "MULNUM": mulNum(); break;
                case "DIVNUM": divNum(); break;
                case "LOADINS": loadInD(); break;
                case "STOREIND": storeInD(); break;
                case "DUMPREG" : DUMPREG(); break;
            }
        }
        return false;
    }

    public void out() {
        components[(int) currentInstruction.getParameter()].setValue(ACC);
        components[(int) currentInstruction.getParameter()].giveOutput();
    }

    public void in() {
        if(currentInstruction.getParameter() < 2) {
            components[(int) currentInstruction.getParameter()].getStdInput();
            ACC = components[(int) currentInstruction.getParameter()].getValue();
            return;
        }
        ACC = components[(int) currentInstruction.getParameter()].getInput();
    }

    public void load() {
        instrDebug();
        double adress = ((int)currentInstruction.getParameter());
        Page loadingPage = MMU.findPage(adress);
        int pageOffset = (int) adress % 1000;
        ACC = loadingPage.memory[pageOffset];
        instrDebug();
    }

    public void jump() {
        instrDebug();
        PC = (int)(currentInstruction.getParameter());
        instrDebug();
    }

    public void jumpNull() {
        if(compareFloat(ACC, 0.0) == 0) {
            instrDebug();
            PC = (int)(currentInstruction.getParameter());
            instrDebug();
        }
    }

    public void jumpPos() {
        if(ACC > 0) {
            instrDebug();
            PC = (int)(currentInstruction.getParameter());
            instrDebug();
        }
    }

    public void jumpNeg() {
        if(ACC < 0) {
            instrDebug();
            PC = (int)(currentInstruction.getParameter());
            instrDebug();
        }
    }

    public void mul() {
        instrDebugMitReg();
        int instrReg = (int)(currentInstruction.getParameter());
        ACC = registers[instrReg] * ACC;
        instrDebugMitReg();
    }

    public void div() {
        int instrReg = (int)(currentInstruction.getParameter());
        if(registers[instrReg] == 0)
            System.out.println("Es darf nicht mit 0 dividiert werden");
        else {
            instrDebugMitReg();
            ACC = ACC / registers[instrReg];
            instrDebugMitReg();
        }
    }

    public void mulNum() {
        instrDebug();
        double mulNum = currentInstruction.getParameter();
        ACC = ACC * mulNum;
        instrDebug();
    }

    public void divNum() {
        double divNum = currentInstruction.getParameter();
        if(divNum == 0.0 && divNum == 0) {
            System.out.println("Es darf nicht mit 0 dividiert werden");
        }
        else {
            instrDebug();
            ACC = ACC / divNum;
            instrDebug();
        }
    }

    public void loadInD() {

        instrDebug();
        double adress = (int)(currentInstruction.getParameter());
        Page loadingPage = MMU.findPage(adress);
        int pageOffset = (int) adress % 1000;
        ACC = loadingPage.memory[pageOffset];
        loadingPage = MMU.findPage(ACC);
        pageOffset = (int) ACC % 1000;
        ACC = loadingPage.memory[pageOffset];
        instrDebug();
    }

    public void storeInD() {
        instrDebug();

        double adress = ((int)currentInstruction.getParameter());
        Page storeingPage = MMU.findPage(adress);
        int pageOffset = (int) adress % 1000;
        adress = storeingPage.memory[pageOffset];
        storeingPage = MMU.findPage(adress);
        pageOffset = (int) adress % 1000;
        storeingPage.memory[pageOffset] = ACC;

        instrDebug();
    }

    public void loadNum() {
        ACC = currentInstruction.getParameter();
    }

    public void store() {
        double adress = ((int)currentInstruction.getParameter());
        Page storeingPage = MMU.findPage(adress);
        int pageOffset = (int) adress % 1000;
        storeingPage.memory[pageOffset] = ACC;
    }

    public void sub() {
        instrDebugMitReg();
        int instrReg = (int)(currentInstruction.getParameter());
        ACC = ACC - registers[instrReg];
        instrDebugMitReg();
    }

    public void addNum() {
        instrDebug();
        Double instrReg = currentInstruction.getParameter();
        ACC = ACC + instrReg;
        instrDebug();
    }

    public void add() {
        instrDebugMitReg();


        int adress = (int)(currentInstruction.getParameter());
        Page addPage = MMU.findPage(adress);
        int pageOffset = adress % 1000;
        ACC = ACC + addPage.memory[pageOffset];



        instrDebugMitReg();
    }

    public void subNum() {
        instrDebug();
        ACC = ACC - currentInstruction.getParameter();
        instrDebug();
    }

    public void instrDebug() {
        if(debuger == true)
            System.out.println("\t\t----Debugmodus-----\nACC: " + ACC + "\tInstruktion: " + currentInstruction.getCommand()
                    + "\n");
    }
    public void instrDebugMitReg() {
        if(debuger == true) {
            int instReg = (int)(currentInstruction.getParameter());
            System.out.println("\t\t----Debugmodus-----\nACC:" + ACC + "\tRegister" + instReg + ": " + registers[instReg] + "\tInstruktion: " + currentInstruction.getCommand()
                    + "\n");
        }
    }

    public int compareFloat(double a, double b) {
        return Integer.compare((int)(a * 1000000), (int)(b * 1000000));
    }

    public void LOADIND(int r) throws IOException {

        double adress = registers[r];
        Page loadingPage = MMU.findPage(adress);
        int pageOffset = (int) adress % 1000;
        ACC = loadingPage.memory[pageOffset];
    }

    public void STOREIND(int r) throws IOException {
        double adress = registers[r];
        Page storeingPage = MMU.findPage(adress);
        int pageOffset = (int) adress % 1000;
        storeingPage.memory[pageOffset] = ACC;
    }

    public void DUMPREG() {
        for(int i = 0; i<4; i++) {
            if(MMU.getCurrentHead().getPage() == null)
                return;
            for(int k = 0; k<999; k++) {
                System.out.print("Register: " + ((i*1000) + k) + " Registerinhalt: ");

                components[0].giveOutput(MMU.getCurrentHead().getPage().memory[k]);
                System.out.print("\n");
            }
            MMU.setCurrendHead(MMU.getCurrentHead().getNext());
        }
    }
}

