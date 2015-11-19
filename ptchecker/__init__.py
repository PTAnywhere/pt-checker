"""
Created on 19/11/2015
@author: Aitor Gomez Goiri <aitor.gomez-goiri@open.ac.uk>
Module to check if a Packet Tracer instance is running.
"""

import subprocess


def check(jar_path, hostname='localhost', port=39000, timeout=1.0, check_every=0.2, file=None, deviceToFind=None):
    ret = subprocess.check_output(['java', '-jar', jar_path, hostname, str(port), str(int(timeout))])
    return int(ret)
