package org.eclipse.smarthome.binding.vallox.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.eclipse.smarthome.binding.vallox.serial.ValloxProperty;
import org.eclipse.smarthome.binding.vallox.serial.ValloxSerialInterface;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValloxSerialHandler extends BaseThingHandler implements ThingHandler {

    private Logger logger = LoggerFactory.getLogger(ValloxSerialHandler.class);
    private Thing thing;
    private ValloxSerialInterface vallox;

    public ValloxSerialHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void dispose() {
        logger.info("dispose()");
        if (vallox != null) {
            vallox.stopListening();
            vallox.close();
            vallox = null;
        }
    }

    @Override
    public void initialize() {
        logger.info("initialize()");
        updateStatus(ThingStatus.INITIALIZING);
        if (vallox == null) {
            vallox = new ValloxSerialInterface();
            try {
                Configuration configuration = getThing().getConfiguration();
                String host = (String) configuration.get(ValloxBindingConstants.PARAMETER_HOST);
                BigDecimal port = (BigDecimal) configuration.get(ValloxBindingConstants.PARAMETER_PORT);
                vallox.connect(host, port.intValue());
                vallox.startListening();
                updateStatus(ThingStatus.ONLINE);
            } catch (IOException e) {
                String message = "Failed to start connection to Vallox serial interface: " + e.toString();
                logger.error(message);
                this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, message);
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (vallox == null) {
            return;
        }
        try {
            ValloxProperty channelProperty = ValloxProperty.valueOf(channelUID.getId());
            if (command instanceof RefreshType) {
                switch (channelProperty) {
                    case FanSpeed:
                        logger.info("handleCommand: FanSpeed " + command);
                        this.updateState(channelUID, new DecimalType(vallox.getValloxStore().fanSpeed));
                        // vallox.send(Variable.FAN_SPEED.getKey(), Telegram.convertBackFanSpeed((byte) 6));
                        break;

                    default:
                        logger.info("handleCommand: Unknown: " + channelUID.getAsString() + " Command: " + command);
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to handle command to Vallox serial interface: " + e.getMessage());
        }

    }

    // @Override
    // public Thing getThing() {
    // logger.info("getThing");
    // return null;
    // }
    //
    // @Override
    // public void thingUpdated(Thing thing) {
    // logger.info("thingUpdated:"+thing);
    // }
    //
    // @Override
    // public void handleRemoval() {
    // logger.info("handleRemoval");
    // }
    //
    // @Override
    // public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
    // logger.info("handleConfigurationUpdate: "+configurationParameters);
    // }
    //

    //
    // @Override
    // public void setCallback(ThingHandlerCallback thingHandlerCallback) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void channelLinked(ChannelUID channelUID) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void channelUnlinked(ChannelUID channelUID) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void bridgeHandlerInitialized(ThingHandler thingHandler, Bridge bridge) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void bridgeHandlerDisposed(ThingHandler thingHandler, Bridge bridge) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void handleCommand(ChannelUID channelUID, Command command) {
    // // TODO Auto-generated method stub
    // }
    //
    // @Override
    // public void handleUpdate(ChannelUID channelUID, State newState) {
    // // TODO Auto-generated method stub
    //
    // }

}
