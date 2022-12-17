import java.util.Scanner;

public class IOStandardComponent extends IOComponent{
    public IOStandardComponent() {
        super();
    }

    public void getStdInput() {
        Scanner inputReader = new Scanner(System.in);
        System.out.print("Bitte geben Sie eine Zahl ein: ");
        value = inputReader.nextDouble();
    }

    public void giveOutput() {
        System.out.println(value);
    }

    public void giveOutput(double val) {
        System.out.println(val);
    }
}
