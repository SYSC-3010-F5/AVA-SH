import socket
import sys

class DataChannel:
	TYPE_HANDSHAKE = 0
	TYPE_CMD = 1
	TYPE_INFO = 2
	TYPE_ERR = 3
	TYPE_DISCONNECT = 4
	MAX_PACKET_SIZE = 1024
	TIMEOUT_S = 4
	HANDSHAKE = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm."
	PORT = 3010
	connected = False
	pairedAddress = -1
	pairedPort = -1
	registeredName = ""
	gpSocket = 0
	
	def __init__(self):
		gpSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		gpSocket.bind(("", PORT))
		
	
	#generic accessors
	def getPairedAddress():
		return pairedAddress
	def getPairedPort():
		return pairedPort
	def getConnected():
		return connected
	def getLocalPort():
		return gpSocket.getsockname()[1]
	def getLocalAddress():
		return gpSocket.getsockname()[0]
	def getRegisteredName():
		return registeredName
	
	
	#close this data channel
	def close():
		gpSocket.close()
		connected = False
	
	#wait to receive a packet with a set timeout, in seconds
	#returns the data as well as source IP+port
	def rcvPacket(timeout):
		gpSocket.settimeout(timeout)
		data, addr = gpSocket.recvfrom(PORT)
		return [data, addr]
	
	#send data to IP
	def sendPacketByteArr(byteArr):
		if(connected):
			gpSocket.sendto(byteArr, (pairedAddress, pairedPort))
		else:
			print("Cannot send packet -- DataChannel not paired")
		
	#unpack a received packet according to our protocol
	def Unpack(packet):
		rawPacket = bytearray(packet)
		index = 1
		
		if (rawPacket[0] == TYPE_HANDSHAKE):
			handshakeKey = bytearray()
			deviceNameBytes = bytearray()
			#parse handshake key
			while(rawPacket[index] != 0):
				handshakeKey.append(rawPacket[index])
				index += 1
				
			#decode the bytes			
			handshake = handshakeKey.decode("utf-8")
			index += 1
			
			#parse device name
			while(rawPacket[index] != 0):
				deviceNameBytes.append(rawPacket[index])
				index += 1
				
			#decode the bytes			
			deviceName = deviceNameBytes.decode("utf-8")
			
			return [TYPE_HANDSHAKE, handshake, deviceName]
		
		if (rawPacket[0] == TYPE_CMD):
			commandKey = bytearray()
			extraInfo = bytearray()
			
			#parse the command key
			while(rawPacket[index] != 0x00):
				commandKey.append(rawPacket[index])
				index += 1
				
			#decode the bytes
			cmdKey = commandKey.decode("utf-8")
			index += 1
			
			#parse extra info
			while(rawPacket[index] != 0x00):
				extraInfo.append(rawPacket[index])
				index += 1
				
			#decode the bytes			
			info = extraInfo.decode("utf-8")
			
			return [TYPE_CMD, cmdKey, deviceName]
		
		if(rawPacket[0] == TYPE_INFO):
			infoBytes = bytearray()
			
			#parse the info string
			while(rawPacket[index] != 0x00):
				infoBytes.append(rawPacket[index])
				index += 1
			
			#decode the bytes	
			infoString = infoBytes.decode("utf-8")
			
			return [TYPE_INFO, infoString, 0x00]
			
		if(rawPacket[0] == TYPE_ERR):
			errBytes = bytearray()
			
			#parse the error message
			while(rawPacket[index] != 0x00):
				errBytes.append(rawPacket[index])
				index += 1
			
			#decode the bytes
			errString = errBytes.decode("utf-8")
			
			return [TYPE_ERR, errString, 0x00]
		
		if(rawPacket[0] == TYPE_DISCONNECT):
			reasonBytes = bytearray()
			
			#parse the reason to disconnect
			while(rawPacket[index] != 0x00):
				reasonBytes.append(rawPacket[index])
				index += 1
			
			#decode the bytes
			reasonString = reasonBytes.decode("utf-8")
			
			return [TYPE_DISCONNECT, reasonString, 0x00]
	
	def connect(sendAddress, sendPort, deviceName):
		sendBytes = bytearray()
		
		#create the handshake packet according to the defined protocol
		sendBytes.append(TYPE_HANDSHAKE)
		sendBytes.append(HANDSHAKE.encode("utf-8"))
		sendBytes.append(0x00)
		sendBytes.append(deviceName.encode("utf-8"))
		
		#send the handshake packet
		gpSocket.sendto(sendBytes, (sendAddress, sendPort))
		
		#wait 10 seconds for a response
		try:
			data, addr = rcvPacket(10)
		except socket.timeout:
			return
		
		if(data[0] == TYPE_HANDSHAKE & data[1] == 0x00):
			pairedPort = addr[1]
			pairedAddress = addr[0]
			connected = True
			registeredName = deviceName
		if(data[0] == TYPE_ERR):
			print(unpack(data)[1])
		else:
			print("Invalid response to handshake")
	
	def disconnect(reason):
		#create the disconnect packet
		data = bytearray()
		data.append(TYPE_DISCONNECT)
		data.append(reason.encode("utf-8"))
		#send the disconnect packet
		sendPacketByteArr(data)
		
		pairedPort = 0
		pairedAddress = 0
		connected = False
		registeredName = ""
		
	def respondHandshake(pairingAddress, pairingPort):
		connected = True
		pairedAddress = pairingAddress
		pairedPort = pairingPort
		
		#create the handshake response
		data = bytearray()
		data.append(TYPE_HANDSHAKE)
		data.append(0x00)
		sendPacketByteArr(data)
		
#MAIN
what = bytearray()
what.append(0x00)
what.extend("aaaa".encode("utf-8"))
what.append(0x00)
what.extend("BB8".encode("utf-8"))
what.append(0x00)
other = Unpack(what)
print(other)