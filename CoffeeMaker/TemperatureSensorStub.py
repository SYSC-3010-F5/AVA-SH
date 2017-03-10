class TemperatureSensor:
    temp = 0    #water temperature in celsius

    def __init__(self):
        self.temp = 14
    
    def getTemp(self):
        if(temp < 90):
            self.temp = self.temp + 5
        return self.temp