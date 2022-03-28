package server;
import java.io.*;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.Random;
        import java.util.Scanner;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

public class Main {
    public static final int PORT = 43666;
    public static final String fullPath = "C:\\Users\\Alexander\\OneDrive\\Documents\\GitHub\\File Server\\File Server\\task\\src\\server\\data\\";
    public static volatile Map<Integer, String> idBase = new ConcurrentHashMap<>();
    public static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {

        try {
            FileInputStream fis = new FileInputStream(fullPath + "idBase");
            ObjectInputStream ois = new ObjectInputStream(fis);
            idBase = (Map) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // e.printStackTrace();
        }

        System.out.println("Server started!");
        try (ServerSocket server = new ServerSocket(PORT)) {
            String exit = "";
            while (!exit.equals("exit")) {
                try (
                        Socket socket = server.accept();
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    String msg = input.readUTF();
                    switch (msg) {
                        case "1":
                            switch (input.readInt()) {
                                case 100:  String file1Name = input.readUTF();
                                    File file1 = new File(fullPath + file1Name);
                                    if (!file1.exists()) {
                                        output.writeInt(2);
                                        break;
                                    } else {
                                        output.writeInt(1);
                                        byte[] message = new byte[(int) file1.length()];
                                        FileInputStream fis = new FileInputStream(file1);
                                        fis.read(message);
                                        fis.close();
                                        output.writeInt(message.length);
                                        output.write(message);
                                        break;
                                    }
                                case 200: int id = input.readInt();
                                    File file2 = new File(fullPath + idBase.get(id));
                                    if (!file2.exists()) {
                                        output.writeInt(2);
                                        break;
                                    } else { output.writeInt(1);

                                        byte[] message = new byte[(int) file2.length()];

                                        FileInputStream fis = new FileInputStream(file2);
                                        fis.read(message);
                                        fis.close();

                                        output.writeInt(message.length);
                                        output.write(message);
                                        break;
                                    }
                            }break;

                        case "2":
                            try {
                                String fileName = input.readUTF(); // получили желаемое имя файла
                                if (fileName.equals("")) {
                                    Random random = new Random();
                                    fileName = String.valueOf(random.nextInt(100));
                                }
                                File file = new File(fullPath + fileName);
                                int fileID = (int) (Math.random() * 100);

                                int length = input.readInt();
                                byte[] fileContent = new byte[length];
                                input.readFully(fileContent, 0, length);

                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(fileContent);
                                fos.close();

                                output.writeUTF("Response says that file is saved! ID = " + fileID);
                                idBase.put(fileID, fileName);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case "3": int idOrName = input.readInt();
                            if (idOrName == 100) {
                                String file3Name = input.readUTF();
                                File file3 = new File(fullPath + file3Name);
                                if (!file3.exists()) {
                                    output.writeUTF("The response says that this file was not found!");
                                    break;
                                } else {
                                    file3.delete();
                                    for (Integer k: idBase.keySet()) {
                                        for (String s: idBase.values()) {
                                            if (s.equals(file3Name)) idBase.remove(k,s);
                                        }
                                    }
                                    output.writeUTF("The response says that this file was successfully deleted!");
                                    break;
                                }
                            }
                            if(idOrName == 200) {
                                int id = input.readInt();
                                if (!idBase.containsKey(id)) {
                                    output.writeUTF("The response says that this file was not found!");
                                    break;
                                } else {
                                    File file4 = new File(fullPath + idBase.get(id));
                                    file4.delete();
                                    idBase.remove(id);
                                    output.writeUTF("The response says that this file was successfully deleted!");
                                    break;
                                }
                            }
                        case "4": exit = "exit"; break;
                    }
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(fullPath + "idBase");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idBase);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}