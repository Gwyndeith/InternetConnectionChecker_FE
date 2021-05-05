import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * @author: Orkun Doğan
 * @date: 26/04/2021
 */
public class Test {
    public static void main(String[] args) {
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            socket = new Socket("ec2-18-189-180-216.us-east-2.compute.amazonaws.com", 9000);
//            socket = new Socket("localhost", 9000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Server says: " + ois.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        label:
        while (true) {
            System.out.print("ping, download or upload: ");
            String inputLine = scanner.nextLine();
            switch (inputLine) {
                case "exit":
                    try {
                        //Sends the required message to the BE server so that it can close the connection.
                        assert oos != null;
                        oos.writeUTF("closeConnection");
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break label;
                case "ping":
                    assert oos != null;
                    assert ois != null;
                    //Sends the required message to the BE server to start the ping test process
                    System.out.println("Your ping is: " + runPingTest(oos, ois) + " ms");
                    break;
                case "download":
                    assert oos != null;
                    System.out.println("Your download speed is: " + runDownloadTest(oos, ois) + " Mbps");
                    break;
                case "upload":
                    assert oos != null;
                    runUploadTest(oos, ois);
                    break;
            }
        }
    }

    //Sends the required message to the BE server to start the ping test process
    public static long runPingTest(ObjectOutputStream oos, ObjectInputStream ois) {
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            //Send pingTest message to the server
            oos.writeUTF("pingTest");
            oos.flush(); //flush method sends what's been written into the OutputStream so far.

            //Reads the server's reply and informs the user accordingly.
            String receivedMessage = ois.readUTF();
            System.out.println("Server says: " + receivedMessage);
            if ("ping message received".equals(receivedMessage))
                endTime = System.currentTimeMillis();
            else {
                System.out.println("Something went wrong with connection to server.");
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (int) (endTime - startTime);
    }

    public static long runDownloadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        long startTime = 0;
        long endTime = 0;
        byte[] fileContent = null;
        try {
            startTime = System.currentTimeMillis();
            oos.writeUTF("downloadTest");
            oos.flush();

            File fileName = null;

            String receivedMessage = ois.readUTF();
            System.out.println("Server says: " + receivedMessage);
            if ("download message received".equals(receivedMessage)) {
                fileName = new File("largeFile.txt");
                try {
                    Files.deleteIfExists(fileName.toPath());
                    System.out.println("File creation: " + fileName.createNewFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Something went wrong with connection to server.");
                return -1;
            }
            try {
                fileContent = (byte[]) ois.readObject();
//                PrintStream fileWriter = new PrintStream(fileName);
//                for (int i = 0; i < fileContent.length; i++)
//                    fileWriter.print(fileContent[i]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            endTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fileContent != null;
        System.out.println(endTime - startTime);
        long Bps = fileContent.length / ((endTime - startTime) / 1000);
        long KBps = Bps / 1024;
        long Mbps = KBps / 1024;

        return Mbps;
    }

    public static void runUploadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            long startTime = System.currentTimeMillis();
            oos.writeUTF(String.valueOf(startTime));
            oos.flush();

            long endTime = System.currentTimeMillis();
            // Determine size of file
            //return size/(endtime-starttime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(OutputStream out, String name, InputStream in, String fileName) throws IOException {
        String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name, "UTF-8") + "\"; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[2048];
        for (int n = 0; n >= 0; n = in.read(buffer))
            out.write(buffer, 0, n);
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
