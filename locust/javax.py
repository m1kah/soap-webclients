import time
import json
from locust import HttpUser, task, between

class Calculator(HttpUser):
    wait_time = between(1, 2)
    
    @task
    def sum(self):
        payload = {"x": 13, "y": 9}
        headers = {"Content-Type": "application/json"}
        self.client.post("/javax/sum", data = json.dumps(payload), headers = headers)
    
    @task
    def multiply(self):
        payload = {"x": 13, "y": 9}
        headers = {"Content-Type": "application/json"}
        self.client.post("/javax/multiply", data = json.dumps(payload), headers = headers)
