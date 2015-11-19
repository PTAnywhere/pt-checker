"""
Created on 19/11/2015
@author: Aitor Gomez Goiri <aitor.gomez-goiri@open.ac.uk>
Module to check if a Packet Tracer instance is running.
"""

import subprocess
from time import time, sleep


def is_running(jar_path, hostname='localhost', port=39000, timeout=1.0, wait_between_retries=0.2, file=None, deviceToFind=None):
    current = time()
    ends_at = current + timeout
    while current<ends_at:
	args = ['java', '-jar', jar_path, hostname, str(port), '1']  # 1 ms: try it once
	if file:
	    args.append(file)
        if deviceToFind:
   	    args.append(deviceToFind)
    	ret = int( subprocess.check_output(args) )
	if ret!=-1: return True
   	current = time()
	if current<ends_at:
	    sleep(wait_between_retries)
    return False
