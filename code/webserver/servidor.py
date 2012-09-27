"""
La Cosa Radioactiva 

Autores: 
    Sergio Galan Nieto. http://sergio.eclectico.net 
    Victor Diaz Barrales. http://www.victordiazbarrales.com

Este codigo tiene licencia GPL asi que ya sabes, si lo modificas tienes que compartir tus conocimientos con nostros! :)  
"""

from OSC import * 

import cherrypy
import simplejson
import string
import os
import json
import base64
from jinja2 import Environment, FileSystemLoader
from cherrypy.lib.static import serve_file 

env = Environment(loader=FileSystemLoader('templates'))


#file system change
import signal
import sys

import logging
import time


class Server:


  def __init__(self):
    self.indexText = None
    self.order="none" 

    cherrypy.config["tools.encode.on"] = True
    cherrypy.config["tools.encode.encoding"] = "utf-8"


  #show directories in the index 
  @cherrypy.expose 
  def index(self, name):
    dir = os.listdir(os.getcwd() + '/static')
    print "hola"
    #remote = cherrypy.request.headerMap.get("Remote-Addr", "") 
    #tmpl = env.get_template("index.html")
    #return tmpl.render(directories=dir)  
    #return open("./static/index.html")
    return serve_file(os.path.join(current_dir, name)) 
 
  #lo que recibe del control remoto 
  @cherrypy.expose		
  def sendosc(self, mAction = None):
    sendOSC(mAction)
    pass

        
import os.path


def setupOSC(): 
    client = OSCClient()
    client.connect( ('192.168.1.146', 12001) ) # note that the argument is a tupple and not two arguments

def sendOSC(num):
    msg = OSCMessage() #  we reuse the same variable msg used above overwriting it
    msg.setAddress("/lacosaradiactiva")
    msg.append(num)
    client.send(msg) # now we dont need to tell the client the address anymore 



if __name__ == '__main__':
    # CherryPy always starts with app.root when trying to map request URIs
    # to objects, so we need to mount a request handler root. A request
    # to '/' will be mapped to HelloWorld().index().
    tutconf = os.path.join(os.path.dirname(__file__), 'lacosaradiactiva.conf')
    current_dir = os.path.dirname(os.path.abspath(__file__))
    print current_dir
    print tutconf
    cherrypy.quickstart(Server(), config=tutconf )   
     
else:
    # This branch is for the test suite; you can ignore it.
    cherrypy.tree.mount(Server(), config=tutconf)


