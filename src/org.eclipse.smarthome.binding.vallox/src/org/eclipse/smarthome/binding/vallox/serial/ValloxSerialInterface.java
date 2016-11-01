package org.eclipse.smarthome.binding.vallox.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main API class for connecting to a vallox central venting unit
 * via a RS485-to-Ethernet bridge. The bridge should be configured
 * such that it behaves as a TCP server and tunnels input and output
 * streams to the RS485 vallox interface.
 *
 * Main telegram specification taken from C-Code from
 * https://github.com/windkh/valloxserial
 *
 * @author Hauke
 *
 */
public class ValloxSerialInterface {

    byte senderID = ValloxProtocol.ADDRESS_PANEL8; // we send commands in the name of panel8 (29)
    byte receiverID = ValloxProtocol.ADDRESS_PANEL1; // we always listen for the telegrams between the master and the
                                                     // panel1!

    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;
    private ExecutorService listenerExecutor;
    private boolean shutdownListening = false;

    private ValloxStore vallox = new ValloxStore();

    /**
     * Get a bean with all variable states of the vallox installations. Variables
     * will be updated after listening has started. Initialially, variables are initialized with
     * 0 resp. false and might only get updated if the vallox sends updates. Need to
     * trigger a variable poll explicitly.
     * 
     * @return the variable store
     */
    public ValloxStore getValloxStore() {
        return vallox;
    }

    /**
     * Connect to a TCP providing host that bridges the serial interface of the
     * vallox installation to a TCP server.
     * 
     * @param host Hostname or IP Adress of the tcp-server
     * @param port port of the server
     * @throws UnknownHostException
     * @throws IOException
     */
    public void connect(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        System.out.println("Connected to " + host + ":" + port);
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("Closed " + socket.getInetAddress() + ":" + socket.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPoll(ValloxProperty propertyId) throws IOException {
        send(Variable.POLL.key, Variable.FAN_SPEED.key);
        // TODO: proper mapping
    }

    public void send(byte variable, byte value, byte destination) throws IOException {
        byte[] telegram = new byte[ValloxProtocol.LENGTH];

        telegram[0] = ValloxProtocol.DOMAIN;
        telegram[1] = senderID;
        telegram[2] = destination;
        telegram[3] = variable;
        telegram[4] = value;
        telegram[5] = calculateChecksum(telegram);

        serialWrite(telegram);
    }

    /**
     * Send one telegram to the master unit.
     * Example: send(Variable.FAN_SPEED.key, Telegram.convertBackFanSpeed((byte)4));
     * 
     * @param variable one of the Variable keys in {@link Variable}}
     * @param value the right binary coded value (see {@link Telegram} for some conversions)
     * @throws IOException
     */
    public void send(byte variable, byte value) throws IOException {
        send(variable, value, ValloxProtocol.ADDRESS_MASTER);
    }

    private void serialWrite(byte[] telegram) throws IOException {
        if (outputStream != null) {
            outputStream.write(telegram);
            outputStream.flush();
        }
    }

    static byte calculateChecksum(byte[] pTelegram) {
        int checksum = 0;
        for (byte i = 0; i < pTelegram.length - 1; i++) {
            checksum += pTelegram[i];
        }
        return (byte) (checksum % 256);
    }

    /**
     * Start listening to telegrams on the serial interface. Stop if requested.
     */
    public void startListening() {
        shutdownListening = false;
        listenerExecutor = Executors.newSingleThreadExecutor();
        listenerExecutor.submit(() -> {
            while (!shutdownListening) {
                try {
                    Telegram telegram = receive();
                    if (telegram == null) {
                        // telegram too short or invalid
                        continue;
                    }
                    System.out.println(telegram);
                    telegram.updateStore(vallox);
                } catch (Exception e) {
                    System.out.println("Exception reading input stream: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        });
    }

    /**
     * Read one telegram.
     * 
     * @return the received telegram or null if no telegram could be read
     * @throws IOException
     * @throws InterruptedException
     */
    private Telegram receive() throws IOException, InterruptedException {
        int available = inputStream.available();
        if (available < ValloxProtocol.LENGTH) {
            Thread.sleep(200);
            return null;
        }
        int domain = inputStream.read();
        if (domain != ValloxProtocol.DOMAIN) {
            System.out.println("No Domain");
            return null;
        }

        // now we're happy -> read the telegram
        int sender = inputStream.read();
        int receiver = inputStream.read();
        int command = inputStream.read();
        int arg = inputStream.read();
        int checksum = inputStream.read();
        int computedChecksum = (domain + sender + receiver + command + arg) & 0x00ff;

        if (checksum != computedChecksum) {
            System.out.println("Invalid");
            return null;
        }

        // only read telegrams that are for us
        if (receiver == receiverID || receiver == senderID || receiver == ValloxProtocol.ADDRESS_PANELS) {
            return new Telegram((byte) sender, (byte) receiver, (byte) command, (byte) arg);
        }
        return null;
    }

    public void stopListening() {
        if (listenerExecutor != null) {
            try {
                System.out.println("attempt to shutdown listener");
                shutdownListening = true;
                listenerExecutor.shutdown();
                listenerExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("tasks interrupted");
            } finally {
                if (!listenerExecutor.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                listenerExecutor.shutdownNow();
                System.out.println("shutdown finished");
            }

        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Get a human readable string-representation in hex for an input
     * binary array of bytes.
     * 
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
