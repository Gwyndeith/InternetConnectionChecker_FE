import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
            System.out.println((String) ois.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        while(true) {
            Scanner scanner = new Scanner(System.in);
            String inputLine = scanner.nextLine();
            if (inputLine.equals("exit"))
                break;
            else if (inputLine.equals("ping"))
                runPingTest(oos, ois);
            else if (inputLine.equals("download"))
                runDownloadTest(oos, ois);
            else if (inputLine.equals("upload"))
                runUploadTest(oos, ois);
        }
    }

    public static void runPingTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            oos.writeObject(System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runDownloadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            oos.writeObject(System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runUploadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            oos.writeObject(System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
