import java.util.UUID;

public class Request_Json {
    private String RFW_ID;
    private String Benchmark;
    private String Workload_Metric;
    private int Batch_Unit;
    private int Batch_ID;
    private int Batch_Size;
    private String Data_Type;

    public Request_Json(String benchmark, String workload_Metric, int batch_Unit, int batch_ID, int batch_Size, String data_Type) {
        RFW_ID = UUID.randomUUID().toString();
        Benchmark = benchmark;
        Workload_Metric = workload_Metric;
        Batch_Unit = batch_Unit;
        Batch_ID = batch_ID;
        Batch_Size = batch_Size;
        Data_Type = data_Type;
    }

    public String getRFW_ID() {
        return RFW_ID;
    }

    public void setRFW_ID(String RFW_ID) {
        this.RFW_ID = RFW_ID;
    }

    public String getBenchmark() {
        return Benchmark;
    }

    public void setBenchmark(String benchmark) {
        Benchmark = benchmark;
    }

    public String getWorkload_Metric() {
        return Workload_Metric;
    }

    public void setWorkload_Metric(String workload_Metric) {
        Workload_Metric = workload_Metric;
    }

    public int getBatch_Unit() {
        return Batch_Unit;
    }

    public void setBatch_Unit(int batch_Unit) {
        Batch_Unit = batch_Unit;
    }

    public int getBatch_ID() {
        return Batch_ID;
    }

    public void setBatch_ID(int batch_ID) {
        Batch_ID = batch_ID;
    }

    public int getBatch_Size() {
        return Batch_Size;
    }

    public void setBatch_Size(int batch_Size) {
        Batch_Size = batch_Size;
    }

    public String getData_Type() {
        return Data_Type;
    }

    public void setData_Type(String data_Type) {
        Data_Type = data_Type;
    }
}
