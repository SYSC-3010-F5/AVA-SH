#!/usr/bin/python
from time import sleep

import subprocess

#to prevent SPI failures, reload spi drivers at the beginning
#only needed for Rpi2
#unload_spi = subprocess.Popen('sudo rmmod spi_bcm2708', shell=True, stdout=subprocess.PIPE)
#start_spi = subprocess.Popen('sudo modprobe spi_bcm2708', shell=True, stdout=subprocess.PIPE)
sleep(3)

#import spidev and open SPI to work with ADC on Gertboard
import spidev

loops = 600

spi = spidev.SpiDev()
#the ADC of the gertboard is on SPI channel 0
spi.open(0,0)
#select the channel of the AD: 0 for AD0, 1 for AD1
channel = 1

while True:
    #send start bit, sgl/diff, odd/sign, MSBF to SPI
    r = spi.xfer2([1, (2+channel)<<6, 0])
    #spi.xfer2 returns same number of 8bit bytes as sent
    #parse out the part of bits which includes the changing value of the sensor
    adc_value = ((r[1]&31) << 6) + (r[2] >> 2)
    #print analogue value
    print(adc_value)
    #delay after each print
    sleep(0.05)
    loops -= 1
