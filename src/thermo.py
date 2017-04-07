import pygame
import time
import dataChannel

#Initalizations
connected=False
serverIP='192.168.2.100' #Set to IP of server
serverPort=3010 #set to Port of server
name='t\Thermostat' #thermostat modules alway begin with t\
myChannel=dataChannel.DataChannel(name, 55000)
compNumber=0
trys=0


# get current temp of system
def getTemp():
        print ('Current get temp:'+ str(temp))
        return temp 
    

def setTemp(newTemp):
        global temp
        temp=newTemp
        print ('Temperature changing to:'+ str(temp))

#turn system off (no longer monitoring
def systemOff():
        running=False
        print('System off')

def heatOn():#easily added in
        heater=True
        print('heating')
        temp+=1

def acOn():#easily added in
        ac=True
        print('System Cooling')
        temp-=1

    
#sort messages
def sortPack(info, extra):
        if(info=='new temp'):
                setTemp(extra)
        elif(info=='system off'):
                systemOff() 
    
connected = myChannel.connect(serverIP, serverPort, (name))#create channel
while(True):

        while(connected==True):
                data, addr = myChannel.rcvPacket(None)#wait forever for packet
                print('Connected to Terminal')
                opcode, info, extra = myChannel.Unpack(data)

                sortPack(info,extra)
        
        trys=trys+1#not connected, attempt to try again
        if(trys==100):
        else:
                print ('10 attemps, changing name')
                compNumber+=1
                newName=name+str(compNumber)
                connected = myChannel.connect(serverIP, serverPort, (newName))
                trys=0

                


