import pygame
import time
import pifacedigitalio as pfio
import dataChannel

#Initalizations
connected=False  #default connection state
pfio.init()
pygame.mixer.init()
pygame.mixer.music.load('beep-01a.mp3')#MP3 file name

serverIP='192.168.2.100' #Set to IP of server
serverPort=3010 #set to Port of server

name='a\Alarm Controller' #base name, always need a\, rest is variable
myChannel=dataChannel.DataChannel(name, 3010)
compNumber=0 #number comparison
trys=0 #limits attempts to accsess server under same name
alarmOff=False #turn alarm off via message

#Define some variables

bit_counter = 0                     #The current bit
current_num = ""                    #The current number in binary
LED_array = [[6,0],[5,0],[4,0],[3,0]]        #The LED configuration array
Pin_array = [0,1,2,3]

#button polling
def checkButton():
	if(pfio.digital_read(1) != 0 or pfio.digital_read(2) != 0 or pfio.digital_read(3) != 0 or pfio.digital_read(0)!=0): #check all buttons
                return True
        else:
                return False
        


#Turn the LEDs on or off
def setLEDS(LEDS = []):
    LED_counter = 0
    for i in range (0,4):#go through all LEDs for binary
        pfio.digital_write(LEDS[i][0], LEDS[i][1])
    return

#Define the pin configuration by counting in binary and stripping out each bit, char by char.
def ledDim(sTime):
    sleepTime=int(sTime)/16.0 #magic number as number of binary values is const
    for LED_counter in range (0,16):#count to 15 binary
        ledOff()
        if checkButton()==True:
                break
        else:
                current_num = bin(LED_counter)[2:].zfill(4)#convert to binary and 0 pad

                for bit_counter in range (0,4):
                    LED_array[bit_counter][1] = int(current_num[bit_counter])

                setLEDS(LED_array)
                time.sleep(sleepTime)#sleep to ensure that takes full time till full lumanance
    return

#turn on all piface leds
def ledOn():
    for index in range(0,8): 
        pfio.digital_write(index, 1)
    return

#turn off all piface leds
def ledOff():
    for index in range(0,8):
        pfio.digital_write(index, 0)
    return

#check socket for packets during alarm
def checkNewPack():   
        try:
                data1, addr1 = myChannel.rcvPacket(.5)#check if packet waiting to be handeled
                return True
        except:
                return False
        return False

#alarm turn on, off by button push or packet
def alarmOn():
        while(checkButton()==False and checkNewPack()==False):
                pygame.mixer.music.play()

#passes data to proper method
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

connected = myChannel.connect(serverIP, serverPort, (name))#create channel
while(True):

        while(connected==True):
                data, addr = myChannel.rcvPacket(None)#wait forever for packet
                print('Connected to Terminal')
                opcode, info, extra = myChannel.Unpack(data)

                sortPack(info,extra)
        
        trys=trys+1#not connected, attempt to try again
        if(trys==100):
                print ('10 attemps, changing name')
                compNumber+=1
                newName=name+str(compNumber)
                connected = myChannel.connect(serverIP, serverPort, (newName))
                trys=0

                


