import java.io.*;
import java.net.*;
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
            socket = new Socket("ec2-3-67-86-160.eu-central-1.compute.amazonaws.com", 9000);
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
                    System.out.println("Your upload speed is: " + runUploadTest(oos, ois) + " Mbps");
                    break;
            }
        }
    }

    //Sends an ICMP ping to the given IP address to ping it, and calculates and returns the time it took to do that in ms as output to console.
    public static long runPingTest(ObjectOutputStream oos, ObjectInputStream ois) {
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        boolean isPinged = false;
        try {
            isPinged = InetAddress.getByName("ec2-3-67-86-160.eu-central-1.compute.amazonaws.com").isReachable(2000);
            endTime = System.currentTimeMillis();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isPinged) {
            return (int) (endTime - startTime);
        } else {
            return -1;
        }
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

        return KBps / 1024;
    }

    public static long runUploadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        long startTime = 0;
        long endTime = 0;
        byte[] fileContent = null;
        try {
            startTime = System.currentTimeMillis();
            //oos.writeUTF(String.valueOf(startTime));
            oos.writeUTF("uploadTest");
            oos.flush();

            //File fileName=null;

            String receivedMessage = ois.readUTF();
            System.out.println("Server says: " + receivedMessage);
            if ("upload message received".equals(receivedMessage)) {
                //fileName = new File("largeFile.txt");
                try {
                    File uploadFile = new File("./uploadFile.txt");
                    fileContent = Files.readAllBytes(uploadFile.toPath());
                    oos.writeObject(fileContent);
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Something went wrong with connection to server.");
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert fileContent != null;
        try {
            endTime = ois.readLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(endTime - startTime);
        long Bps = fileContent.length / ((endTime - startTime) / 1000);
        long KBps = Bps / 1024;

        return KBps / 1024;
    }
}
