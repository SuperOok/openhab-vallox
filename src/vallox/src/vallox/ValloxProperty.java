package vallox;

public enum ValloxProperty {
		// sensor data 
		FanSpeedProperty							,  // VALLOX_VARIABLE_FAN_SPEED
		TempInsideProperty							,  // VALLOX_VARIABLE_TEMP_INSIDE
		TempOutsideProperty							,  // VALLOX_VARIABLE_TEMP_OUTSIDE
		TempExhaustProperty							,  // VALLOX_VARIABLE_TEMP_EXHAUST
		TempIncommingProperty						,  // VALLOX_VARIABLE_TEMP_INCOMMING

		// status bits
		PowerStateProperty							,  // VALLOX_VARIABLE_SELECT
		CO2AdjustStateProperty						,  // VALLOX_VARIABLE_SELECT
		HumidityAdjustStateProperty					,  // VALLOX_VARIABLE_SELECT
		HeatingStateProperty						,  // VALLOX_VARIABLE_SELECT
		FilterGuardIndicatorProperty				,  // VALLOX_VARIABLE_SELECT
		HeatingIndicatorProperty					, // VALLOX_VARIABLE_SELECT
		FaultIndicatorProperty						, // VALLOX_VARIABLE_SELECT
		ServiceReminderIndicatorProperty			, // VALLOX_VARIABLE_SELECT

		HumidityProperty							, // VALLOX_VARIABLE_HUMIDITY
		BasicHumidityLevelProperty					, // VALLOX_VARIABLE_BASIC_HUMIDITY_LEVEL
		HumiditySensor1Property						, // VALLOX_VARIABLE_HUMIDITY_SENSOR1
		HumiditySensor2Property						, // VALLOX_VARIABLE_HUMIDITY_SENSOR2

		CO2HighProperty								, // VALLOX_VARIABLE_CO2_HIGH
		CO2LowProperty								, // VALLOX_VARIABLE_CO2_LOW
		CO2SetPointHighProperty						, // VALLOX_VARIABLE_CO2_SET_POINT_UPPER
		CO2SetPointLowProperty						, // VALLOX_VARIABLE_CO2_SET_POINT_LOWER

		FanSpeedMaxProperty							, // VALLOX_VARIABLE_FAN_SPEED_MAX
		FanSpeedMinProperty							, // VALLOX_VARIABLE_FAN_SPEED_MIN
		DCFanInputAdjustmentProperty				, // VALLOX_VARIABLE_DC_FAN_INPUT_ADJUSTMENT
		DCFanOutputAdjustmentProperty				, // VALLOX_VARIABLE_DC_FAN_OUTPUT_ADJUSTMENT
		InputFanStopThresholdProperty				, // VALLOX_VARIABLE_INPUT_FAN_STOP
		HeatingSetPointProperty						, // VALLOX_VARIABLE_HEATING_SET_POINT
		PreHeatingSetPointProperty					, // VALLOX_VARIABLE_PRE_HEATING_SET_POINT
		HrcBypassThresholdProperty					, // VALLOX_VARIABLE_HRC_BYPASS
		CellDefrostingThresholdProperty				, // VALLOX_VARIABLE_CELL_DEFROSTING

		// program
		AdjustmentIntervalMinutesProperty			, // VALLOX_VARIABLE_PROGRAM
		AutomaticHumidityLevelSeekerStateProperty	, // VALLOX_VARIABLE_PROGRAM
		BoostSwitchModeProperty						, // VALLOX_VARIABLE_PROGRAM
		RadiatorTypeProperty						, // VALLOX_VARIABLE_PROGRAM
		CascadeAdjustProperty						, // VALLOX_VARIABLE_PROGRAM

		// program2
		MaxSpeedLimitModeProperty					, // VALLOX_VARIABLE_PROGRAM2

		ServiceReminderProperty						, // VALLOX_VARIABLE_SERVICE_REMINDER

		// ioport multi purpose 1
		PostHeatingOnProperty						, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_1

		// ioport multi purpose 2
		DamperMotorPositionProperty					, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2
		FaultSignalRelayProperty					, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2
		SupplyFanOffProperty						, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2
		PreHeatingOnProperty						, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2
		ExhaustFanOffProperty						, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2
		FirePlaceBoosterOnProperty					, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2

		IncommingCurrentProperty					, // VALLOX_VARIABLE_CURRENT_INCOMMING
		LastErrorNumberProperty                     , // VALLOX_VARIABLE_LAST_ERROR_NUMBER


		// TODO: those variables are to be implemented in future
		//VALLOX_VARIABLE_IOPORT_FANSPEED_RELAYS
		//VALLOX_VARIABLE_INSTALLED_CO2_SENSORS
		//VALLOX_VARIABLE_POST_HEATING_ON_COUNTER
		//VALLOX_VARIABLE_POST_HEATING_OFF_TIME
		//VALLOX_VARIABLE_POST_HEATING_TARGET_VALUE
		//VALLOX_VARIABLE_FLAGS_1
		//VALLOX_VARIABLE_FLAGS_2
		//VALLOX_VARIABLE_FLAGS_3
		//VALLOX_VARIABLE_FLAGS_4
		//VALLOX_VARIABLE_FLAGS_5
		//VALLOX_VARIABLE_FLAGS_6
		//VALLOX_VARIABLE_FIRE_PLACE_BOOSTER_COUNTER
		//VALLOX_VARIABLE_MAINTENANCE_MONTH_COUNTER

		// calculated  properties
		InEfficiencyProperty			,
		OutEfficiencyProperty			,
		AverageEfficiencyProperty		,
		

		// virtual properties to be able to poll for this variable
		SelectStatusProperty			,
		ProgramProperty					, // VALLOX_VARIABLE_PROGRAM
		Program2Property				, // VALLOX_VARIABLE_PROGRAM2
		IoPortMultiPurpose1Property		, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_1
		IoPortMultiPurpose2Property		, // VALLOX_VARIABLE_IOPORT_MULTI_PURPOSE_2
}
