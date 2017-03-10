import pygame
import time
import pifacedigitalio as pfio

pfio.init()
pygame.mixer.init()
pygame.mixer.music.load('beep-01a.mp3')

def checkButton():
	if(pfio.digital_read(1) != 0 or pfio.digital_read(2) != 0 or pfio.digital_read(3) != 0):
                print('Button Pushed')
                return True
        else:
                return False
        
def ledDim():
         while(True):
                for i in range(100):
                        pfio.digital_write(2,1)
                        time.sleep(i/100)
                        pfio.digital_write(2,0)
                        time.sleep(i/100)
def soundAlarm():
        pygame.mixer.music.play()

for i in range(0,7,-1):
    pfio.digital_write(i,0)


try:
        button=False
        while(button==False):
                soundAlarm()
                button=checkButton()

except KeyboardInterrupt:
        print ('Closing Program')

