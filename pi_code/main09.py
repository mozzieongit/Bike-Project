#!/usr/bin/env python
#CLIENT

# Written by Jannik Peters 2017

# With Parts (GPS Poller) by Dan Mandle http://dan.mandle.me September 2012
# License: GPL 2.0


import RPi.GPIO as GPIO
import MFRC522
import socket
import sys
import os
from gps import *

import threading
import time

gpsp = None
gpsd = None # Seting global variable --GPS

ipaddress = '192.168.0.11'

GPIO.setwarnings(False)

class GpsPoller(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        global gpsd #bring it in scope
        gpsd = gps(mode=WATCH_ENABLE) #starting stream of info
        self.current_value = None
        self.running = True #setting the thread running to true

    def run(self):
        global gpsd
        global gpsp
        while gpsp.running:
            gpsp.next()

class Client(threading.Thread):
    al_state = False

    def __init__(self, iD):
        threading.Thread.__init__(self)
        self.iD = iD
        global gpsd #bring it in scope
        gpsd = gps(mode=WATCH_ENABLE) #starting stream of info
        self.current_value = None
        self.running = True #setting the thread running to true
        
    def run(self):
        global gpsp
        
        if self.iD == 0:
            
            while True:
                data = sock.recv(128)
                print(data)
                if data:
                    if data[0] == '4':
                        print "Change State to True"
                        Client.al_state = True
                    elif data[0] == '5':
                        print "Cha to False"
                        Client.al_state = False
                        
        #---
        elif self.iD == 1:
            card_placed = False
            while True:
                try:
                    while True:
                        MIFAREReader = MFRC522.MFRC522()
                        authcode = [70, 65, 72, 82, 82, 65, 68, 67, 72] # die ersten 9 Ziffern sind der Authentifizierungscode

                        #Scan for cards
                        (status,TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)
                        #If a card is found
                        if status == MIFAREReader.MI_OK and card_placed == False:
                            card_placed = True
                            # Get the UID of the card
                            (status,uid) = MIFAREReader.MFRC522_Anticoll()
                            if status == MIFAREReader.MI_OK:
                                # This is the default key for authentication
                                key = [0xFF,0xFF,0xFF,0xFF,0xFF,0xFF]
                                # Select the scanned tag
                                MIFAREReader.MFRC522_SelectTag(uid)
                                # Authenticate
                                status = MIFAREReader.MFRC522_Auth(MIFAREReader.PICC_AUTHENT1A, 8, key, uid)
                                # Check if authenticated
                                if status == MIFAREReader.MI_OK:
                                    # Read block 8
                                    data = MIFAREReader.MFRC522_Read(8)
                                    if data[:9] == authcode:
                                        print >>sys.stderr, 'sending "Authentifiziert"'
                                        if Client.al_state:
                                            #print(Client.al_state)
                                            Client.al_state = False
                                            sock.sendall("3\n")
                                            #print(Client.al_state)
                                        else:
                                            Client.al_state = True
                                            sock.sendall("2\n")
                                        print >>sys.stderr, 'send State'
                                        time.sleep(1)
                                        #return "Authentifiziert"
                                    #elif ...
                        elif status != MIFAREReader.MI_OK:
                            card_placed = False
                except:
                    continue
        #---
        #   53.53109,8.61237
        elif self.iD == 2:
            global gpsp
            try:
                gpsp.start()
                while True:

                    print gpsd.fix.latitude,', ',gpsd.fix.longitude,' - Time: ',gpsd.utc
                    time.sleep(5)

            except (KeyboardInterrupt, SystemExit):
                print "\nKilling Thread..."
                gpsp.running = False
                gpsp.join()
            print "Done. \nExiting Thread..."

#-----
gpsp = GpsPoller()
                    
lock_me = threading.Lock()

#Create Socket

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = (ipaddress, 8080)
print >>sys.stderr, 'connecting to %s port %s' % server_address

while True:
    try:
        sock.connect(server_address)
        break
    except:
        next
sock.sendall("11&PiOne&ABC\n")
datalog = sock.recv(10)
if datalog == "11\n":
    print >>sys.stderr, 'logged in successful'

    t1 = Client(0) # RECV Message
    t2 = Client(1) # NFC
   #t3 = Client(2) # GPS Print

    t1.start()
    t2.start()
   #t3.start()

    while True:
        t2.join()
        sock.close()
        sock.connect(server_address)

    t1.join()
else:
    print >>sys.stderr, 'NOT Arrived'
print >>sys.stderr, 'closing socket'
sock.close()
