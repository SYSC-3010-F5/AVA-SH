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

#Define some variables

#LED_counter = 0                     #The current number in decimal
bit_counter = 0                     #The current bit
current_num = ""                    #The current number in binary
LED_array = [[6,0],[5,0],[4,0],[3,0]]        #The LED configuration array


Pin_array = [0,1,2,3]

#button poolling
def checkButton():
	if(pfio.digital_read(1) != 0 or pfio.digital_read(2) != 0 or pfio.digital_read(3) != 0):
                print('Button Pushed')
                return True
        else:
                return False
        

#Reset all the pins to 0

#Turn the LEDs on or off
def setLEDS(LEDS = []):
    LED_counter = 0
    for i in range (0,4):
        pfio.digital_write(LEDS[i][0], LEDS[i][1])
    return

#Define the pin configuration by counting in binary and stripping out each bit, char by char.
def ledDim(sTime):
    sleepTime=sTime/16.0
    for LED_counter in range (0,16):
        ledOff()
        if checkButton()==True:
                break
        else:
                current_num = bin(LED_counter)[2:].zfill(4)

                for bit_counter in range (0,4):
                    LED_array[bit_counter][1] = int(current_num[bit_counter])

                setLEDS(LED_array)
                time.sleep(sleepTime)
    return

def ledOn():
    for index in range(0,8):
        pfio.digital_write(index, 1)
    return

def ledOff():
    for index in range(0,8):
        pfio.digital_write(index, 0)
    return


#alarm turn on, off by button push
def alarmOn():
        while(checkButton()==False and alarmOff==False):
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
compNumber=0
trys=0
while(True):
        connected = myChannel.connect("134.117.58.116", 3010, (name))

        while(connected==True):
                data, addr = myChannel.rcvPacket(None)
                opcode, info, extra = myChannel.Unpack(data)

                sortPack(info,extra)
        
        trys=trys+1
        if(trys==100):
                print ('10 attemps, changing name')
                compNumber+=1
                newName=name+str(compNumber)
                print newName
                trys=0

                


