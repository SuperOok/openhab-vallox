package vallox;

public class Telegram {

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

	void updateStore(ValloxStore vallox) {
		byte variable = command;
		byte value = arg;

		if (variable == Variable.IOPORT_FANSPEED_RELAYS.key) {
			// TODO
			// convertIoPortFanSpeedRelays
		} else if (variable == Variable.IOPORT_MULTI_PURPOSE_1.key) {
			vallox.postHeatingOn = convertIoPortMultiPurpose1(value);
		} else if (variable == Variable.IOPORT_MULTI_PURPOSE_2.key) {
			// 1 1 1 1 1 1 1 1  0=0ff 1=on
			// | | | | | | | |
			// | | | | | | | +- 0 
			// | | | | | | +--- 1 damper motor position - 0=winter 1=season - readonly
			// | | | | | +----- 2 fault signal relay - 0=open 1=closed - readonly
			// | | | | +------- 3 supply fan - 0=on 1=off
			// | | | +--------- 4 pre-heating - 0=off 1=on - readonly
			// | | +----------- 5 exhaust-fan - 0=on 1=off
			// | +------------- 6 fireplace-booster - 0=open 1=closed - readonly 
			// +--------------- 7 
			//*pUnknown1 = (value & 0x01) != 0;
			vallox.damperMotorPosition = (value & 0x02) != 0;
			vallox.faultSignalRelayClosed = (value & 0x04) != 0;
			vallox.supplyFanOff = (value & 0x08) != 0;
			vallox.preHeatingOn = (value & 0x10) != 0;
			vallox.exhaustFanOff = (value & 0x20) != 0;
			vallox.firePlaceBoosterClosed = (value & 0x40) != 0;
			//*pUnknown2 = (value & 0x80) != 0;
		}
		else if (variable == Variable.INSTALLED_CO2_SENSORS.key) {
			// TODO
			// convertInstalledCO2Sensor
		}
		else if (variable == Variable.CURRENT_INCOMMING.key) {
			vallox.incommingCurrent = value;
		}
		else if (variable == Variable.LAST_ERROR_NUMBER.key) {
			vallox.lastErrorNumber = value;
		}
		else if (variable == Variable.POST_HEATING_ON_COUNTER.key) {
			// TODO
		}
		else if (variable == Variable.POST_HEATING_OFF_TIME.key) {
			// TODO
		}
		else if (variable == Variable.POST_HEATING_TARGET_VALUE.key) {
			// TODO
		}
		else if (variable == Variable.FLAGS_1.key) {
			// TODO
		}
		else if (variable == Variable.FLAGS_2.key) {
			// TODO
		}
		else if (variable == Variable.FLAGS_3.key) {
			// TODO
		}
		else if (variable == Variable.FLAGS_4.key) {
			// TODO
		}
		else if (variable == Variable.FLAGS_5.key) {
			// TODO
		}
		else if (variable == Variable.FLAGS_6.key) {
			// TODO
		}
		else if (variable == Variable.FIRE_PLACE_BOOSTER_COUNTER.key) {
			// TODO
		}
		else if (variable == Variable.MAINTENANCE_MONTH_COUNTER.key) {
			// TODO
		}
		else if (variable == Variable.FAN_SPEED.key) {
			vallox.fanSpeed = convertFanSpeed(value);
		}

		else if (variable == Variable.TEMP_OUTSIDE.key) {
			vallox.tempOutside = convertTemperature(value);
		}

		else if (variable == Variable.TEMP_EXHAUST.key) {
			vallox.tempExhaust = convertTemperature(value);
		}

		else if (variable == Variable.TEMP_INSIDE.key) {
			vallox.tempInside = convertTemperature(value);
		}

		else if (variable == Variable.TEMP_INCOMMING.key) {
			vallox.tempIncomming = convertTemperature(value);
		}
		else if (variable == Variable.SELECT.key) {
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
			vallox.powerState				= (value & 0x01) != 0;
			vallox.cO2AdjustState			= (value & 0x02) != 0;
			vallox.humidityAdjustState		= (value & 0x04) != 0;
			vallox.heatingState				= (value & 0x08) != 0;
			vallox.filterGuardIndicator		= (value & 0x10) != 0;
			vallox.heatingIndicator			= (value & 0x20) != 0;
			vallox.faultIndicator			= (value & 0x40) != 0;
			vallox.serviceReminderIndicator	= (value & 0x80) != 0;
		}
		else if (variable == Variable.HUMIDITY.key) {
			vallox.humidity = value;
		}
		else if (variable == Variable.BASIC_HUMIDITY_LEVEL.key) {
			vallox.basicHumidityLevel = value;
		}
		else if (variable == Variable.HUMIDITY_SENSOR1.key) {
			vallox.humiditySensor1 = value;
		}
		else if (variable == Variable.HUMIDITY_SENSOR2.key) {
			vallox.humiditySensor2 = value;
		}
		else if (variable == Variable.CO2_HIGH.key) {
			vallox.cO2High = value;
		}
		else if (variable == Variable.CO2_LOW.key) {
			vallox.cO2Low = value;
		}
		else if (variable == Variable.CO2_SET_POINT_UPPER.key) {
			vallox.cO2SetPointHigh = value;
		}
		else if (variable == Variable.CO2_SET_POINT_LOWER.key) {
			vallox.cO2SetPointLow = value;
		}
		else if (variable == Variable.FAN_SPEED_MAX.key) {
			vallox.fanSpeedMax = convertFanSpeed(value);
		}
		else if (variable == Variable.FAN_SPEED_MIN.key) {
			vallox.fanSpeedMin = convertFanSpeed(value);
		}
		else if (variable == Variable.DC_FAN_OUTPUT_ADJUSTMENT.key) {
			vallox.dCFanOutputAdjustment = value;
		}
		else if (variable == Variable.DC_FAN_INPUT_ADJUSTMENT.key) {
			vallox.dCFanInputAdjustment = value;
		}
		else if (variable == Variable.INPUT_FAN_STOP.key) {
			vallox.inputFanStopThreshold = convertTemperature(value);
		}
		else if (variable == Variable.HEATING_SET_POINT.key) {
			vallox.heatingSetPoint = convertTemperature(value);
		}
		else if (variable == Variable.PRE_HEATING_SET_POINT.key) {
			vallox.preHeatingSetPoint = convertTemperature(value);
		}
		else if (variable == Variable.HRC_BYPASS.key) {
			vallox.hrcBypassThreshold = convertTemperature(value);
		}
		else if (variable == Variable.CELL_DEFROSTING.key) {
			vallox.cellDefrostingThreshold = convertTemperature(value);
		}
		else if (variable == Variable.PROGRAM.key) {
			// 1 1 1 1 1 1 1 1
			// | | | | _______
			// | | | |     |  
			// | | | |     +--- 0-3 set adjustment interval of CO2 and %RH in minutes 
			// | | | |   
			// | | | |   
			// | | | | 
			// | | | +--------- 4 automatic RH basic level seeker state
			// | | +----------- 5 boost switch modde (1=boost, 0 = fireplace)
			// | +------------- 6 radiator type 0 = electric, 1 = water
			// +--------------- 7 cascade adjust 0 = off, 1 = on
			vallox.adjustmentIntervalMinutes = value & 0x0F;
			vallox.automaticHumidityLevelSeekerState = (value & 0x10) != 0;
			vallox.boostSwitchMode					 = (value & 0x20) != 0;
			vallox.radiatorType						 = (value & 0x40) != 0;
			vallox.cascadeAdjust					 = (value & 0x80) != 0;
		}
		else if (variable == Variable.PROGRAM2.key) {
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
		}
		else if (variable == Variable.SERVICE_REMINDER.key) {
			vallox.serviceReminder = value;
		}
		else if (variable == Variable.UNKNOWN.key) {
			// do nothing;
		}
		else if (variable == Variable.SUSPEND.key) {
			// // C02 communication starts: no tx allowed!
			vallox.suspended = true;
		}
		else if (variable == Variable.RESUME.key) {
			// // C02 communication ends: tx allowed!
			vallox.suspended = false;
		}
		else{
		// default:
		System.out.println("Unknown command received: "+this);
		}
	}

	public static byte convertTemperature(byte value) {
		int index = value & 0xff;
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
	 * @param value 1-8
	 * @return
	 */
	public static byte convertBackFanSpeed(byte value) {
		return ValloxProtocol.FAN_SPEED_MAPPING[value-1];
	}

	// 0xFF --> 8
	/**
	 * Convert a hex telegram command value to its speed number from 1 to 8.
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
	public static boolean convertIoPortMultiPurpose1(byte value)
		{
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
		return String.format("Telegram [sender=%s, receiver=%s, command=%s, arg=%s, variable=%s]",
				byteToHex(sender), byteToHex(receiver), byteToHex(command), byteToHex(arg), variableString);
	}

}
