import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        while(true) {
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
                    System.out.println("Your ping is: " + runPingTest(oos, ois) + " ms");
                    break;
                case "download":
                    assert oos != null;
                    runDownloadTest(oos, ois);
                    break;
                case "upload":
                    assert oos != null;
                    runUploadTest(oos, ois);
                    break;
            }
        }
    }

    public static long runPingTest(ObjectOutputStream oos, ObjectInputStream ois) {
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();
            //Send pingTest message to the server
            oos.writeUTF("pingTest");
            oos.flush();
            //Give the start time to server? No need for ping test.
            //oos.writeObject(startTime);

            //Do not need to send any file to server for ping test
            //sendFile(oos, "pingTest", ois, "");
            String receivedMessage = ois.readUTF();
            System.out.println("Server says: " + receivedMessage);
            if ("message received".equals(receivedMessage))
                endTime = System.currentTimeMillis();
            else {
                System.out.println("Something went wrong with connection to server.");
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (int)(endTime - startTime);
    }

    public static void runDownloadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            long startTime = System.currentTimeMillis();
            oos.writeUTF(String.valueOf(startTime));
            File fileName = new File ("testfile.txt");
            URL url = new URL("http://DownloadFilePath");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            long endTime = System.currentTimeMillis();
            // Determine size of file
            //return size/(endtime-starttime);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name,"UTF-8") + "\"; filename=\"" + URLEncoder.encode(fileName,"UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[2048];
        for (int n = 0; n >= 0; n = in.read(buffer))
            out.write(buffer, 0, n);
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
