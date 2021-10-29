import com.google.gson.Gson;

import java.util.*;


import java.io.*;
import java.net.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors




public class server {
    final static int number_of_samples = 10000;
	//Array of Sockets for all connected clients
	static ArrayList<Socket> ClientsSocketes = new ArrayList<Socket>();


	public static void main(String[] args) throws Exception {
		

 	   	
		//create the welcoming server's socket
 	   	ServerSocket welcomeSocket = new ServerSocket(6789);
  	    
 	   	//thread to always listen for new connections from clients
 	   	new Thread (new Runnable(){ @Override
 	   		public void run() {

 	   		Socket connectionSocket;
	 	   	DataOutputStream outToClient;
	
	 	   	while (!welcomeSocket.isClosed()) {

 	   		try {

 	   			//when a new client connect, accept this connection and assign it to a new connection socket
 	   			connectionSocket = welcomeSocket.accept();

 	   			//create a new output stream and send the message "You are connected" to the client
 	   			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes("-Connect," + "Message here" + "\n");



				ClientsSocketes.add(connectionSocket);
 	   			StartNewClientThread(connectionSocket);

 	   		}
 	   		catch (Exception ex) {
 	   		
 	   		}
	                  
 	    } 	 
 	   	
 	  }}).start();
 	   
 	   	
 	  	



 	  
    }
	
	
	static void StartNewClientThread(Socket connectionSocket)
	{
		
		//thread to receive and reply to specific client
		new Thread (new Runnable(){ @Override
		public void run() {
		
			try {
				
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				String clientSentence;
				boolean isRun = true;
				Response_Json responseJson;
				Request_Json requestJson;
				Gson gson = new Gson();
				DataInputStream dis = new DataInputStream(connectionSocket.getInputStream());
				while (isRun) {
				
					clientSentence = inFromClient.readLine();


	    			if (clientSentence.startsWith("-Remove")) { //Remove Client

	    				for (int i = 0; i < ClientsSocketes.size(); i++) {
	    					if (ClientsSocketes.get(i).getPort() == connectionSocket.getPort()) {
	    						ClientsSocketes.remove(i);
	    						isRun = false;
	    					}
	    				}
		
					} else if (clientSentence.startsWith("-JSON")) {
						System.out.println("Identified the string as json");
						String []username = clientSentence.split("@");
						System.out.println(username.length);
						requestJson = gson.fromJson(username[1],Request_Json.class);
						Record[] Batch = new Record[requestJson.getBatch_Size()*requestJson.getBatch_Unit()];
						boolean training_or_testing;
						boolean DVD_or_NDBench;
						try {
							File myObj;
							if (requestJson.getBenchmark().equals("DVD")){
								DVD_or_NDBench = true;
								if(requestJson.getData_Type().equals("Training")) {
									myObj = new File("src/main/resources/DVD-training.txt");
									training_or_testing = true;
								}
								else {
									training_or_testing = false;
									myObj = new File("src/main/resources/DVD-testing.txt");
								}
							}else{
								DVD_or_NDBench = false;
								if(requestJson.getData_Type().equals("Training")) {
									myObj = new File("src/main/resources/NDBench-training.txt");
									training_or_testing = true;
								}
								else{
									training_or_testing = false;
									myObj = new File("src/main/resources/NDBench-testing.txt");
							}
							}

							Scanner myReader = new Scanner(myObj);
							String Line = myReader.nextLine();
							//System.out.println(Line);
							int controller;
							switch (requestJson.getWorkload_Metric()){
								case "CPU":
									controller = 0;
									break;
								case "NetworkIn":
									controller = 1;
									break;
								case "NetworkOut":
									controller = 2;
									break;
								case "Memory":
									controller = 3;
									break;
								default:
									controller = 4;
							}
							//System.out.println("Last record should be "+ (requestJson.getBatch_ID()+requestJson.getBatch_Size())*requestJson.getBatch_Unit());


							for ( int i =0 ; i < number_of_samples ; i++){
								Line = myReader.nextLine();
								//System.out.println("read line "+ i );
								if (i >= (requestJson.getBatch_ID()+requestJson.getBatch_Size())*requestJson.getBatch_Unit()){
									//System.out.println(" Finished reading");
									break;
								}


								if (i >= requestJson.getBatch_ID()*requestJson.getBatch_Unit() ){
									//System.out.println(" Storing for "+ i );
									String[] values = Line.split("\t");
									//System.out.println(values.length);
									if (controller == 0)
										Batch[i-requestJson.getBatch_ID()*requestJson.getBatch_Unit()] =  new Record(Double.parseDouble(values[0]),-1,-1,-1,training_or_testing,DVD_or_NDBench);
									else if (controller == 1)
										Batch[i-requestJson.getBatch_ID()*requestJson.getBatch_Unit()] =  new Record(-1,Double.parseDouble(values[1]),-1,-1,training_or_testing,DVD_or_NDBench);
									else if (controller == 2)
										Batch[i-requestJson.getBatch_ID()*requestJson.getBatch_Unit()] =  new Record(-1,-1,Double.parseDouble(values[2]),-1,training_or_testing,DVD_or_NDBench);
									else if (controller == 3)
										Batch[i-requestJson.getBatch_ID()*requestJson.getBatch_Unit()] =  new Record(-1,-1,-1,Double.parseDouble(values[3]),training_or_testing,DVD_or_NDBench);
									else
										Batch[i-requestJson.getBatch_ID()*requestJson.getBatch_Unit()] =  new Record(Double.parseDouble(values[0]),Double.parseDouble(values[1]),Double.parseDouble(values[2]),Double.parseDouble(values[3]),training_or_testing,DVD_or_NDBench);
								}
								//System.out.println("Finished this loop");
							}
							myReader.close();
							responseJson = new Response_Json(requestJson.getRFW_ID(),(int)(requestJson.getBatch_ID()+requestJson.getBatch_Size()-1),Batch);
							String json = gson.toJson(responseJson);
							outToClient.writeBytes("-JSON@" + json + "\n");
						} catch (FileNotFoundException e) {
							System.out.println("An error occurred.");
							e.printStackTrace();
						}


					}
					else if (clientSentence.startsWith("-Proto")) {
						int len = dis.readInt();
						byte[] data = new byte[len];
						if (len > 0) {
							dis.readFully(data);
							System.out.println(data.toString());

						}
						Request.RequestProto request = Request.RequestProto.parseFrom(data);
						if (request != null){

							Record[] Batch = new Record[request.getBatchSize()*request.getBatchUnit()];
							boolean training_or_testing;
							boolean DVD_or_NDBench;

							File myObj;
							if (request.getBenchmark().equals("DVD")){
								DVD_or_NDBench = true;
								if(request.getDataType().equals("Training")) {
									myObj = new File("src/main/resources/DVD-training.txt");
									training_or_testing = true;
								}
								else {
									training_or_testing = false;
									myObj = new File("src/main/resources/DVD-testing.txt");
								}
							}else{
								DVD_or_NDBench = false;
								if(request.getDataType().equals("Training")) {
									myObj = new File("src/main/resources/NDBench-training.txt");
									training_or_testing = true;
								}
								else{
									training_or_testing = false;
									myObj = new File("src/main/resources/NDBench-testing.txt");
								}
							}

							Scanner myReader = new Scanner(myObj);
							String Line = myReader.nextLine();
							//System.out.println(Line);
							int controller;
							switch (request.getWorkloadMetric()){
								case "CPU":
									controller = 0;
									break;
								case "NetworkIn":
									controller = 1;
									break;
								case "NetworkOut":
									controller = 2;
									break;
								case "Memory":
									controller = 3;
									break;
								default:
									controller = 4;
							}
							//System.out.println("Last record should be "+ (requestJson.getBatch_ID()+requestJson.getBatch_Size())*requestJson.getBatch_Unit());


							for ( int i =0 ; i < number_of_samples ; i++){
								Line = myReader.nextLine();
								//System.out.println("read line "+ i );
								if (i >= (request.getBatchID()+request.getBatchSize())*request.getBatchUnit()){
									//System.out.println(" Finished reading");
									break;
								}


								if (i >= request.getBatchID()*request.getBatchUnit() ){
									//System.out.println(" Storing for "+ i );
									String[] values = Line.split("\t");
									//System.out.println(values.length);
									if (controller == 0)
										Batch[i-request.getBatchID()*request.getBatchUnit()] =  new Record(Double.parseDouble(values[0]),-1,-1,-1,training_or_testing,DVD_or_NDBench);
									else if (controller == 1)
										Batch[i-request.getBatchID()*request.getBatchUnit()] =  new Record(-1,Double.parseDouble(values[1]),-1,-1,training_or_testing,DVD_or_NDBench);
									else if (controller == 2)
										Batch[i-request.getBatchID()*request.getBatchUnit()] =  new Record(-1,-1,Double.parseDouble(values[2]),-1,training_or_testing,DVD_or_NDBench);
									else if (controller == 3)
										Batch[i-request.getBatchID()*request.getBatchUnit()] =  new Record(-1,-1,-1,Double.parseDouble(values[3]),training_or_testing,DVD_or_NDBench);
									else
										Batch[i-request.getBatchID()*request.getBatchUnit()] =  new Record(Double.parseDouble(values[0]),Double.parseDouble(values[1]),Double.parseDouble(values[2]),Double.parseDouble(values[3]),training_or_testing,DVD_or_NDBench);
								}
								//System.out.println("Finished this loop");
							}
							myReader.close();

							Response.ResponseProto.Builder response = Response.ResponseProto.newBuilder();
							response.setRFWID(request.getRFWID());
							response.setLastBatchID((int)(request.getBatchID()+request.getBatchSize()-1));
							Response.ResponseProto.Record.Builder recordbuilder = Response.ResponseProto.Record.newBuilder();
							for (int i =0 ; i< Batch.length; i++){
								recordbuilder.setCPU(Batch[i].getCPU())
										.setMemory(Batch[i].getMemory())
										.setDVDOrNDBench(Batch[i].isDVD_or_NDBench())
										.setNetIn(Batch[i].getNet_in())
										.setNetOut(Batch[i].getNet_out())
										.setTrainingOrTesting(Batch[i].isTraining_or_testing());
								response.addBatch(recordbuilder.build());
							}
							System.out.println("Check point ");
							byte[] Proto = response.build().toByteArray();

							List <Response.ResponseProto.Record> temp = response.build().getBatchList();
							Response.ResponseProto.Record temo;
							for (int i =0 ; i< response.build().getBatchList().size(); i++)
							{
								temo = temp.get(i);
								System.out.println( i
										+ " " + temo.getCPU()
										+ " " + temo.getNetIn()
										+ " " + temo.getNetOut()
										+ " " + temo.getMemory());
							}

							outToClient.writeBytes("-Proto" + "\n");
							Thread.sleep(50);
							sendBytes(Proto,connectionSocket);






					}



	    			}	
				
				  			
			} } catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}}).start();
		  
	  
	}
	static void sendBytes(byte[] myByteArray,Socket socket) throws IOException {
		sendBytes(myByteArray, 0, myByteArray.length, socket);
	}

	static void sendBytes(byte[] myByteArray, int start, int len, Socket socket) throws IOException {
		if (len < 0)
			throw new IllegalArgumentException("Negative length not allowed");
		if (start < 0 || start >= myByteArray.length)
			throw new IndexOutOfBoundsException("Out of bounds: " + start);
		// Other checks if needed.

		// May be better to save the streams in the support class;
		// just like the socket variable.
		OutputStream out = socket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);

		dos.writeInt(len);
		if (len > 0) {
			dos.write(myByteArray, start, len);
		}
	}
    
}
