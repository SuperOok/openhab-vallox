package org.eclipse.smarthome.binding.vallox.serial;

import java.util.Collection;

import org.eclipse.smarthome.binding.vallox.service.ValloxSerialHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Telegram {

    private Logger logger = LoggerFactory.getLogger(ValloxSerialHandler.class);

    private byte sender;
    private byte receiver;
    private byte command;
    private byte arg;

    public Telegram(byte sender, byte receiver, byte command, byte arg) {
        this.sender = sender;
        this.receiver = receiver;
        this.command = command;
        this.arg = arg;
    }

    void updateStore(ValloxStore vallox, Collection<ValueChangeListener> listener) {

        byte variable = command;
        byte value = arg;

        if (variable == Variable.IOPORT_FANSPEED_RELAYS.key) {
            // TODO
            // convertIoPortFanSpeedRelays
        } else if (variable == Variable.IOPORT_MULTI_PURPOSE_1.key) {
            vallox.postHeatingOn = convertIoPortMultiPurpose1(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.IoPortMultiPurpose1));
        } else if (variable == Variable.IOPORT_MULTI_PURPOSE_2.key) {
            // 1 1 1 1 1 1 1 1 0=0ff 1=on
            // | | | | | | | |
            // | | | | | | | +- 0
            // | | | | | | +--- 1 damper motor position - 0=winter 1=season - readonly
            // | | | | | +----- 2 fault signal relay - 0=open 1=closed - readonly
            // | | | | +------- 3 supply fan - 0=on 1=off
            // | | | +--------- 4 pre-heating - 0=off 1=on - readonly
            // | | +----------- 5 exhaust-fan - 0=on 1=off
            // | +------------- 6 fireplace-booster - 0=open 1=closed - readonly
            // +--------------- 7
            // *pUnknown1 = (value & 0x01) != 0;
            vallox.damperMotorPosition = (value & 0x02) != 0;
            vallox.faultSignalRelayClosed = (value & 0x04) != 0;
            vallox.supplyFanOff = (value & 0x08) != 0;
            vallox.preHeatingOn = (value & 0x10) != 0;
            vallox.exhaustFanOff = (value & 0x20) != 0;
            vallox.firePlaceBoosterClosed = (value & 0x40) != 0;
            // *pUnknown2 = (value & 0x80) != 0;
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.DamperMotorPosition));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FaultSignalRelayClosed));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.SupplyFanOff));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.PreHeatingOn));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.ExhaustFanOff));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FirePlaceBoosterClosed));
        } else if (variable == Variable.INSTALLED_CO2_SENSORS.key) {
            // TODO
            // convertInstalledCO2Sensor
        } else if (variable == Variable.CURRENT_INCOMMING.key) {
            vallox.incommingCurrent = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.IncommingCurrent));
        } else if (variable == Variable.LAST_ERROR_NUMBER.key) {
            vallox.lastErrorNumber = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.LastErrorNumber));
        } else if (variable == Variable.POST_HEATING_ON_COUNTER.key) {
            // TODO
        } else if (variable == Variable.POST_HEATING_OFF_TIME.key) {
            // TODO
        } else if (variable == Variable.POST_HEATING_TARGET_VALUE.key) {
            // TODO
        } else if (variable == Variable.FLAGS_1.key) {
            // TODO
        } else if (variable == Variable.FLAGS_2.key) {
            // TODO
        } else if (variable == Variable.FLAGS_3.key) {
            // TODO
        } else if (variable == Variable.FLAGS_4.key) {
            // TODO
        } else if (variable == Variable.FLAGS_5.key) {
            // TODO
        } else if (variable == Variable.FLAGS_6.key) {
            // TODO
        } else if (variable == Variable.FIRE_PLACE_BOOSTER_COUNTER.key) {
            // TODO
        } else if (variable == Variable.MAINTENANCE_MONTH_COUNTER.key) {
            // TODO
        } else if (variable == Variable.FAN_SPEED.key) {
            vallox.fanSpeed = convertFanSpeed(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FanSpeed));
        }

        else if (variable == Variable.TEMP_OUTSIDE.key) {
            vallox.tempOutside = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.TempOutside));
            updateEfficiencies(vallox, listener);
        }

        else if (variable == Variable.TEMP_EXHAUST.key) {
            vallox.tempExhaust = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.TempExhaust));
            updateEfficiencies(vallox, listener);
        }

        else if (variable == Variable.TEMP_INSIDE.key) {
            vallox.tempInside = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.TempInside));
            updateEfficiencies(vallox, listener);
        }

        else if (variable == Variable.TEMP_INCOMMING.key) {
            vallox.tempIncomming = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.TempIncomming));
            updateEfficiencies(vallox, listener);
        } else if (variable == Variable.SELECT.key) {
            // 1 1 1 1 1 1 1 1
            // | | | | | | | |
            // | | | | | | | +- 0 Power state
            // | | | | | | +--- 1 CO2 Adjust state
            // | | | | | +----- 2 %RH adjust state
            // | | | | +------- 3 Heating state
            // | | | +--------- 4 Filterguard indicator
            // | | +----------- 5 Heating indicator
            // | +------------- 6 Fault indicator
            // +--------------- 7 service reminder
            vallox.powerState = (value & 0x01) != 0;
            vallox.cO2AdjustState = (value & 0x02) != 0;
            vallox.humidityAdjustState = (value & 0x04) != 0;
            vallox.heatingState = (value & 0x08) != 0;
            vallox.filterGuardIndicator = (value & 0x10) != 0;
            vallox.heatingIndicator = (value & 0x20) != 0;
            vallox.faultIndicator = (value & 0x40) != 0;
            vallox.serviceReminderIndicator = (value & 0x80) != 0;
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.PowerState));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CO2AdjustState));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HumidityAdjustState));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HeatingState));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FilterGuardIndicator));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HeatingIndicator));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FaultIndicator));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.ServiceReminderIndicator));
        } else if (variable == Variable.HUMIDITY.key) {
            vallox.humidity = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.Humidity));
        } else if (variable == Variable.BASIC_HUMIDITY_LEVEL.key) {
            vallox.basicHumidityLevel = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.BasicHumidityLevel));
        } else if (variable == Variable.HUMIDITY_SENSOR1.key) {
            vallox.humiditySensor1 = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HumiditySensor1));
        } else if (variable == Variable.HUMIDITY_SENSOR2.key) {
            vallox.humiditySensor2 = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HumiditySensor2));
        } else if (variable == Variable.CO2_HIGH.key) {
            vallox.cO2High = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CO2High));
        } else if (variable == Variable.CO2_LOW.key) {
            vallox.cO2Low = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CO2Low));
        } else if (variable == Variable.CO2_SET_POINT_UPPER.key) {
            vallox.cO2SetPointHigh = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CO2SetPointHigh));
        } else if (variable == Variable.CO2_SET_POINT_LOWER.key) {
            vallox.cO2SetPointLow = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CO2SetPointLow));
        } else if (variable == Variable.FAN_SPEED_MAX.key) {
            vallox.fanSpeedMax = convertFanSpeed(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FanSpeedMax));
        } else if (variable == Variable.FAN_SPEED_MIN.key) {
            vallox.fanSpeedMin = convertFanSpeed(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.FanSpeedMin));
        } else if (variable == Variable.DC_FAN_OUTPUT_ADJUSTMENT.key) {
            vallox.dCFanOutputAdjustment = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.DCFanOutputAdjustment));
        } else if (variable == Variable.DC_FAN_INPUT_ADJUSTMENT.key) {
            vallox.dCFanInputAdjustment = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.DCFanInputAdjustment));
        } else if (variable == Variable.INPUT_FAN_STOP.key) {
            vallox.inputFanStopThreshold = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.InputFanStopThreshold));
        } else if (variable == Variable.HEATING_SET_POINT.key) {
            vallox.heatingSetPoint = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HeatingSetPoint));
        } else if (variable == Variable.PRE_HEATING_SET_POINT.key) {
            vallox.preHeatingSetPoint = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.PreHeatingSetPoint));
        } else if (variable == Variable.HRC_BYPASS.key) {
            vallox.hrcBypassThreshold = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.HrcBypassThreshold));
        } else if (variable == Variable.CELL_DEFROSTING.key) {
            vallox.cellDefrostingThreshold = convertTemperature(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CellDefrostingThreshold));
        } else if (variable == Variable.PROGRAM.key) {
            // 1 1 1 1 1 1 1 1
            // | | | | _______
            // | | | | |
            // | | | | +--- 0-3 set adjustment interval of CO2 and %RH in minutes
            // | | | |
            // | | | |
            // | | | |
            // | | | +--------- 4 automatic RH basic level seeker state
            // | | +----------- 5 boost switch modde (1=boost, 0 = fireplace)
            // | +------------- 6 radiator type 0 = electric, 1 = water
            // +--------------- 7 cascade adjust 0 = off, 1 = on
            vallox.adjustmentIntervalMinutes = value & 0x0F;
            vallox.automaticHumidityLevelSeekerState = (value & 0x10) != 0;
            vallox.boostSwitchMode = (value & 0x20) != 0;
            vallox.radiatorType = (value & 0x40) != 0;
            vallox.cascadeAdjust = (value & 0x80) != 0;
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.AdjustmentIntervalMinutes));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.AutomaticHumidityLevelSeekerState));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.BoostSwitchMode));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.RadiatorType));
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.CascadeAdjust));
        } else if (variable == Variable.PROGRAM2.key) {
            // 1 1 1 1 1 1 1 1
            // | | | | | | | |
            // | | | | | | | +- 0 Function of max speed limit 0 = with adjustment, 1 = always
            // | | | | | | +--- 1
            // | | | | | +----- 2
            // | | | | +------- 3
            // | | | +--------- 4
            // | | +----------- 5
            // | +------------- 6
            // +--------------- 7
            vallox.maxSpeedLimitMode = (value & 0x01) != 0;
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.MaxSpeedLimitMode));
        } else if (variable == Variable.SERVICE_REMINDER.key) {
            vallox.serviceReminder = Byte.toUnsignedInt(value);
            listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.ServiceReminder));
        } else if (variable == Variable.UNKNOWN.key) {
            // do nothing;
        } else if (variable == Variable.SUSPEND.key) {
            // // C02 communication starts: no tx allowed!
            vallox.suspended = true;
        } else if (variable == Variable.RESUME.key) {
            // // C02 communication ends: tx allowed!
            vallox.suspended = false;
        } else {
            // default:
            logger.debug("Unknown command received: " + this);
        }
    }

    public void updateEfficiencies(ValloxStore vallox, Collection<ValueChangeListener> listener) {
        int maxPossible = vallox.tempInside - vallox.tempOutside;
        if (maxPossible <= 0) {
            vallox.inEfficiency = 100;
            vallox.outEfficiency = 100;
            vallox.averageEfficiency = 100;
        }
        if (maxPossible > 0) {
            double inEfficiency = (vallox.tempIncomming - vallox.tempOutside) * 100.0 / maxPossible;
            if (vallox.inEfficiency != (int) inEfficiency) {
                vallox.inEfficiency = (int) inEfficiency;
                listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.InEfficiency));
            }

            double outEfficiency = (vallox.tempInside - vallox.tempExhaust) * 100.0 / maxPossible;
            if (vallox.outEfficiency != (int) outEfficiency) {
                vallox.outEfficiency = (int) outEfficiency;
                listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.OutEfficiency));
            }

            double averageEfficiency = (vallox.inEfficiency + vallox.outEfficiency) / 2;
            if (vallox.averageEfficiency != (int) averageEfficiency) {
                vallox.averageEfficiency = (int) averageEfficiency;
                listener.stream().forEach(l -> l.notifyChanged(ValloxProperty.AverageEfficiency));
            }
        }
    }

    public static byte convertTemperature(byte value) {
        int index = Byte.toUnsignedInt(value);
        return ValloxProtocol.TEMPERATURE_MAPPING[index];
    }

    public static byte convertBackTemperature(byte temperature) {
        byte value = 100;

        for (int i = 0; i < 255; i++) {
            byte valueFromTable = ValloxProtocol.TEMPERATURE_MAPPING[i];
            if (valueFromTable >= temperature) {
                value = (byte) i;
                break;
            }
        }

        return value;
    }

    /**
     * Convert a speed number from 1 to 8 to its hex telegram command.
     * 8 --> 0xFF
     *
     * @param value 1-8
     * @return
     */
    public static byte convertBackFanSpeed(byte value) {
        return ValloxProtocol.FAN_SPEED_MAPPING[value - 1];
    }

    // 0xFF --> 8
    /**
     * Convert a hex telegram command value to its speed number from 1 to 8.
     *
     * @param value
     * @return 1-8
     */
    public static int convertFanSpeed(byte value) {
        int fanSpeed = 0;

        for (byte i = 0; i < 8; i++) {
            if (ValloxProtocol.FAN_SPEED_MAPPING[i] == value) {
                fanSpeed = (byte) (i + 1);
                break;
            }
        }
        return fanSpeed;
    }

    // 1 1 1 1 1 1 1 1
    // | | | | | | | |
    // | | | | | | | +- 0
    // | | | | | | +--- 1
    // | | | | | +----- 2
    // | | | | +------- 3
    // | | | +--------- 4
    // | | +----------- 5 post-heating on - 0=0ff 1=on - readonly
    // | +------------- 6
    // +--------------- 7
    public static boolean convertIoPortMultiPurpose1(byte value) {
        return (value & 0x20) != 0;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String byteToHex(byte b) {
        int v = b & 0xFF;
        char c1 = hexArray[v >>> 4];
        char c2 = hexArray[v & 0x0F];
        return "" + c1 + c2;
    }

    @Override
    public String toString() {
        String variableString = Variable.get(command).toString();
        return String.format("Telegram [sender=%s, receiver=%s, command=%s, arg=%s, variable=%s]", byteToHex(sender),
                byteToHex(receiver), byteToHex(command), byteToHex(arg), variableString);
    }

}
