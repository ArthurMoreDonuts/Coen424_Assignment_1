import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.UUID;

import static java.net.SocketOptions.SO_RCVBUF;


public class client {

	static Socket clientSocket;


	public static void main(String[] args)  {
		BufferedReader inFromConsole = new BufferedReader(new InputStreamReader(System.in));

		Request_Json requestJson;
		Gson gson = new Gson();




		try {

			//create a new socket to connect with the server application
			clientSocket = new Socket("localhost", 6789);
			//clientSocket = new Socket("3.142.142.222", 6789);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

			//create a buffer reader and connect it to the socket's input stream
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

			String receivedSentence;
			//always read received messages and append them to the textArea
			while (true) {
				System.out.print("Enter 1 for request or 2 for disconnect : ");

				int controller = Integer.parseInt(inFromConsole.readLine());
				if (controller == 1) {

					System.out.print("Enter 1 binary serialization, or 2 for Json serialization : ");
					int requestType = Integer.parseInt(inFromConsole.readLine());

					System.out.print("For the benchmark type, enter 1 for DVD or 2 for NDBench : ");
					int temp = Integer.parseInt(inFromConsole.readLine());
					String Benchmark_Type;
					if (temp == 1)
						Benchmark_Type = "DVD";
					else if (temp == 2)
						Benchmark_Type = "NDBench";
					else {
						System.out.println("Invalid input");
						continue;
					}

					System.out.print("For the workload metric, enter 1 for CPU, 2 for NetworkIn, 3 for NetworkOut, 4 for Memory, or 5 for all : ");
					temp = Integer.parseInt(inFromConsole.readLine());
					String Workload_Metric;
					if (temp == 1)
						Workload_Metric = "CPU";
					else if (temp == 2)
						Workload_Metric = "NetworkIn";
					else if (temp == 3)
						Workload_Metric = "NetworkOut";
					else if (temp == 4)
						Workload_Metric = "Memory";
					else if (temp == 5)
						Workload_Metric = "All";
					else {
						System.out.println("Invalid input");
						continue;
					}

					System.out.print("Enter the batch unit please : ");
					String Batch_Unit = inFromConsole.readLine();
					if (Batch_Unit.equals("0")) {
						System.out.println("Invalid input");
						continue;
					}
					System.out.print("Enter the batch ID please : ");
					String Batch_ID = inFromConsole.readLine();

					System.out.print("Enter the batch size please : ");
					String Batch_Size = inFromConsole.readLine();

					System.out.print("For data types, enter 1 for training or 2 for testing : ");
					int temp2 = Integer.parseInt(inFromConsole.readLine());
					String Data_Type;
					if (temp2 == 1)
						Data_Type = "Training";
					else if (temp2 == 2)
						Data_Type = "Testing";
					else {
						System.out.println("Invalid input");
						continue;
					}

					if (requestType == 1) {
						outToServer.writeBytes("-Proto" + "\n");
						Request.RequestProto.Builder request = Request.RequestProto.newBuilder();
						request.setRFWID(UUID.randomUUID().toString());
						request.setBatchID(Integer.parseInt(Batch_ID));
						request.setBatchSize(Integer.parseInt(Batch_Size));
						request.setBenchmark(Benchmark_Type);
						request.setBatchUnit(Integer.parseInt(Batch_Unit));
						request.setWorkloadMetric(Workload_Metric);
						request.setDataType(Data_Type);

						//byte[] Proto = request.build().toByteArray();
					//	Object A = Request.RequestProto.class.cast(request.build());
					//	sendBytes(serialize(A));
						sendBytes(serialize(request.build()));
						Boolean waiting = true;
						while (waiting) {
							receivedSentence = inFromServer.readLine();
							if (receivedSentence.startsWith("-Proto")) {
								String[] strings = receivedSentence.split("@");

								System.out.println("we are getting something "+ strings[1]);
								int len = Integer.parseInt(strings[1]);

								byte[] data = new byte[0];
								if (len > 0) {
									data = new byte[len];
									//dis.read(data);
									dis.readFully(data);
									System.out.println("Not here ");

								}


							//Response.ResponseProto response = Response.ResponseProto.parseFrom(data);
								Response.ResponseProto response = (Response.ResponseProto) deserialize(data);
							//System.out.println(receivedSentence);
							if (response != null) {
								List<Response.ResponseProto.Record> recordList = response.getBatchList();
								Response.ResponseProto.Record temo;
								Record[] array = new Record[recordList.size()];
								for (int i = 0; i < recordList.size(); i++) {
									temo = recordList.get(i);

									array[i] = new Record(	temo.getCPU(),
															temo.getNetIn(),
														    temo.getNetOut(),
															temo.getMemory(),
															temo.getTrainingOrTesting(),
															temo.getDVDOrNDBench());
								}
								Record[] Batch = array;
								System.out.println("The Response for request " + response.getRFWID() + " has arrived");
								System.out.println("Tha last batch ID is " + response.getLastBatchID());
								switch (temp) {
									case 1:
										System.out.println("Entry	CPU");
										break;
									case 2:
										System.out.println("Entry	NetworkIn");
										break;
									case 3:
										System.out.println("Entry	NetworkOut");
										break;
									case 4:
										System.out.println("Entry	Memory");
										break;
									default:
										System.out.println("Entry	CPU	NetworkIn	NetworkOut	Memory");

								}
								for (int i = 0; i < Batch.length; i++) {
									switch (temp) {
										case 1:
											System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
													+ " " + Batch[i].getCPU());
											break;
										case 2:
											System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
													+ " " + Batch[i].getNet_in());
											break;
										case 3:
											System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
													+ " " + Batch[i].getNet_out());
											break;
										case 4:
											System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
													+ " " + Batch[i].getMemory());
											break;
										default:
											System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
													+ " " + Batch[i].getCPU()
													+ " " + Batch[i].getNet_in()
													+ " " + Batch[i].getNet_out()
													+ " " + Batch[i].getMemory());

									}
								}

								waiting = false;
							}}

						}


					} else {
						requestJson = new Request_Json(
								Benchmark_Type,
								Workload_Metric,
								Integer.parseInt(Batch_Unit),
								Integer.parseInt(Batch_ID),
								Integer.parseInt(Batch_Size),
								Data_Type);

						String json = gson.toJson(requestJson);
						outToServer.writeBytes("-JSON@" + json + "\n");



					Boolean waiting = true;
					while (waiting) {

						receivedSentence = inFromServer.readLine();
						//System.out.println(receivedSentence);

						if (receivedSentence.startsWith("-JSON")) {
							String[] strings = receivedSentence.split("@");
							System.out.println(strings.length);
							//	if (sendTextField.getText().equals(strings[2 + Integer.parseInt(strings[1])])) {

							Response_Json responseJson = gson.fromJson(strings[1], Response_Json.class);
							Record[] Batch = responseJson.getBatch();
							System.out.println("The Response for request " + responseJson.getRFW_ID() + " has arrived");
							System.out.println("Tha last batch ID is " + responseJson.getLast_batch_ID());
							switch (temp) {
								case 1:
									System.out.println("Entry	CPU");
									break;
								case 2:
									System.out.println("Entry	NetworkIn");
									break;
								case 3:
									System.out.println("Entry	NetworkOut");
									break;
								case 4:
									System.out.println("Entry	Memory");
									break;
								default:
									System.out.println("Entry	CPU	NetworkIn	NetworkOut	Memory");

							}
							for (int i = 0; i < Batch.length; i++) {
								switch (temp) {
									case 1:
										System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
												+ " " + Batch[i].getCPU());
										break;
									case 2:
										System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
												+ " " + Batch[i].getNet_in());
										break;
									case 3:
										System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
												+ " " + Batch[i].getNet_out());
										break;
									case 4:
										System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
												+ " " + Batch[i].getMemory());
										break;
									default:
										System.out.println(((Integer.parseInt(Batch_Unit)) * (Integer.parseInt(Batch_ID)) + i)
												+ " " + Batch[i].getCPU()
												+ " " + Batch[i].getNet_in()
												+ " " + Batch[i].getNet_out()
												+ " " + Batch[i].getMemory());

								}
							}

							waiting = false;
						}

					}
				}



				}
				else if(controller == 2) {
					outToServer.writeBytes("-Remove\n");
					clientSocket.close();
					break;
				}
				else {
					System.out.println("The number entered is invalid");
					continue;
				}

				/*

				  TODO - Implement binary serialization - deserialization
				  TODO - Move the codes to the cloud along with the file



				*/

				// Receiving the response
				/*
				outToServer.writeBytes("-Connect," + "Message here" + "\n");
				receivedSentence = inFromServer.readLine();
				System.out.println(receivedSentence);

				if (receivedSentence.startsWith("-Connected,")) {
					String[] strings = receivedSentence.split(",");
					//	if (sendTextField.getText().equals(strings[2 + Integer.parseInt(strings[1])])) {

				}

				 */

			}





		} catch (Exception ex) {
			System.out.println(ex);
		}


	}

	static void sendBytes(byte[] myByteArray) throws IOException {
		sendBytes(myByteArray, 0, myByteArray.length);
	}

	static void sendBytes(byte[] myByteArray, int start, int len) throws IOException {
		if (len < 0)
			throw new IllegalArgumentException("Negative length not allowed");
		if (start < 0 || start >= myByteArray.length)
			throw new IndexOutOfBoundsException("Out of bounds: " + start);
		// Other checks if needed.

		// May be better to save the streams in the support class;
		// just like the socket variable.
		OutputStream out = clientSocket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);

		dos.writeInt(len);
		if (len > 0) {
			dos.write(myByteArray, start, len);
		}
	}

	public static byte[] serialize(Object obj) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException{
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}



    
}