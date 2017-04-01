import time
import pifacedigitalio as pfio
import DataChannel

#Declaring constants
serverIP = "192.168.2.76"


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

name='c\Coffee Maker'
myChannel = DataChannel.DataChannel(name)
trys=0
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

                


