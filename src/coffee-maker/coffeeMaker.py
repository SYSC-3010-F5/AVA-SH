#!/usr/bin/env python

import RPi.GPIO as GPIO
import sys
import DataChannel
from time import sleep

GPIO.setmode(GPIO.BCM)
port = 4
GPIO.setup(port, GPIO.OUT)
GPIO.output(port, 0)

#CIRCUIT SETUP
#Connect GP4 on J2 to RLY1 on J4
#Connect coffee maker's relay ground to RLY1 on J6
#Connect power source: positive to any RPWR on J6, negative to any GND on board
#Connect coffeemaker's relay positive to power source

def coffeeOn():
    GPIO.output(port, 1)

def coffeeOff():
    GPIO.output(port, 0)

def sortPack(info, extra):
        if(info=='coffee on'):
                coffeeOn()
        elif(info=='coffee off'):
                coffeeOff()

 
name='c\Coffee Maker'
myChannel = DataChannel.DataChannel(name)
trys=0
try:
    while(True):
            connected = myChannel.connect(serverIP, 3010, name)
            time.sleep(1)
            while(connected==True):
                    data, addr = myChannel.rcvPacket(None)
                    opcode, info, extra = myChannel.Unpack(data)
                    sortPack(info,extra)
                    
            
            trys=trys+1
            if(trys==5):
                    print ('5 attemps, changing name')
                    name=name+'1'
                    print (name)
                    trys=0
                    time.sleep(0.5)
except KeyboardInterrupt:
    #reset GPIO ports after keyboard interrupt to close (CTRL-C)
    GPIO.cleanup()

