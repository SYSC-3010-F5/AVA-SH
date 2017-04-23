# AVA-SH Packet Format

*Revision 2.1.1* 

*Jason Van Kerkhoven*

*Team F5*

*Revised on March 18th, 2017*

-----



## Abstract

More or less a slightly different flavor of UDP. This revision of the protocol removes safe-guards and checks for the following:
 
- Corruption of packet contents
- Packet duplication
- Packet loss
- Gross delays in packet delivery

This assumes the packets being sent will arrive with negligible delays. It also assumes no packet duplication, no packet corruption, and not packet loss.

Designed to allow 2-way point to point communication between the main AVA server and either a UI or a module. More generally, the module/interface will solely send packets to the server. The server will have a master registry of all devices that have paired with it, and can send to any of them.

The server registry, will contain the name of the module, as well its its physical location on the network (an IPv4 address and a socket).

This can be seen through:

	interface_1 <------> SERVER <------> module_1

There is no multi-packet data sent by design. All packets are of a maximum of 1kB (1024B) long. All packets are atomic (self-containing and independent), that is, data is never broken up over multiple packets. The maximum size of 1kB is arbitrary, and can be changed easily in future revisions if it proves too small to encapsulate all the data needed.



## Java Implementation

Two classes are used in order to transmit data:
 - `DataChannel.java`
 - `DataMultiChannel.java`

Additionally, the `ComsProtocol.java` interface outlines all the expected methods that must be present for the realization of this protocol.

The `DataChannel` class is used by modules and interfaces. Upon startup, the module/interface instantiates a `DataChannel` type object for all their communications. The `DataChannel` then attempts to register the module with the server master registry. The module sends a handshake packet to the well-known server IPv4 address on a well known socket. The handshake packet contains both the valid handshake key, and the string name for the module.

The server has a single instance the `DataMultiChannel` class running. Open receiving a packet, the packet is unpacked, and its contents saved. The server then checks the included packet handshake key against its copy of the handshake key. If they are identical, the registration process continues. If, however, the key is incorrect, an error-type packet is sent back to the module attempting registration. The error packet should not contain any information as to why/where the handshake key was incorrect. It should, however, clearly denote that the handshake key was incorrect.

Next, the server checks its master registry. If the device name is already registered (ie a different module has already been registered under the name the module attempting registration has requested), an error-type packet is sent, denoting the device name is already registered. Otherwise, if the name is not already registered, the module is added to the registry. It is registered under the name included in the handshake packet.

The server then send an empty handshake packet (ie two zero bytes) to the module, indicating it's successful registration.



## General Format

Each packet is a maximum of 1024 bytes. Multiple packets is not supported or necessary. Each packet can be divided into 2 sections, as follows:

		1byte                  max 1023 bytes
	|-----------|-------------------------------------------|
	|   TYPE    |                   DATA                    |
	|-----------|-------------------------------------------|

The **TYPE** denotes what is being sent. The **DATA** is the data that is sent, normally encoded as *UTF-8* characters.

To summarize, the lead byte of a packet are always reserved and used by the packet itself.




## Type Classifications

There are 5 types of packets that can be sent. Listed as follows:

| **Opcode** | **Packet** Type |
|--------|-------------|
| 0      | Handshake   |
| 1      | Command     |
| 2      | Info        |
| 3      | Error       |
| 4      | Disconnect  |

**Type 0** is used to establish an initial connection with the server.

**Type 1** is used to issue a command to the server (get the server to do something like make coffee, water plants, play music, etc). Similarly, certain commands (such as water plants) are forwarded by the server to a particular module.

**Type 2** is used to update the user on the progress of a task, or status of something in the system (example, the server would return a type 2 packet if the user requests the weather).

**Type 3** is sent to inform the user some error has occurred (either their command, type 1, packet is not recognized as a valid command), or something else has gone mildly or catastrophically wrong.

**Type 4** is used to disconnect with the server.



