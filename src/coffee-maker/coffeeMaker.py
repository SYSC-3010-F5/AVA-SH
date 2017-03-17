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

def coffeeOn():
        pfio.digital_write(1,1)

def coffeeOff():
        pfio.digital_write(1,0)



def sortPack(info, extra):
        if(info=='coffee on'):
                coffeeOn()
        elif(info=='coffee off'):
                coffeeOff()


for i in range(0,7,-1):
        pfio.digital_write(i,0)

myChannel=dataChannel.DataChannel()
name='Coffee Maker'
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
                name=name+'1'
                print (name)
                trys=0

                


