# openhab-vallox
Interface to a vallox central venting unit.

Tested with 
* Vallox KWL 90 SE and 
* USR-TCP232-410 RS485-to-ethernet interface

## Configuration

Configure the serial interface such that it acts as TCP server on some port of your choice. 
Configure the serial side as 
* baudrate: 9600
* data size: 8 bit
* parity: none
* stop bits: 1 
