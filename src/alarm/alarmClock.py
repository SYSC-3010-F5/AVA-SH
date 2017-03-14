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
def ledDim(pwm):
         while(True):
                for i in range(100):
                        pfio.digital_write(2,1)
                        time.sleep(i/100)
                        pfio.digital_write(2,0)
                        time.sleep(i/100)

def ledOn():
        pfio.digital_write(2,1)

def ledOff():
        pfio.digital_write(2,0)

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

connected = myChannel.connect  ("192.168.3.2", 3010, "AlarmClock")

while(connected==False):
        
        
        data, addr = myChannel.rcvPacket(10)
        opcode, info, extra = myChannel.Unpack(data)

        sortPack(info,extra)
        


