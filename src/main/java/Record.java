public class Record {

    private double CPU;
    private double net_in;
    private double net_out;
    private double memory;
    private boolean training_or_testing;
    private boolean DVD_or_NDBench;

    public Record(double CPU, double net_in, double net_out, double memory, boolean training_or_testing, boolean DVD_or_NDBench) {
        this.CPU = CPU;
        this.net_in = net_in;
        this.net_out = net_out;
        this.memory = memory;
        this.training_or_testing = training_or_testing;// true for training and false for testing
        this.DVD_or_NDBench = DVD_or_NDBench; // true for DVD and false for NDBench
    }

    public double getCPU() {
        return CPU;
    }

    public void setCPU(double CPU) {
        this.CPU = CPU;
    }

    public double getNet_in() {
        return net_in;
    }

    public void setNet_in(double net_in) {
        this.net_in = net_in;
    }

    public double getNet_out() {
        return net_out;
    }

    public void setNet_out(double net_out) {
        this.net_out = net_out;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public boolean isTraining_or_testing() {
        return training_or_testing;
    }

    public void setTraining_or_testing(boolean training_or_testing) {
        this.training_or_testing = training_or_testing;
    }

    public boolean isDVD_or_NDBench() {
        return DVD_or_NDBench;
    }

    public void setDVD_or_NDBench(boolean DVD_or_NDBench) {
        this.DVD_or_NDBench = DVD_or_NDBench;
    }
}
