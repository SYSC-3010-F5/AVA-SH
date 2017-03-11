class WeightSensor:
    weight = 0  #weight in grams
    
    def __init__(self):
        self.weight = 0
        
    def getWeight(self):
        if(self.weight < 700):
            self.weight = self.weight + 5
            
        return self.weight
        
