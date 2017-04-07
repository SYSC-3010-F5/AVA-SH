import socket
import sys

class DataChannel():
	TYPE_HANDSHAKE = 0x00
	TYPE_CMD = 0x01
	TYPE_INFO = 0x02  
	TYPE_ERR = 0x03
	TYPE_DISCONNECT = 0x04
	MAX_PACKET_SIZE = 1024
	TIMEOUT_S = 4
	HANDSHAKE = "1: A robot may not injure a human being or, through inaction, allow a human being to come to harm."
	PORT = 3011
	connected = False
	pairedAddress ='134.117.58.116' #update if running as own class
	pairedPort = 3010
	registeredName = ""
	gpSocket = 0
	
	def __init__(self, deviceName, port):
		self.gpSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		self.gpSocket.bind(("", port))
		self.gpSocket.setblocking(1)
		self.registeredName = deviceName
	
	#generic accessors
	def getPairedAddress(self):
		return self.pairedAddress
	def getPairedPort(self):
		return self.pairedPort
	def getConnected(self):
		return self.connected
	def getLocalPort(self):
		return self.gpSocket.getsockname()[1]
	def getLocalAddress(self):
		return self.gpSocket.getsockname()[0]
	def getRegisteredName(self):
		return self.registeredName
	
	
	#close this data channel
	def close(self):
		self.gpSocket.close()
		self.connected = False
	
	#wait to receive a packet with a set timeout, in seconds
	#returns the data as well as source IP+port
	def rcvPacket(self, timeout):
		self.gpSocket.settimeout(timeout)
		data, addr = self.gpSocket.recvfrom(self.MAX_PACKET_SIZE)
		return [data, addr]
	
	#send data to IP
	def sendPacketByteArr(self, byteArr):
		if(self.connected):
			self.gpSocket.sendto(byteArr, (self.pairedAddress, self.pairedPort))
		else:
			print("Cannot send packet -- DataChannel not paired")
		
	#unpack a received packet according to our protocol
	def Unpack(self, packet):
		rawPacket = bytearray(packet)
		index = 1
		
		if (rawPacket[0] == self.TYPE_HANDSHAKE):
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
			
			return [self.TYPE_HANDSHAKE, handshake, deviceName]
		
		if (rawPacket[0] == self.TYPE_CMD):
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
			
			return [self.TYPE_CMD, cmdKey, info]
		
		if(rawPacket[0] == self.TYPE_INFO):
			infoBytes = bytearray()
			
			#parse the info string
			while(index < len(rawPacket)):
				infoBytes.append(rawPacket[index])
				index += 1
			#rawPacket.remove(0)
			#infoBytes.extend(rawPacket)
			
			
			#decode the bytes	
			infoString = infoBytes.decode("utf-8")
			
			return [self.TYPE_INFO, infoString, 0x00]
			
		if(rawPacket[0] == self.TYPE_ERR):
			errBytes = bytearray()
			
			#parse the error message
			while(index < len(rawPacket)):
				errBytes.append(rawPacket[index])
				index += 1
			
			#decode the bytes
			errString = errBytes.decode("utf-8")
			
			return [self.TYPE_ERR, errString, 0x00]
		
		if(rawPacket[0] == self.TYPE_DISCONNECT):
			reasonBytes = bytearray()
			
			#parse the reason to disconnect
			while(index < len(rawPacket)):
				reasonBytes.append(rawPacket[index])
				index += 1
			
			#decode the bytes
			reasonString = reasonBytes.decode("utf-8")
			
			return [self.TYPE_DISCONNECT, reasonString, 0x00]
	
	#attempt a connection, returns True if successful, False if not
	def connect(self, sendAddress, sendPort, deviceName):
		sendBytes = bytearray()
		
		#create the handshake packet according to the defined protocol
		sendBytes.append(self.TYPE_HANDSHAKE)
		sendBytes.extend(self.HANDSHAKE.encode("utf-8"))
		sendBytes.append(0x00)
		sendBytes.extend(deviceName.encode("utf-8"))
		#send the handshake packet
		self.gpSocket.sendto(sendBytes, (sendAddress, sendPort))
		
		#wait 10 seconds for a response
		try:
			data, addr = self.rcvPacket(10)
			print('Connection Established')
		except socket.timeout:
			return False
                rawData = bytearray(data)
		
		if(rawData[0] == 0x00):
                        if(rawData[1] == 0x00):
                                self.pairedPort = addr[1]
                                #print(addr[1])
                                self.pairedAddress = addr[0]
                                #print(addr[0])
                                self.connected = True
                                self.registeredName = deviceName
                                return True
		elif(rawData[0] == self.TYPE_ERR):
                        #print(self.Unpack(data)[1])
			return False
		else:
			print("Invalid response to handshake\n")
			print(rawData)
			return False
	
	def disconnect(self, reason):
		#create the disconnect packet
		data = bytearray()
		data.append(self.TYPE_DISCONNECT)
		data.extend(reason.encode("utf-8"))
		#send the disconnect packet
		self.sendPacketByteArr(data)
		
		self.pairedPort = 0
		self.pairedAddress = 0
		self.connected = False
		self.registeredName = ""
	
	#This class is meant for modules, so it does not need to respond to a handshake	
	#def respondHandshake(self, pairingAddress, pairingPort):
	#	self.connected = True
	#	self.pairedAddress = pairingAddress
	#	self.pairedPort = pairingPort
	#	
	#	#create the handshake response
	#	data = bytearray()
	#	data.append(self.TYPE_HANDSHAKE)
	#	data.append(0x00) 
	#	self.sendPacketByteArr(data)
		
	#send the command and extra info (should be formatted as string)
	def sendCmd(self, cmdKey, extraInfo):
		toSend = bytearray()
		toSend.append(self.TYPE_CMD)
		toSend.extend(cmdKey.encode("utf-8"))
		toSend.append(0x00)
		toSend.extend(extraInfo.encode("utf-8"))
		toSend.append(0x00)
		
		self.sendPacketByteArr(toSend)
		
	#send an info packet, info is a string
	def sendInfo(self, info):
		toSend = bytearray()
		toSend.append(self.TYPE_INFO)
		toSend.extend(info.encode("utf-8"))
		
		self.sendPacketByteArr(toSend)
	
	#send an error packet, errMsg is a string	
	def sendErr(self, errMsg):
		toSend = bytearray()
		toSend.append(self.TYPE_ERR)
		toSend.extend(errMsg.encode("utf-8"))
		
		self.sendPacketByteArr(toSend)
		

