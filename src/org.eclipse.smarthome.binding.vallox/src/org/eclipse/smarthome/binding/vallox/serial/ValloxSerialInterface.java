package org.eclipse.smarthome.binding.vallox.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.binding.vallox.service.ValloxSerialHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger logger = LoggerFactory.getLogger(ValloxSerialHandler.class);

    byte senderID = ValloxProtocol.ADDRESS_PANEL8; // we send commands in the name of panel8 (29)
    byte receiverID = ValloxProtocol.ADDRESS_PANEL1; // we always listen for the telegrams between the master and the
                                                     // panel1!

    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;
    private ExecutorService listenerExecutor;
    private boolean shutdownListening = false;

    private List<ValueChangeListener> listener;
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

    public List<ValueChangeListener> getListener() {
        if (listener == null) {
            listener = new ArrayList<ValueChangeListener>();
        }
        return listener;
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
        logger.info("Connected to " + host + ":" + port);
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
                logger.info("Closed " + socket.getInetAddress() + ":" + socket.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a poll request to the vallox. Denoting the property will request a value update
     * from the vallox. It will answer with a corresponding telegram with the updated
     * value. However, this answer is an asynchronous callback and it cannot handle
     * multiple requests in too short time. It will then only process the latest
     * request correctly and answer a lot of garbage bytes before that.
     *
     * @param prop
     * @throws IOException
     */
    public void sendPoll(ValloxProperty prop) throws IOException {
        if (listenerExecutor == null || listenerExecutor.isTerminated()) {
            logger.error("Poll requested while no-one is listening for answers!");
        }
        Variable v = null;
        switch (prop) {
            case AverageEfficiency:
            case InEfficiency:
            case OutEfficiency:
                // calculated
                break;
            case AdjustmentIntervalMinutes:
            case AutomaticHumidityLevelSeekerState:
            case BoostSwitchMode:
            case CascadeAdjust:
            case RadiatorType:
            case Program:
                v = Variable.PROGRAM;
                break;
            case MaxSpeedLimitMode:
            case Program2:
                v = Variable.PROGRAM2;
                break;
            case CO2AdjustState:
            case PowerState:
            case HumidityAdjustState:
            case HeatingState:
            case FilterGuardIndicator:
            case HeatingIndicator:
            case FaultIndicator:
            case ServiceReminderIndicator:
            case SelectStatus:
                v = Variable.SELECT;
                break;
            case PostHeatingOn:
                v = Variable.IOPORT_MULTI_PURPOSE_1;
                break;
            case DamperMotorPosition:
            case FaultSignalRelayClosed:
            case SupplyFanOff:
            case PreHeatingOn:
            case ExhaustFanOff:
            case FirePlaceBoosterClosed:
                v = Variable.IOPORT_MULTI_PURPOSE_2;
                break;
            case BasicHumidityLevel:
                v = Variable.BASIC_HUMIDITY_LEVEL;
                break;
            case CellDefrostingThreshold:
                v = Variable.CELL_DEFROSTING;
                break;
            case CO2High:
                v = Variable.CO2_HIGH;
                break;
            case CO2Low:
                v = Variable.CO2_LOW;
                break;
            case CO2SetPointHigh:
                v = Variable.CO2_SET_POINT_UPPER;
                break;
            case CO2SetPointLow:
                v = Variable.CO2_SET_POINT_LOWER;
                break;
            case DCFanInputAdjustment:
                v = Variable.DC_FAN_INPUT_ADJUSTMENT;
                break;
            case DCFanOutputAdjustment:
                v = Variable.DC_FAN_OUTPUT_ADJUSTMENT;
                break;
            case FanSpeed:
                v = Variable.FAN_SPEED;
                break;
            case FanSpeedMax:
                v = Variable.FAN_SPEED_MAX;
                break;
            case FanSpeedMin:
                v = Variable.FAN_SPEED_MIN;
                break;
            case HeatingSetPoint:
                v = Variable.HEATING_SET_POINT;
                break;
            case HrcBypassThreshold:
                v = Variable.HRC_BYPASS;
                break;
            case Humidity:
                v = Variable.HUMIDITY;
                break;
            case HumiditySensor1:
                v = Variable.HUMIDITY_SENSOR1;
                break;
            case HumiditySensor2:
                v = Variable.HUMIDITY_SENSOR2;
                break;
            case IncommingCurrent:
                v = Variable.CURRENT_INCOMMING;
                break;
            case InputFanStopThreshold:
                v = Variable.INPUT_FAN_STOP;
                break;
            case IoPortMultiPurpose1:
                v = Variable.IOPORT_MULTI_PURPOSE_1;
                break;
            case IoPortMultiPurpose2:
                v = Variable.IOPORT_MULTI_PURPOSE_2;
                break;
            case LastErrorNumber:
                v = Variable.LAST_ERROR_NUMBER;
                break;
            case PreHeatingSetPoint:
                v = Variable.PRE_HEATING_SET_POINT;
                break;
            case ServiceReminder:
                v = Variable.SERVICE_REMINDER;
                break;
            case TempExhaust:
                v = Variable.TEMP_EXHAUST;
                break;
            case TempIncomming:
                v = Variable.TEMP_INCOMMING;
                break;
            case TempInside:
                v = Variable.TEMP_INSIDE;
                break;
            case TempOutside:
                v = Variable.TEMP_OUTSIDE;
                break;
            default:
                break;
        }
        if (v != null) {
            logger.debug("Sending Poll request for property {} with variable {}", prop, v);
            send(Variable.POLL.key, v.key);
        }
    }

    public void send(byte variable, byte value, byte destination) throws IOException {
        byte[] telegram = new byte[ValloxProtocol.LENGTH];

        telegram[0] = ValloxProtocol.DOMAIN;
        telegram[1] = senderID;
        telegram[2] = destination;
        telegram[3] = variable;
        telegram[4] = value;
        telegram[5] = calculateChecksum(telegram);

        logger.debug("sending telegram: {} {} {} {} {} {}", Telegram.byteToHex(telegram[0]),
                Telegram.byteToHex(telegram[1]), Telegram.byteToHex(telegram[2]), Telegram.byteToHex(telegram[3]),
                Telegram.byteToHex(telegram[4]), Telegram.byteToHex(telegram[5]));
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
                    logger.trace(telegram.toString());
                    telegram.updateStore(vallox, listener);
                } catch (Exception e) {
                    logger.error("Exception reading input stream: " + e.toString());
                    break;
                }
            }
        });
        logger.info("Start listening to vallox telegrams!");
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
            logger.warn("Received Telegram has no Domain byte, ignoring telegram: {}",
                    Telegram.byteToHex((byte) domain));
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
            logger.warn("Received Telegram has invalid checksum, ignoring telegram.");
            return null;
        }

        // only read telegrams that are for us
        if (receiver == receiverID || receiver == senderID || receiver == ValloxProtocol.ADDRESS_PANELS) {
            return new Telegram((byte) sender, (byte) receiver, (byte) command, (byte) arg);
        }
        logger.debug("Ignoring telegram not for us: {} {} {} {}", sender, receiver, command, arg);
        return null;
    }

    public void stopListening() {
        if (listenerExecutor != null) {
            try {
                logger.info("attempt to shutdown listener");
                shutdownListening = true;
                listenerExecutor.shutdown();
                listenerExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("Listener Thread interrupted");
            } finally {
                if (!listenerExecutor.isTerminated()) {
                    logger.warn("Listener Thread cancel non-finished tasks");
                }
                listenerExecutor.shutdownNow();
                logger.info("shutdown of vallox listener finished");
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
