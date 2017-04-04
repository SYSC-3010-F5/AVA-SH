import pygame
import time
import pifacedigitalio as pfio
import dataChannel

#Initalizations
connected=False
pfio.init()


#used for server alarm off
alarmOff=False


#button poolling
def checkButton():
	if(pfio.digital_read(1) != 0 or pfio.digital_read(2) != 0 or pfio.digital_read(3) != 0):
                print('Button Pushed')
                return True
        else:
                return False
def getTemp():
    myChannel.sendInfo(temp)
    
def setTemp(newTemp):
    temp=newTemp

def systemOff():
    running=False

def heatOn():
    heater=True

def acOn():
    ac=True
    
def statusCheck()
    

def sortPack(info, extra):
        if(info=='check temp'):
                getTemp()
        elif(info=='change temp'):
                setTemp(extra)
        elif(info=='off'):
                systemOff()
                
    

myChannel=dataChannel.DataChannel()
name='Alarm Controller'
compNumber=0
trys=0
ledOff()
while(True):
        connected = myChannel.connect("134.117.58.116", 3010, (name))

        while(connected==True):
                data, addr = myChannel.rcvPacket(None)
                opcode, info, extra = myChannel.Unpack(data)

                sortPack(info,extra)

                connected=True
                statusCheck()
        
        trys=trys+1
        if(trys==100):
                print ('10 attemps, changing name')
                compNumber+=1
                newName=name+str(compNumber)
                print newName
                trys=0

                


