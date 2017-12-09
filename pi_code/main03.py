#!/usr/bin/env python
#CLIENT

#sock.close in exception

import RPi.GPIO as GPIO
import MFRC522
import socket
import sys

import threading
import time

ipaddress = '192.168.43.199'

def cardcomp():
    while True:
        
        try:
            while True:
                MIFAREReader = MFRC522.MFRC522()
                authcode = [70, 65, 72, 82, 82, 65, 68, 67, 72] # die ersten 9 Ziffern sind der Authentifizierungscode

                #Scan for cards
                (status,TagType) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)
                #If a card is found
                if status == MIFAREReader.MI_OK:
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
                                sock.sendall("Authentifiziert\n")
                                print >>sys.stderr, 'send authentifiziert'
                                time.sleep(1)
                                #return "Authentifiziert"
                            #elif ...
        except:
            continue
# ...   

def recv_data():
    while True:
        time.sleep(0.25)
        data = sock.recv(1024)
        print(data)

#Create Socket

GPIO.setwarnings(False)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = (ipaddress, 8080)
print >>sys.stderr, 'connecting to %s port %s' % server_address
sock.connect(server_address)

t1 = threading.Thread(target=cardcomp)
t2 = threading.Thread(target=recv_data)

t1.start()
t2.start()

while True:
    t2.join()
    sock.close()
    sock.connect(server_address)

t1.join()
            
print >>sys.stderr, 'closing socket'
sock.close()
