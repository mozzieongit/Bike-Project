#!/usr/bin/env python
# CLIENT
# PYTHON 2.7

# TODO: line 32 - positionsueberpruefung fortfuehren

# Written by Jannik Peters 2017

# GPS Deleted

import RPi.GPIO as GPIO
import MFRC522
import socket
import sys

import threading
import time

ipaddress = '192.168.0.11'
port = 8080

GPIO.setwarnings(False)

class Client():
    al_state = False
    position = "53.53109,8.61237"
    position_old = "53.53109,8.61237"

    def _test_gps(self):
        while True:
            if Client.position != Client.position_old:
                sock.sendall("4\n")
                print >>sys.stderr, "Send 4"
                Client.position = "53.53109,8.61237"

    def test_gps(self):
        threading.Thread(target=self._test_gps).start()

    def _recv_tcp(self):

        while True:
            data = sock.recv(128)
            print(data)
            if data:
                if data[0] == '4':
                    print "Change State to True"
                    Client.al_state = True
                elif data[0] == '5':
                    print "Change State from", Client.al_state, "to False"
                    Client.al_state = False

    def recv_tcp(self):
        threading.Thread(target=self._recv_tcp).start()

    def _nfc_auth(self):
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
    def nfc_auth(self):
        threading.Thread(target=self._nfc_auth).start()

    def _scipt_gps(self):
        x = 15
        while True:
            time.sleep(x)
            Client.position = "53.53109,8.61238"
            x += 15

    def script_gps(self):
        threading.Thread(target=self._script_gps).start()


#Create Socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = (ipaddress, port)

print >>sys.stderr, 'connecting to %s port %s' % server_address

try:
    try:
        sock.connect(server_address)
        sock.sendall("11&ID&anderesPW&53.53109,8.61237\n")
        datalog = sock.recv(10)
        if datalog == "11":
            print >>sys.stderr, 'logged in successful'

            base = Client()
            t1 = base.recv_tcp()
            t2 = base.nfc_auth()
            t3 = base.test_gps()
            if sys.argv[1] == "script":
                t4 = base.script_gps()

            print("Started")
            t1.join()
            t2.join()
            t3.join()

        else:
            print >>sys.stderr, 'Wrong Login'
            next
    except:
        next

except:
    print >>sys.stderr, 'closing socket'
    sock.close()
