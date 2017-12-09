#!/usr/bin/env python
#SERVER wait and send

import socket
import sys
import threading

def wait_in():
    while True:
        data = connection.recv(1024)
        print(data)

def send_msg():
    while True:
        connection.sendall(raw_input('what to send: '))

ipadd = socket.gethostbyname(socket.gethostname())
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server_address = (ipadd, 8080)
print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)

t1 = threading.Thread(target=wait_in)
t2 = threading.Thread(target=send_msg)

sock.listen(1)

while True:
    #wait for connection
    print >>sys.stderr, 'waiting for a connection'
    connection, client_address = sock.accept()

    while True:
        try:
            print >>sys.stderr, 'connection from', client_address

            t1.start()
            t2.start()

            t1.join()
            t2.join()
            
        finally:
            #Clean up the connection
            connection.close()
            #sock.close() #selfadded

print("finished")
