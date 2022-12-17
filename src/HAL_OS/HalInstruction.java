

public class HalInstruction {
    private String command = "";
    private double parameter = 0;

    public HalInstruction(String command, double parameter) {
        this.command = command;
        this.parameter = parameter;
    }

    public String getCommand() {
        return command;
    }
    public double getParameter() {
        return parameter;
    }
}
