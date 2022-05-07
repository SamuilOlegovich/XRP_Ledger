package com.samuilolegovich.model.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuilolegovich.enums.StringEnum;
import okhttp3.HttpUrl;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.xrpl.xrpl4j.client.XrplClient;

import javax.websocket.*;
import java.io.*;
import java.net.*;
import java.net.http.WebSocket;
import java.util.Scanner;

@ClientEndpoint
public class SocketXRPTest {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedReader in;
    private BufferedWriter bufferedWriter;

    private WebSocketSession webSocketSession;
    private WebSocketClient webSocketClient;

    public SocketXRPTest() {
//        if (document.getElementById("xls").checked) net = "wss://xls20-sandbox.rippletest.net:51233"
//        if (document.getElementById("tn").checked) net = "wss://s.altnet.rippletest.net:51233"
//        if (document.getElementById("dn").checked) net = "wss://s.devnet.rippletest.net:51233"
//        return net
    }


    public void initSocket() throws Exception {
//        HttpUrl rippledUrl = HttpUrl.get("wss://s.devnet.rippletest.net:51233");
//        XrplClient xrplClient = new XrplClient(rippledUrl);
//        System.out.println(xrplClient.getJsonRpcClient());
        webSocketClient = new WebSocketClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        webSocketClient.connect(container, URI.create("wss://s.devnet.rippletest.net:51233"));
        System.out.println("-------------------------------------");
//        webSocketSession = new WebSocketSession();

//        try {
//
//            System.out.println("****************** ");
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            System.out.println("**************************************");
//            container.connectToServer(MyClientEndpoint.class, URI.create("ws://localhost:8989/tictactoeserver/endpoint/hello"));
////            Session session = container.connectToServer(MyClientEndpoint.class, URI.create("wss://s.altnet.rippletest.net:51233"));
//            System.out.println("**************************************___________________________");
////            System.out.println("******************                    " + session.getRequestURI());
//
////            session.close();
//        }
//        catch (DeploymentException | IOException e) {
//
//        } finally {
//
//        }

//        webSocketClient.connect(container, URI.create("ws://localhost:8080/tictactoeserver/endpoint"));



        try {
            try {
                RequestDto requestDto = new RequestDto();
                MyFirstRequestDto myFirstRequestDto = new MyFirstRequestDto();
                ObjectMapper objectMapper = new ObjectMapper();
                String st = objectMapper.writeValueAsString(requestDto);
                String st2 = objectMapper.writeValueAsString(myFirstRequestDto);
                System.out.println(st);
                System.out.println(st2);
                // адрес - локальный хост, порт - 4004, такой же как у сервера
                // этой строкой мы запрашиваем



                System.out.println("****************************1************************************");
//                clientSocket = new Socket(StringEnum.WSS_TEST.value, Integer.parseInt(StringEnum.WSS_PORT_TEST.value));
                clientSocket = new Socket();

                System.out.println("****************************2************************************");
                clientSocket.connect(new InetSocketAddress("s.devnet.rippletest.net", 51233));
//                Scanner scanner = new Scanner(clientSocket.getInputStream());
//                clientSocket.connect(new InetSocketAddress("s1.ripple.com", 51233));
//                clientSocket.connect(new InetSocketAddress("s.altnet.rippletest.net", 51233));
//                clientSocket.connect(new InetSocketAddress("india.colorado.edu", 13));

                System.out.println("****************************3************************************");
                System.out.println(clientSocket.getLocalAddress().getHostAddress());
                System.out.println(clientSocket.isClosed());
                System.out.println(clientSocket.isConnected());

                System.out.println("****************************4************************************");


//                while (scanner.hasNextLine()) {
//
//                    System.out.println(scanner.next());
//                }

                //  у сервера доступ на соединение
                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                // читать соообщения с сервера
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                while (in.ready()) {
//
//                System.out.println(in.readLine());
//                }
                System.out.println("****************************5************************************");
                // писать туда же
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                // если соединение произошло и потоки успешно созданы - мы можем
                //  работать дальше и предложить клиенту что то ввести
                // если нет - вылетит исключение
//                String word = bufferedReader.readLine(); // ждём пока клиент что-нибудь
                // не напишет в консоль
//                bufferedWriter.write(word + "\n"); // отправляем сообщение на сервер
//                bufferedWriter.write(st); // отправляем сообщение на сервер
                bufferedWriter.write(st2); // отправляем сообщение на сервер
                bufferedWriter.write("{\"id\": \"on_open_ping_1\",\"command\": \"ping\"}"); // отправляем сообщение на сервер
                bufferedWriter.flush();
                String serverWord = in.readLine(); // ждём, что скажет сервер
                System.out.println("Ответ сервера  -->  " + serverWord); // получив - выводим на экран
            } catch (IOException e) {
                System.out.println("Catch  -->  " + e.getMessage());

            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                in.close();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("s.devnet.rippletest.net", 51233);
            try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
                String line = "{\"id\": 1, \"command\": \"account_channels\", \"account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\", \"destination_account\": \"ra5nK24KXen9AHvsdFTKHSANinZseWnPcX\", \"ledger_index\": \"validated\"}";

                out.write(line.getBytes());
                out.flush();

                byte[] data = new byte[32 * 1024];
                int readBytes = in.read(data);

                System.out.println("Server  -->  " + new String(data, 0 , readBytes));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    {"id": 1, "command": "account_channels", "account": "rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn", "destination_account": "ra5nK24KXen9AHvsdFTKHSANinZseWnPcX", "ledger_index": "validated"}
}
