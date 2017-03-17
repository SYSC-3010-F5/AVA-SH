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
         print('starting pwm')
         while(True):
                for i in range(pwm*2):
                        pfio.digital_write(2,1)
                        time.sleep(i/pwm)
                        pfio.digital_write(2,0)
                        time.sleep(0.01)
                break
         return

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
name='AlarmClock'
trys=0
ledOff()
while(True):
        #connected = myChannel.connect("0.0.0.0", 3010, (name))

        while(connected==False):
                data, addr = myChannel.rcvPacket(10)
                opcode, info, extra = myChannel.Unpack(data)

                sortPack(info,extra)

                ledDim(10)
                connected=True
        
        trys=trys+1
        if(trys==100):
                print ('10 attemps, changing name')
                name=name+'1'
                print (name)
                trys=0

                


