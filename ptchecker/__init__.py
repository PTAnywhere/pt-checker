"""
Created on 19/11/2015
@author: Aitor Gomez Goiri <aitor.gomez-goiri@open.ac.uk>
Module to check if a Packet Tracer instance is running.
"""

import subprocess32 as subprocess
from time import time, sleep


def is_running(jar_path, hostname='localhost', port=39000, timeout=1.0, wait_between_retries=0.2, file_path=None, device_to_find=None):
    current = time()
    ends_at = current + timeout
    while current<ends_at:
        args = ['java', '-jar', jar_path, hostname, str(port), '1']  # 1 ms: try it once
        if file_path:
            args.append(file_path)
        if device_to_find:
            args.append(device_to_find)

        try:
            subprocess_timeout = ends_at - current
            # This timeout is used only to make sure that the Java program
            # doesn't stay blocked forever by mistake
            # (it shouldn't, but I cannot ensure that 'ptipc' does not get blocked).
            ret = int( subprocess.check_output(args, timeout=subprocess_timeout) )
            if ret != -1:
                return True
        except TimeoutExpired:
            pass

        current = time()
        if current < ends_at:
            sleep(wait_between_retries)
    return False
