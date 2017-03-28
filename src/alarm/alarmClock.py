import pygame
import time
import pifacedigitalio as pfio
import dataChannel

#Initalizations
connected=False
pfio.init()
pygame.mixer.init()
pygame.mixer.music.load('beep-01a.mp3')#MP3 file name

#used for server alarm off
alarmOff=False

#button poolling
def checkButton():
	if(pfio.digital_read(1) != 0 or pfio.digital_read(2) != 0 or pfio.digital_read(3) != 0):
                print('Button Pushed')
                return True
        else:
                return False
        
    
#bright/dimming for LED
#totalTime should be passed in seconds, as the sleep method takes in an int as seconds to sleep
def ledDim(totalTime):
         #using pins 0-3 for now, could be changed
	
         timeBetweenBrightnessLevels = totalTime/16
         
	 #starting from 1 as the initial brightness should be 1, not 0
	 #this code is for N-type MOSTFETs
	 #if P-types are used, use bitwise inverters (~) to set pins low rather than high
	 for i in range(1,15):
        	pfio.digital_write(0, i&1) #check if first bit is high
         	pfio.digital_write(1, i&2) #check if second bit is high
         	pfio.digital_write(2, i&4) #check if third bit is high
         	pfio.digital_write(3, i&8) #check if fourth bit is high
		#wait until it's time to go up one brightness level
		time.sleep(timeBetweenBrightnessLevels)
		
	 
	 ledOn()

def ledOn():
        #max brightness, send maximum current to the LED
	#this code is for N-type MOSFETs
	#if P-types are used, write 0 to the pins rather than 1
        pfio.digital_write(0,1)
        pfio.digital_write(1,1)
        pfio.digital_write(2,1)
        pfio.digital_write(3,1)

def ledOff():
        #turn off all transistors to turn off the LED
	#this code is for N-type MOSFETs
	#if P-types are used, write 1 to the pins rather than 0
        pfio.digital_write(0,0)
        pfio.digital_write(1,0)
        pfio.digital_write(2,0)
        pfio.digital_write(3,0)

#alarm turn on, off by button push
def alarmOn():
        while((not(checkButton())) and alarmOff==False):
                pygame.mixer.music.play()

def sortPack(info, extra):
        if(info=='led on'):
                ledOn()
        elif(info=='led off'):
                ledOff()
        elif(info=='led pwm'):
                ledDim(extra)
        elif(info=='alarm on'):
                alarmOn()
        elif(info=='alarm off'):
                alarmOff=True


for i in range(0,7,-1):
        pfio.digital_write(i,0)

myChannel=dataChannel.DataChannel()
name='Alarm Controller'
trys=0
ledOff()
while(True):
        connected = myChannel.connect("134.117.58.116", 3010, (name))

        while(connected==True):
                data, addr = myChannel.rcvPacket(None)
                opcode, info, extra = myChannel.Unpack(data)

                sortPack(info,extra)

                #ledDim(10)
                #connected=True
        
        trys=trys+1
        if(trys==100):
                print ('10 attemps, changing name')
                name=name+'1'
                print (name)
                trys=0

                


