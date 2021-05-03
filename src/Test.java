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
            starttime=oos.writeObject(System.currentTimeMillis());
            URL url = new URL ("https://uploadpath");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            private void sendFile(OutputStream out, String name, InputStream in, String fileName) {
                String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name,"UTF-8")
                        + "\"; filename=\"" + URLEncoder.encode(filename,"UTF-8") + "\"\r\n\r\n";
                out.write(o.getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[2048];
                for (int n = 0; n >= 0; n = in.read(buffer))
                    out.write(buffer, 0, n);
                out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            endtime=oos.writeObject(System.currentTimeMillis());
            return endtime-starttime;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runDownloadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            starttime =oos.writeObject(System.currentTimeMillis());
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
            endtime=oos.writeObject(System.currentTimeMillis());
            // Determine size of file
            //return size/(endtime-starttime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runUploadTest(ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            starttime=oos.writeObject(System.currentTimeMillis());
            URL url = new URL ("https://uploadpath");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            private void sendFile(OutputStream out, String name, InputStream in, String fileName) {
                String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name,"UTF-8")
                        + "\"; filename=\"" + URLEncoder.encode(filename,"UTF-8") + "\"\r\n\r\n";
                out.write(o.getBytes(StandardCharsets.UTF_8));
                byte[] buffer = new byte[2048];
                for (int n = 0; n >= 0; n = in.read(buffer))
                    out.write(buffer, 0, n);
                out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            endtime=oos.writeObject(System.currentTimeMillis());
            // Determine size of file
            //return size/(endtime-starttime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
