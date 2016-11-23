# openhab-vallox
Interface to a vallox central venting unit.

Tested with 
* Vallox KWL 90 SE and 
* USR-TCP232-410 RS485-to-ethernet interface

## Prerequisite

This Openhab2 bundle so far only works with a RS485-to-ethernet gateway.
It does not support a local serial interface of the device executing OH2.

I do not have any experience with direct serial RS485 communication under Java and
do not have any device for testing it. So if you can contribute any code for direct
RS485 serial write and read, I might easily merge that.

## Configuration

Configure the serial interface such that it acts as TCP server on some port of your choice. 
Configure the serial side as 
* baudrate: 9600
* data size: 8 bit
* parity: none
* stop bits: 1 

## Install

1. run `mvn package`
2. in the bundle dir in the `target` directory the bundle binary `org.eclipse.smarthome.binding.vallox-2.0.0-SNAPSHOT.jar` will be compiled
3. take the jar-file and copy it to your Openhab2 karaf `deploy` directory
4. it should get picked off by karaf automatically and started
5. go into karaf console and check: `bundle:list`, it should show a `Vallox` bundle in active state
6. open paper ui in browser
7. check that Configuration -> Bindings lists the Vallox binding
8. go to Configuration -> Things
9. add Vallox KWL Serial Binding Thing (exactly one)
10. enter configuration parameters (host and port of serial gateway)
11. The thing should show status `ONLINE`; if not, go into the details of the thing. The status there should include some error message.
12. make sure you create items for all the many channels of the binding
  * you can use [demo.items](src/org.eclipse.smarthome.binding.vallox/conf/demo.items) to copy/paste the available items to your own item file
13. make the items available in some sitemap and you should be able to control the vallox via the OH2 UIs
  * you can use [demo.sitemap](src/org.eclipse.smarthome.binding.vallox/conf/demo.sitemap) as an example for a sitemap

## Features

The binding currently supports the following Features
* 14 major properties, some read-only, some writeable
* 35 advanced properties, so far read-only most of them

Note that there might be more properties that can be read or written which might be not implemented yet. Contributions welcome.
Also not all channels are perfectly documented.
