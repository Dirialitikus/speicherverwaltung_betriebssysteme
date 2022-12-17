

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Random;
import java.util.Scanner;



public class HalMMU {
    HalMMU(){ setUpVirtualMemory(); setUpRegister();}
    String errorString = "";
    int errorCounter = 1;
    protected Page virtualMemory[] = new Page [64];
    private Register currentHead = null;
    private final int maxReg = 4;


    void setUpVirtualMemory() {

        for(int i = 0; i<64; i++) {

            virtualMemory[i] = new Page(i);

        }
    }

    void setUpRegister() {
        currentHead = new Register();
        Register current = currentHead;
        for(int i = 0; i<maxReg - 1; i++) {
            current.setNext(new Register());
            current.getNext().setPrevious(current);
            current = current.getNext();
            if(i == maxReg-2) {
                current.setNext(currentHead);
                currentHead.setPrevious(current);
            }
        }
    }

    Page findPage(double r) {
        int pageNr =(int) r / 1000;
        Register data = isPageInList(pageNr);

        if(data == null) {
            randomswapPage(pageNr);
            return currentHead.getPage();
        }

        return data.getPage();
    }

    Register isPageInList(int pageNr){
        int i = 0;
        Register current = currentHead;

        while(i<maxReg) {
            if(current.getPage() == null) {
                errorFunction(pageNr);
                return null;

            }
            if(current.getPage().getNumber() == pageNr) {
                return current;
            }
            current = current.getNext();
            i++;
        }
        errorFunction(pageNr);
        return null;

    }

    void errorFunction(int pageNr) {


        File errorlog = new File("pageErrorLog.txt");
        BufferedWriter errorWriter = null;
        if (!errorlog.exists()) {
            try {
                errorlog.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        errorString = errorString + "PAGE ERROR: " + errorCounter++ + " PAGE NUMBER: " + pageNr + "\n";

        try {
            FileWriter fw = new FileWriter(errorlog);
            errorWriter = new BufferedWriter(fw);
            errorWriter.write(errorString);
            errorWriter.newLine();
            errorWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    void swapPage(double pageNr) {

        while(true) {
            if(currentHead.getPage() == null || currentHead.getPage().getRefBit() == false) {
                currentHead.setPage(virtualMemory[(int)pageNr]);
                currentHead.getPage().setRefBit(true);
                break;
            }
            else {
                currentHead.getPage().setRefBit(false);
            }
            currentHead = currentHead.getNext();
        }
    }

    void randomswapPage(double pageNr) {

        Random r1 = new Random();
        int randomNumber = r1.nextInt(4);

        for(int i = 0; i<randomNumber; i++) {
            currentHead = currentHead.getNext();
        }

        currentHead.setPage(virtualMemory[(int)pageNr]);
        errorFunction((int)pageNr);


    }


    Register getCurrentHead() {return currentHead;}
    void setCurrendHead(Register currentHead) {this.currentHead = currentHead;}

}


class Register{
    private Page page = null;
    private Register next = null;
    private Register previous = null;

    void setNext(Register next) { this.next = next;}
    Register getNext() { return next;}
    Page getPage() { return page;}
    void setPage(Page page) { this.page = page;}

    private Boolean protectBit = true;

    void setProtectedBit(Boolean protectBit) {this.protectBit = protectBit;}
    void setPrevious(Register previous) {this.previous = previous;}
    Register getPrevious() {return previous;}

}


class Page{

    private int pageNr = 0;
    protected double memory[] = new double [1000];
    private Boolean refBit = false;
    Page (int i) {
        pageNr = i;}
    int getNumber() {return pageNr;}
    boolean getRefBit() {return refBit;}
    void setRefBit(boolean refBit) {this.refBit = refBit;}

}

