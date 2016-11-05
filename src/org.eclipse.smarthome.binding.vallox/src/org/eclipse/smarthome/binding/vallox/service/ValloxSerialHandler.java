package org.eclipse.smarthome.binding.vallox.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.eclipse.smarthome.binding.vallox.serial.Telegram;
import org.eclipse.smarthome.binding.vallox.serial.ValloxProperty;
import org.eclipse.smarthome.binding.vallox.serial.ValloxSerialInterface;
import org.eclipse.smarthome.binding.vallox.serial.ValloxStore;
import org.eclipse.smarthome.binding.vallox.serial.ValueChangeListener;
import org.eclipse.smarthome.binding.vallox.serial.Variable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValloxSerialHandler extends BaseThingHandler implements ThingHandler, ValueChangeListener {

    private Logger logger = LoggerFactory.getLogger(ValloxSerialHandler.class);
    private ValloxSerialInterface vallox;

    public ValloxSerialHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void dispose() {
        logger.info("dispose()");
        if (vallox != null) {
            vallox.stopListening();
            vallox.getListener().remove(this);
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
                vallox.getListener().add(this);
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
                vallox.sendPoll(channelProperty);
            } else if (command instanceof DecimalType) {
                byte value = ((DecimalType) command).byteValue();
                logger.debug("Setting channel {} to value {}.", channelProperty, value);
                switch (channelProperty) {
                    case FanSpeed:
                        vallox.send(Variable.FAN_SPEED.key, Telegram.convertBackFanSpeed(value));
                        break;
                    case FanSpeedMax:
                        vallox.send(Variable.FAN_SPEED_MAX.key, Telegram.convertBackFanSpeed(value));
                        break;
                    case FanSpeedMin:
                        vallox.send(Variable.FAN_SPEED_MIN.key, Telegram.convertBackFanSpeed(value));
                        break;
                    case DCFanOutputAdjustment:
                        vallox.send(Variable.DC_FAN_OUTPUT_ADJUSTMENT.key, value);
                        break;
                    case DCFanInputAdjustment:
                        vallox.send(Variable.DC_FAN_INPUT_ADJUSTMENT.key, value);
                        break;
                    case HrcBypassThreshold:
                        vallox.send(Variable.HRC_BYPASS.key, Telegram.convertBackTemperature(value));
                        break;
                    case InputFanStopThreshold:
                        vallox.send(Variable.INPUT_FAN_STOP.key, Telegram.convertBackTemperature(value));
                        break;
                    case HeatingSetPoint:
                        vallox.send(Variable.HEATING_SET_POINT.key, Telegram.convertBackTemperature(value));
                        break;
                    case PreHeatingSetPoint:
                        vallox.send(Variable.PRE_HEATING_SET_POINT.key, Telegram.convertBackTemperature(value));
                        break;
                    case CellDefrostingThreshold:
                        vallox.send(Variable.CELL_DEFROSTING.key, Telegram.convertBackTemperature(value));
                        break;
                    default:
                        logger.warn("Trying to send set-command to read-only channel: {} Ignoring.", channelProperty);
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to handle command to Vallox serial interface: " + e.toString());
        }
    }

    @Override
    public void notifyChanged(ValloxProperty prop) {
        ValloxStore vs = vallox.getValloxStore();
        ChannelUID channel = new ChannelUID(this.getThing().getUID(), prop.toString());
        State state = null;
        switch (prop) {
            case AdjustmentIntervalMinutes:
                state = new DecimalType(vs.adjustmentIntervalMinutes);
                break;
            case AutomaticHumidityLevelSeekerState:
                state = getOnOff(vs.automaticHumidityLevelSeekerState);
                break;
            case AverageEfficiency:
                state = new DecimalType(vs.averageEfficiency);
                break;
            case BasicHumidityLevel:
                state = new DecimalType(vs.basicHumidityLevel);
                break;
            case BoostSwitchMode:
                state = getOnOff(vs.boostSwitchMode);
                break;
            case CascadeAdjust:
                state = getOnOff(vs.cascadeAdjust);
                break;
            case CellDefrostingThreshold:
                state = new DecimalType(vs.cellDefrostingThreshold);
                break;
            case CO2AdjustState:
                state = getOnOff(vs.cO2AdjustState);
                break;
            case CO2High:
                state = new DecimalType(vs.cO2High);
                break;
            case CO2Low:
                state = new DecimalType(vs.cO2Low);
                break;
            case CO2SetPointHigh:
                state = new DecimalType(vs.cO2SetPointHigh);
                break;
            case CO2SetPointLow:
                state = new DecimalType(vs.cO2SetPointLow);
                break;
            case DamperMotorPosition:
                state = getOnOff(vs.damperMotorPosition);
                break;
            case DCFanInputAdjustment:
                state = new DecimalType(vs.dCFanInputAdjustment);
                break;
            case DCFanOutputAdjustment:
                state = new DecimalType(vs.dCFanOutputAdjustment);
                break;
            case ExhaustFanOff:
                state = getOnOff(vs.exhaustFanOff);
                break;
            case FanSpeed:
                state = new DecimalType(vs.fanSpeed);
                break;
            case FanSpeedMax:
                state = new DecimalType(vs.fanSpeedMax);
                break;
            case FanSpeedMin:
                state = new DecimalType(vs.fanSpeedMin);
                break;
            case FaultIndicator:
                state = getOnOff(vs.faultIndicator);
                break;
            case FaultSignalRelay:
                state = getOnOff(vs.faultSignalRelayClosed);
                break;
            case FilterGuardIndicator:
                state = getOnOff(vs.filterGuardIndicator);
                break;
            case FirePlaceBoosterOn:
                state = getOnOff(vs.firePlaceBoosterClosed);
                break;
            case HeatingIndicator:
                state = getOnOff(vs.heatingIndicator);
                break;
            case HeatingSetPoint:
                state = new DecimalType(vs.heatingSetPoint);
                break;
            case HeatingState:
                state = getOnOff(vs.heatingState);
                break;
            case HrcBypassThreshold:
                state = new DecimalType(vs.hrcBypassThreshold);
                break;
            case Humidity:
                state = new DecimalType(vs.humidity);
                break;
            case HumidityAdjustState:
                state = getOnOff(vs.humidityAdjustState);
                break;
            case HumiditySensor1:
                state = new DecimalType(vs.humiditySensor1);
                break;
            case HumiditySensor2:
                state = new DecimalType(vs.humiditySensor2);
                break;
            case IncommingCurrent:
                state = new DecimalType(vs.incommingCurrent);
                break;
            case InEfficiency:
                state = new DecimalType(vs.inEfficiency);
                break;
            case InputFanStopThreshold:
                state = new DecimalType(vs.inputFanStopThreshold);
                break;
            case IoPortMultiPurpose1:
                state = new DecimalType(vs.ioPortMultiPurpose1);
                break;
            case IoPortMultiPurpose2:
                state = new DecimalType(vs.ioPortMultiPurpose2);
                break;
            case LastErrorNumber:
                state = new DecimalType(vs.lastErrorNumber);
                break;
            case MaxSpeedLimitMode:
                state = getOnOff(vs.maxSpeedLimitMode);
                break;
            case OutEfficiency:
                state = new DecimalType(vs.outEfficiency);
                break;
            case PostHeatingOn:
                state = getOnOff(vs.postHeatingOn);
                break;
            case PowerState:
                state = getOnOff(vs.powerState);
                break;
            case PreHeatingOn:
                state = getOnOff(vs.preHeatingOn);
                break;
            case PreHeatingSetPoint:
                state = new DecimalType(vs.preHeatingSetPoint);
                break;
            case Program:
                state = new DecimalType(vs.program);
                break;
            case Program2:
                state = new DecimalType(vs.program2);
                break;
            case RadiatorType:
                state = getOnOff(vs.radiatorType);
                break;
            case SelectStatus:
                state = new DecimalType(vs.selectStatus);
                break;
            case ServiceReminder:
                state = new DecimalType(vs.serviceReminder);
                break;
            case ServiceReminderIndicator:
                state = getOnOff(vs.serviceReminderIndicator);
                break;
            case SupplyFanOff:
                state = getOnOff(vs.supplyFanOff);
                break;
            case TempExhaust:
                state = new DecimalType(vs.tempExhaust);
                break;
            case TempIncomming:
                state = new DecimalType(vs.tempIncomming);
                break;
            case TempInside:
                state = new DecimalType(vs.tempInside);
                break;
            case TempOutside:
                state = new DecimalType(vs.tempOutside);
                break;
            default:
                logger.warn("Got update notification for unknown vallox property: {}", channel);
                break;
        }
        this.updateState(channel, state);
        logger.debug("Updated state for channel {} to {}", channel, state);
    }

    private static OnOffType getOnOff(boolean on) {
        if (on) {
            return OnOffType.ON;
        }
        return OnOffType.OFF;
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
