# -*- coding: utf-8 -*-

import FB
import threading
import sys

class BuddyList ():

    def __init__ (self, name, password):
        self.fb = FB.FBHandler (name, password)
        self.updateBuddyList()
        self.showBuddyList()

        #inizializza il thread per la ricezione dei messaggi
        self.receive = receiveRoutine (self.fb)

    def updateBuddyList(self):
        b_array = []
        res = self.fb.buddy()

        if res["error"] != 0:
            print "error: " + str(res)
            return [("-1", ">> errore <<")]

        buddies = res["payload"]["buddy_list"]["userInfos"]
        
        for x,y in buddies.items():
            b_array.append ((x , y["name"]))

        self.buddyList = b_array

        return

        
    def showBuddyList (self):
        i = 0

        for x in self.buddyList:
            print str(i) + ": " + x[1]
            i+=1

        return

    def send (self, index, txt):
        res = self.fb.send(self.buddyList[index][0], txt)
        
        if res["error"] != 0:
            print ">> Errore nell'invio del messaggio: " + res["errorDescription"]
            print "\tcodice: " + str(res["error"])

        return

    def exitChat (self):
        sys.exit()

    #esegue il thread per la ricezione dei messaggi
    def startReceiveRoutine(self):
        self.receive.start()
        return


class receiveRoutine (threading.Thread):

    def __init__ (self, fb):
        threading.Thread.__init__(self)
        self.fb = fb

    def run (self):
        log = 0
        error = False

        while (True):
            res = {"t": "continue"}
            while (res["t"] == "continue"):
                    res = self.fb.receive()

            if res ["t"] == "msg":
                msg = res["ms"][0]

                #gestione dei tipi di messaggi:
                #messaggio ricevuto
                if msg["type"] == "msg":
                    print msg["from_name"] + " -> " + msg["to_name"] + ":"
                    print "\t" + msg["msg"]["text"]
                    print ""

                #un contatto sta scrivendo
                elif msg["type"] == "typ":
                    pass

                #operazioni eseguite nella finestra di facebook
                elif msg["type"] == "feedpub":
                    pass

                elif msg["type"] == "close_chat":
                    pass

                elif msg["type"] == "focus_chat":
                    pass

                elif msg["type"] == "unfocus_chat":
                    pass

                #messaggi delle notifiche
                elif msg["type"] == "notification":
                    pass

                elif msg["type"] == "notifications_read":
                    pass

                #messaggio da un'applicazione
                elif msg["type"] == "app_msg":
                    pass

                #codice imprevisto
                else:
                    error = True

            elif res["t"] == "refresh":
                pass

            else:
                error = True

            if error == True:
                print "Errore di ricezione: " + str(res)
                s = str(log) + ":\n\t" + str(res) + "\n\n"
                f = open ("debug", "a")
                f.write(s)
                f.close()
                error = False
        