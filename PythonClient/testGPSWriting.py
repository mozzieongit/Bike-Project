# Runs perfectly using Python 2.7.3 on Raspian.
# GPSD Official Documentation:
# http://www.catb.org/gpsd/gpsd_json.html

# Original GpsPoller script by Dan Mandle:
# http://www.danmandle.com/blog/getting-gpsd-to-work-with-python/
# Original readCoordinates funtion by recantha:
# https://github.com/recantha/picorder-v3/blob/master/picorder.py
# Additional modifications by Jacob Ilkka:
# http://blog.jacobilkka.com/python-gps-data-writer/
# https://gist.github.com/jilkka/c4d0d384ea659022e857046bbe0a5739


import os
import time
import math
import threading
from gps import *

gpsd = None  # seting the global variable


class GpsPoller(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        global gpsd  # bring it in scope
        gpsd = gps(mode=WATCH_ENABLE)  # starting the stream of info
        self.current_value = None
        self.running = True  # setting the thread running to true

    def run(self):
        global gpsd
        while gpsp.running:
            gpsd.next()  # this will continue to loop and grab EACH set of gpsd info to clear the buffer


if __name__ == '__main__':
    gpsp = GpsPoller()  # create the thread
    try:
        gpsp.start()  # start it up
        while True:

            def readCoordinates():
                lat = gpsd.fix.latitude
                lon = gpsd.fix.longitude
                speed = float("{0:.2f}".format(gpsd.fix.speed / .44704))  # convert to mph
                alt = float("{0:.2f}".format(gpsd.fix.altitude / .3048))  # convert to feet
                climb = float("{0:.2f}".format(gpsd.fix.climb / .3048))  # convert to ft/s
                track = gpsd.fix.track
                fixtype = gpsd.fix.mode

                if (math.isnan(lat)):
                    lat = "No fix"

                if (math.isnan(lon)):
                    lon = "No fix"

                if (math.isnan(speed)):
                    speed = "No fix"
                else:
                    speed = "%s mph" % speed

                if (math.isnan(alt)):
                    alt = "No fix"
                else:
                    alt = "%s ft" % alt

                if (math.isnan(climb)):
                    climb = "N/A"
                else:
                    climb = "%s ft/s" % climb

                if (math.isnan(track)):
                    track = "%s'" % track
                else:
                    track = "No Track"

                if fixtype == 1:
                    fixtype = "No Fix"
                else:
                    fixtype = "%sD" % fixtype

                coords = [lat, lon, alt, speed, climb, track, fixtype]

                return coords


            coords = readCoordinates()
            d = open('currentData.py', 'w')
            d.write('%s,%s' % (coords[0], coords[1]))
            d.close()

            #print "\n \n"

            latitude = coords[0]
            longitude = coords[1]
            altitude = coords[2]
            heading = coords[5]
            speed = coords[3]
            climb = coords[4]
            fi = coords[6]

            #print "Latitude:  ", latitude
            #print "Longitude: ", longitude
            #print "Elevation: ", altitude
            #print "Heading:   ", heading
            #print "Speed:     ", speed
            #print "Climb:     ", climb
            #print "Fix:       ", fi

            time.sleep(2)

    except (KeyboardInterrupt, SystemExit):  # when you press ctrl+c
        print "\nKilling Thread..."
        gpsp.running = False
        gpsp.join()  # wait for the thread to finish what it's doing
    print "Done.\nExiting."