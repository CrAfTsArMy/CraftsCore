package de.craftsblock.craftscore.web.socket;

import de.craftsblock.craftscore.annotations.Beta;
import de.craftsblock.craftscore.annotations.Experimental;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Experimental
@Beta
public class WebSocketServer {

    private static final ConcurrentLinkedQueue<WebSocketClient> activeClients = new ConcurrentLinkedQueue<>();

    private final InetSocketAddress address;
    private final int backlog;

    private Thread thread;

    public WebSocketServer(InetSocketAddress address) {
        this(address, 0);
    }

    public WebSocketServer(InetSocketAddress address, int backlog) {
        this.address = address;
        this.backlog = backlog;
    }

    public void start() {
        if (thread != null)
            throw new IllegalStateException("You can not start a web socket server twice!");
        thread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(address, 0);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    WebSocketClient clientHandler = new WebSocketClient(socket);
                    clientHandler.start();
                    activeClients.add(clientHandler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "WebSocket Server #" + UUID.randomUUID().toString().replace("-", "").substring(0, 6));
        thread.start();
    }

    public void stop() {
        thread.interrupt();
        thread = null;
        for (WebSocketClient handler : activeClients)
            handler.disconnect();
    }

    private static class WebSocketClient extends Thread {
        private final Socket socket;

        private final BufferedReader reader;
        private final BufferedWriter writer;

        public WebSocketClient(Socket socket) throws IOException {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        @Override
        public void run() {
            try {
                String request = reader.readLine();
                String response = handshake(request);
                writer.write(response);
                writer.flush();

                while (!isInterrupted()) {
                    String message = readSocketFrame(reader);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                activeClients.remove(this);
            }
            interrupt();
        }

        public void disconnect() {
            disconnect(null);
        }

        public void disconnect(@Nullable String reason) {
            if (reason != null && !reason.isEmpty())
                send(reason);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            activeClients.remove(this);
        }

        public void send(String message) {
            try {
                writer.write(message + "\r\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public void send(byte[] message) throws IOException {
            socket.getOutputStream().write(message);
            socket.getOutputStream().flush();
        }

        private String readSocketFrame(BufferedReader reader) throws IOException {
            StringBuilder frame = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                frame.append(line);
            return frame.toString();
        }

        private String handshake(String request) {
            try {
                for (String line : request.split("\r\n"))
                    if (line.contains("Sec-WebSocket-Key"))
                        return "HTTP/1.1 101 Switching Protocols\r\n" +
                                "Upgrade: websocket\r\n" +
                                "Connection: Upgrade\r\n" +
                                "Sec-WebSocket-Accept: " + convertToAcceptKey(line.split(":")[1].trim()) + "\r\n\r\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        private String convertToAcceptKey(String key) throws NoSuchAlgorithmException {
            key += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            byte[] sha1Hash = MessageDigest.getInstance("SHA-1").digest(key.getBytes(StandardCharsets.ISO_8859_1));
            return Base64.getEncoder().encodeToString(sha1Hash);
        }
    }

}
