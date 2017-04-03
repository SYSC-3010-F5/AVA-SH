#!/usr/bin/env python

import RPi.GPIO as GPIO
import sys
import DataChannel
from time import sleep
import subprocess
import spidev

#initialize GPIO port for controlling coffee maker
GPIO.setmode(GPIO.BCM)
port = 4
GPIO.setup(port, GPIO.OUT)
GPIO.output(port, 0)

#initialize SPI for weight sensor on ADC channel 1
spi = spidev.SpiDev()
spi.open(0,0)
channel = 1

#CIRCUIT SETUP: COFFEE MAKER
#Connect GP4 to RLY1 on J4
#Connect coffee maker's relay ground to RLY1 on J6
#Connect power source: positive to any RPWR on J6, negative to any GND on board
#Connect coffeemaker's relay positive to power source

#CIRCUIT SETUP: WEIGHT SENSOR
#Connect the following pins: GP11-SCLK, GP10-MOSI, GP9-MISO, GP8-CSnA
#Connect power (red lead) to 5v pin on Gertboard
#Connect ground (grey lead) to any GND on Gertboard
#Connect purple lead to AD1

#this constant was found through testing
COFFEE_WEIGHT = 45

#values from the ADC tend to fluctuate by this length
ADC_FLUCTUATION = 15

def coffeeOn():
    #get two weight samples
    weight1 = getWeight()
    sleep(0.5)
    weight2 = getWeight()

    #wait until a stable weight is gotten
        #wait until 3 samples that are relatively close are obtained in a row
    samples = 0
    while(samples < 3):
        if(abs(weight1 - weight2) < ADC_FLUCTUATION):
            samples += 1
        else:
            samples = 0
        weight1 = weight2
        weight2 = getWeight()
        #if the analog value is less than 200, it was probably an error
        if(weight2 < 200):
            continue
        sleep(1)
        print(weight2)
    #end while

    initialWeight = weight2
    print("Initial weight: %d" % initialWeight)
    #turn on relay
    GPIO.output(port, 1)
    
    weight = getWeight()
    #begin polling the weight sensor
    try:
        #wait until the cup is full
        #wait until a valid full weight is read 3 times in a row
        fullSamples = 0
        while(fullSamples < 3):
            sleep(2)
            weight = getWeight()
            print(weight)
            if(weight > initialWeight + COFFEE_WEIGHT):
                fullSamples += 1
            else:
                fullSamples = 0
        #end while
        coffeeOff()
    except KeyboardInterrupt:
        #acts as an emergency stop using CTRL-C
        coffeeOff()
        GPIO.cleanup()

def coffeeOff():
    GPIO.output(port, 0)

def getWeight():
    #send start bit, sgl/diff, odd/sign, MSBF to SPI
    r = spi.xfer2([1, (2+channel)<<6, 0])
    #spi.xfer2 returns same number of 8bit bytes as sent
    #parse out the part of bits which includes the changing value of the sensor
    adc_value = ((r[1]&31) << 6) + (r[2] >> 2)
    #return analogue value
    return adc_value

def sortPack(info, extra):
        if(info=='coffee on'):
                coffeeOn()
        elif(info=='coffee off'):
                coffeeOff()

GPIO.cleanup()

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
    coffeeOff()
    GPIO.cleanup()