## Illegal Characters

Illegal characters must not be used in the any of the UTF-8 encoded sections of the packets.

To reiterate, `HANDSHAKE_KEY`, `DEVICE_NAME`, `COMMAND_KEY`, `EXTRA_INFO`, `INFO`, `ERROR_MESSAGE`, or `DISCONNECT_REASON` must not contain these characters:

|**UTF-8 CHARACTER**    |     **NAME**     |    **HEX VALUE**     |
|-------------------|--------------|------------------|
| \0                | null         | 0x00             |
| $                 | dollar sign  | 0x24             |



## Packet Formats

The notation used to describe packets should be observed for effective distillation of information. It should be noted that data fields, such as `HANDSHAKE_KEY`, and `COMMAND_KEY` will be denoted using pothole case, as well as using code block. Any variable data field size will be shown in dashes and italics, such as *-k-* or *-b-*.

### Type 0: Handshake

		   1 byte        <k> bytes		  1 byte            <n> bytes      
		|----------|-------------------|----------|--------------------------|
		|   0x00   |   HANDSHAKE_KEY   |   0x00   |        DEVICE_NAME       | 
		|----------|-------------------|----------|--------------------------|

The `TYPE` (static 1 byte size) denotes the type of packet being sent. Since this is a "Handshake" packet, the `TYPE` field will always be **0x00**.
		
The `HANDSHAKE_KEY` denotes the well-known byte pattern used to establish an initial connection and registration. As of version 2.1.0 of this format, the key is a 98 byte long character string (UTF-8), equal to:
>1: A robot may not injure a human being or, through inaction, allow a human being to come to harm.

It is of worth to note the `HANDSHAKE_KEY` cannot contain a **0x00** byte (null character in UTF-8), as this will result a premature termination of the `HANDSHAKE_KEY`. The `HANDSHAKE_KEY` takes up a variable *-k-* bytes. `HANDSHAKE_KEY` cannot not contain a "\" character (with the exception of in the prefix discussed later in the document).

The `HANDSHAKE_KEY` is followed by a terminating zero byte (**0x00**). The sole purpose of this byte is to denote a termination of the `HANDSHAKE_KEY`. This allows the `HANDSHAKE_KEY` to be freely changed (so long as both client and server are aware of what the active key is), and not be limited to a static byte size.

Following the terminating **0x00** byte, there is a variable *-i-* bytes long `DEVICE_NAME` field. The `DEVICE_NAME` is a string identifier for the device being paired. It is encoded as a UTF-8 character string. The length of the `DEVICE_NAME` field spans to the end of the packet.
		
The server response to a valid handshake packet is an empty handshake packet, that is, a packet where the `HANDSHAKE_KEY` and `DEVICE_NAME` fields are empty, such that:

				|------|------|
				| 0x00 | 0x00 |
				|------|------|

In order to keep track of various types of modules, a standard was added to `DEVICE_NAME`, so that the communication protocol need not be changed.
		
Each `DEVICE_NAME` field should begin with "..\", where ".." can be any single character, or, string of characters (UTF-8 encoding). This is used to denote what type of module a device is. The supported types of devices are included in the list below.

|  PREFIX  |                MODULE TYPE                 |
|----------|--------------------------------------------|
|  i\      |  any interface such as an app or terminal  |
|  c\      |  a coffee maker                            |
|  a\      |  an alarm controller                       |
|  m\      |  a media driver                            |
|  g\      |  a generic module                          |

The ..\ prefix for `DEVICE_NAME` is not strictly enforced, and can be neglected by modules. However, by doing this, there is no way of forwarding packets from server to a module (as forwarding requires a ..\ prefix on `DEVICE_NAME`).



### Type 1: Command

		   1 byte        <k> bytes	1 byte      <j> bytes       1 byte
		|----------|-----------------|----------|----------------|----------|
		|   0x01   |   COMMAND_KEY   |   0x00   |   EXTRA_INFO   |   0x00   |
		|----------|-----------------|----------|----------------|----------|

