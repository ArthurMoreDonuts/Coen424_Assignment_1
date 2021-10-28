public class Response_Json {
    private String RFW_ID;
    private int last_batch_ID;
    private Record[] Batch;

    public Response_Json(String RFW_ID, int last_batch_ID, Record[] batch) {
        this.RFW_ID = RFW_ID;
        this.last_batch_ID = last_batch_ID;
        Batch = batch;
    }

    public String getRFW_ID() {
        return RFW_ID;
    }

    public void setRFW_ID(String RFW_ID) {
        this.RFW_ID = RFW_ID;
    }

    public int getLast_batch_ID() {
        return last_batch_ID;
    }

    public void setLast_batch_ID(int last_batch_ID) {
        this.last_batch_ID = last_batch_ID;
    }

    public Record[] getBatch() {
        return Batch;
    }

    public void setBatch(Record[] batch) {
        Batch = batch;
    }
}
