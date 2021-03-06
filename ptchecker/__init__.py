"""
Created on 19/11/2015
@author: Aitor Gomez Goiri <aitor.gomez-goiri@open.ac.uk>
Module to check if a Packet Tracer instance is running.
"""
import subprocess32 as subprocess
from time import time, sleep


class TimeoutError(Exception):
    def __init__(self, hostname, port, time, attempts):
        self.hostname = hostname
        self.port = port
        self.time = time
        self.attempts = attempts

    def __str__(self):
        return 'Packet Tracer instance (%s:%d) did not answer (time: %.2f, attempts: %d).' % (self.hostname, self.port, self.time, self.attempts)


def is_running(jar_path, hostname='localhost', port=39000, timeout=1.0, wait_between_retries=0.2, file_path=None, device_to_find=None):
    try:
        get_roundtrip_time(jar_path, hostname, port, timeout, wait_between_retries, file_path, device_to_find)
        return True
    except TimeoutError:
        return False


def get_roundtrip_time(jar_path, hostname='localhost', port=39000, timeout=1.0, wait_between_retries=0.2, file_path=None, device_to_find=None):
    current = time()
    began_at = current
    ends_at = began_at + timeout
    attempts = 0
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
                # We could use ret, but this will only consider the time needed
                # in the last attempt.
                return time() - began_at
        except subprocess.TimeoutExpired:
            pass
        attempts += 1
        current = time()
        if current < ends_at:
            sleep(wait_between_retries)
    raise TimeoutError(hostname, port, timeout, attempts)