The `TYPE` (static 1 byte size) denotes the type of packet being sent. Since this is a "Command" packet, the TYPE field will always be a **0x01**.
		
The `COMMAND_KEY` denotes what you want the server to do. The `COMMAND_KEY` is a variable *-k-* bytes long. It must not contain  a **0x00** byte (null character). The `COMMAND_KEY` should be a short string of 8bit ASCII characters that allow the server to identify what action should be performed. For instance, playing a song could be given the key "play song", turning on a coffee machine could be given the key "make coffee", and so on and so forth.

After the `COMMAND_KEY`, there is a terminating **0x00** byte. This denotes the end of the command key. It is a static size of 1 byte.

Following the first terminating **0x00** byte, there is an optional field for additional information. This is up to the description of the command whether to use or require additional information. The `EXTRA_INFO` field should be a string of 8bit ASCII characters, formatted using JSON formatting style whenever complex.

For an example, setting an alarm might have the `COMMAND_KEY` "new alarm". The `EXTRA_INFO` field would contain the info needed to create a new alarm such as a time. day, and alarm title. 

A counter-example would be a `COMMAND_KEY` "light on". There is no more information needed, as it is all implicitly given through the `COMMAND_KEY`. Therefore, the `EXTRA_INFO` field would contain no information.

After the `EXTRA_INFO` field, there is a 2nd terminating **0x00** byte. This is to denote the end of the `EXTRA_INFO` field. If the `EXTRA_INFO` field is left empty, it will be directly after the 1st terminating **0x00**, so that the packet will read {**0x01**, `COMMAND_KEY`, **0x00**, **0x00**}. If the EXTRA_INFO field is not left empty, the packet will be read conventionally as the above diagram shows.



### Type 2: Info

		   1 byte                           <i> bytes
		|----------|--------------------------------------------------------|
		|   0x02   |                         INFO                           |
		|----------|--------------------------------------------------------|

The `TYPE` (static 1 byte size) denotes the type of packet being sent. Since this is a "Info" packet, the TYPE field will always be a **0x02**.
		
The rest of the data is encoded as an 8bit ASCII string of characters. It should be formatted such that it follows JSON formatting conversions. The INFO field spans to the end of the packet. The INFO field can be a variable *-i-* bytes long.



### Type 3: Error

		  1 byte                           <e> bytes
		|----------|--------------------------------------------------------|
		|   0x03   |                     ERROR_MESSAGE                      |
		|----------|--------------------------------------------------------|

The `TYPE` (static 1 byte size) denotes the type of packet being sent. Since this is a "Error" packet, the `TYPE` field will always be a **0x03**.

The rest of the data is encoded as an 8bit ASCII string of characters. It should accurately describe the error that occurred, as well as (if known) what caused said error to occur. If the system must shutdown/reboot as a result of this error, the message should contain that information. The `ERROR_MESSAGE` can be a variable *-e-* bytes long

Essentially, the error packet is exactly the same as a info packet, with the exception that the opcode is labeled as an error instead of regular informaton.




### Type 4: Disconnect

		  1 byte                           <d> bytes
		|----------|--------------------------------------------------------|
		|   0x04   |                  DISCONNECT_REASON                     |
		|----------|--------------------------------------------------------|

The `TYPE` (static 1 byte size) denotes the type of the packet being sent. Since this is a "Disconnect" packet, the `TYPE` field will always be a **0x04**.
		
The rest of the data is encoded as a string of UTF-8 encoded characters. It should denote the reason behind disconnecting (ie user request, module shutdown, module reset, etc). The `DISCONNECT_REASON` is not mandatory, and can be left empty. Disconnect packets are handled the same regardless of if there is a valid `DISCONNECT_REASON` field. The `DISCONNECT_REASON` field is a variable *-d-* bytes long.



