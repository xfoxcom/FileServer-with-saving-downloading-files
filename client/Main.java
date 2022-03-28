package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final int PORT = 43666;
    public static final String fullPath = "C:\\Users\\Alexander\\OneDrive\\Documents\\GitHub\\File Server\\File Server\\task\\src\\client\\data\\";

    public static void main(String[] args) throws InterruptedException {
Thread.sleep(1000); // для проверки, отсрочка запуска клиента
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
            Scanner scr = new Scanner(System.in);
            switch (scr.nextLine()) {
                case "1": output.writeUTF("1");
                    System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                    switch (scr.nextInt()) {
                        case 1: output.writeInt(100);
                            System.out.print("Enter filename: ");
                            scr.nextLine();
                            String fileName = scr.nextLine();
                            output.writeUTF(fileName);
                            System.out.println("The request was sent.");
                            int number = input.readInt();
                            if (number == 1) {
                                System.out.print("The file was downloaded! Specify a name for it: ");
                                String specialName1 = scr.nextLine();
                                File file1 = new File(fullPath + specialName1);
                                int length = input.readInt();
                                byte[] fileContent = new byte[length];
                                input.readFully(fileContent, 0, length);
                                FileOutputStream fos = new FileOutputStream(file1);
                                fos.write(fileContent);
                                fos.close();
                                System.out.println("File saved on the hard drive!");
                                break;
                            } else if (number == 2) {
                                System.out.println("The response says that this file is not found!");
                            } break;
                        case 2: output.writeInt(200);
                            System.out.print("Enter id: ");
                            int id = scr.nextInt();
                            output.writeInt(id);
                            System.out.println("The request was sent.");
                            int number2 = input.readInt();
                            if (number2 == 1) {
                                System.out.print("The file was downloaded! Specify a name for it: ");
                                scr.nextLine();
                                String specialName2 = scr.nextLine();
                                File file2 = new File(fullPath + specialName2);
                                int length1 = input.readInt();
                                byte[] fileContent1 = new byte[length1];
                                input.readFully(fileContent1, 0, length1);
                                FileOutputStream fos1 = new FileOutputStream(file2);
                                fos1.write(fileContent1);
                                fos1.close();
                                System.out.println("File saved on the hard drive!");
                                break;
                            } if (number2 == 2) {
                            System.out.println("The response says that this file is not found!");
                        }
                    } break;
                case "2": output.writeUTF("2");
                    System.out.print("Enter name of the file: ");
                    String filename = scr.nextLine();
                    File file = new File(fullPath + filename);
                    if (!file.exists()) { System.out.println("File not exists"); break; }
                    else {
                        System.out.print("Enter name of the file to be saved on server: ");
                        String specialName = scr.nextLine();  // or next? can be NULL
                        output.writeUTF(specialName); // отправили на сервер желаемое имя файла

                        FileInputStream ios = new FileInputStream(file);
                        byte[] message = new byte[(int) file.length()];

                        ios.read(message);
                        ios.close();

                        output.writeInt(message.length); // отправили длину сообщения
                        output.write(message); // отправили само сообщение

                        System.out.println("The request was sent.");
                        System.out.println(input.readUTF());
                        break;
                    }
                case "3": output.writeUTF("3");
                    System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
                    int choose = scr.nextInt();
                    switch (choose) {
                        case 1: output.writeInt(100);
                            System.out.print("Enter filename: ");

                            scr.nextLine();
                            String a = scr.nextLine();
                            output.writeUTF(a);

                            System.out.println("The request was sent.");
                            System.out.println(input.readUTF());
                            break;
                        case 2: output.writeInt(200);
                            System.out.print("Enter id: ");
                            int id = scr.nextInt();
                            output.writeInt(id);
                            System.out.println("The request was sent.");
                            System.out.println(input.readUTF());
                            break;
                    } break;
                case "exit": output.writeUTF("4");
                    System.out.println("The request was sent.");
                    break;
            }
        } catch (IOException e) {
           // e.printStackTrace();
        }
    }
}
