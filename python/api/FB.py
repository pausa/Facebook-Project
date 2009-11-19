# -*- coding: utf-8 -*-
#################################
#                               #
#   API DI FACEBOOK IN PYTHON   #
#                               #
#################################

#inserimento dei moduli necessari
import time
import urllib2
import cookielib
import random
import re
import json
from urllib import urlencode
from HTMLParser import HTMLParser
from math import ceil

#ridenominazione dei metodi usati più di frequente
urlopen = urllib2.urlopen
Request = urllib2.Request
randrange = random.randrange

#ridefinizione dell'user-agent
AGENT =  {'User-Agent' : \
            'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.3) Gecko/20090913 Firefox/3.5.2'}
                
#indirizzi usati
FB_BASE = "http://www.facebook.com"
FB_LOGIN_PAGE = FB_BASE + "/login.php"
FB_HOME_PAGE = FB_BASE + "/home.php"
FB_BUDDY_UPDATE = FB_BASE + "/ajax/presence/update.php"
FB_SEND = FB_BASE + "/ajax/chat/send.php"
FB_CHAT_WIN = FB_BASE + "/presence/popout.php"

#limiti dei valori random
R1 = 1000000000
R2 = 9999999999

#inizializzazione del gestore dei cookies
cj = cookielib.CookieJar()
opener = urllib2.build_opener (urllib2.HTTPCookieProcessor(cj))
urllib2.install_opener(opener)


class FBHandler (HTMLParser):
    user = -1
    post_form_id = -1
    channel = -1
    seq = -1

    def __init__ (self, email, password):
        HTMLParser.__init__(self)
        data = self.login (email, password)
        self.populate(data)
        self.receive()

    #piccolo parser HTML per recuperare user_id e post_form_id
    def handle_starttag (self, tag, attrs):
    
        #check = False
        #if tag == "input":
            #for x,y in attrs:
                #if x.lower() == "id" and y.lower() == "user":
                    #check = true
                
                #if check == true and x.lower() == "value":
                    #self.user = y
                    #print "numero utente: ", y
                    #break
            
        check = False    
        if tag == "input":
            for x,y in attrs:
                if x.lower() == "id" and y.lower() == "post_form_id":
                    check = True
                    
                if check == True and x.lower() == "value":
                    self.post_form_id = y
                    print "post_form_id: ", y
                    break
                    
    def findChannel (self, data):
        exp = re.compile ("channel\d+")
        s = exp.search(data)
        self.channel = s.group()[7:9]
        print "channel: ", self.channel
        
    def findUser (self, data):
        exp = re.compile ('window.presence = new Presence\("\d+"')
        s = exp.search(data)
        print ">> ", s.group()
        up = s.group().find('"')
        down = s.group().rfind('"')
        self.user = s.group()[(up+1) : down]
        print "uid: ", self.user
        
    def populate (self, data):
        self.findUser(data)
        self.findChannel(data)
        self.feed(data)
        
    #restituisce il corpo della homepage di facebook
    def getPage (self, url):

        #viene costruita la richiesta
        print ">> ottenendo la pagina di facebook"
        req = Request (url,"",AGENT)
        p = urlopen (req)
        
        #restituisce il corpo
        return p.read().decode("utf8")

    #procedura di login, restituisce il corpo della pagina principale di fb
    def login (self, email, password):

        print ">> inizio la procedura di login"
        #parametri necessari per il login
        data = {"email" : email, "pass" : password}
        
        print ">> ottengo la pagina di login"
        #viene prima richiesta la pagina
        p = urlopen (FB_LOGIN_PAGE)
        
        #si effettua l'autenticazione tramite una POST request
        req = Request (FB_LOGIN_PAGE, urlencode(data), AGENT)
        
        print ">> invio la richiesta"
        #si richiede la pagina, dovrebbe restituire la homepage di fb
        p = urlopen (req)
        
        if p.geturl() == FB_LOGIN_PAGE:
            print ">> problemi di login\n"
        
        print ">> login effettuato"
        #restituisce il corpo della homepage
        return self.getPage(FB_CHAT_WIN)
        
    #procedura per ottenere la buddy list
    def buddy (self):

        #parametri necessari per fare la richiesta
        data = {"buddy_list" : "1", "force_render" : "1", \
                    "post_form_id" : self.post_form_id, "user": self.user}
        
        #si costruisce la stringa per la POST request
        req = Request (FB_BUDDY_UPDATE, urlencode (data), AGENT)
        
        #si ottiene il risultato
        p = urlopen(req)
        
        #restituisce la lista contenente la buddy list
        return json.loads(p.read().decode("utf8")[9:])

    #procedura per inviare messaggi
    def send (self, receiver, text):

        #parametri necessari per la richiesta
        data = {"client_time" : str(int(ceil(time.time() * 1000))),\
                "msg_id" : str(randrange(R1, R2)), "msg_text" : text, "popped_out" : \
                "true", "post_form_id" : self.post_form_id, "to" : receiver}
        
        #si costruisce la stringa per la POST request
        req = Request (FB_SEND, urlencode (data), AGENT)
        
        #si ottiene il risultato
        p = urlopen(req)
        
        #restituisce l'esito della richiesta
        return json.loads(p.read().decode("utf8")[9:])
        
    def receive (self):
                 
        p = urlopen ("http://0.channel" + self.channel + \
                        ".facebook.com/x/" + str(randrange(R1,R2)) + \
                        "/false/p_" + self.user + "=" + str(self.seq))

        res = json.loads (p.read().decode("utf8")[9:])
        
        if res["t"] == "refresh":
            self.seq = res["seq"]
            
        elif res["t"] != "continue":
            self.seq += 1;
        
        return res
        
        