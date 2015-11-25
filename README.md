# pt-checker
Python library (and Java dependency) to check that Packet Tracer is running on a given address and port.


## Usage

```
import ptchecker

is_running = ptchecker.is_running(jar_path, hostname, port, timeout, wait_between_retries, file_path, device_to_find)
if is_running:
    print "The Packet Tracer instance answered. I.e., it is running."
```

 * __jar\_path__ the path to the jar to be executed to contact Packet Tracer (see the _jPTChecker_ directory).
 * __hostname__ (optional, default: 'localhost') string with the name of the Packet Tracer instance host.
 * __port__ (optional, default: 39000) an int for the port number of the Packet Tracer instance.
 * __timeout__ (optional, default: 1.0) number of seconds while the program will retry connections.
 * __wait\_between\_retries__ (optional, default: 0.2) make a new attempt after the specified seconds.
 * __file_path__ (optional, default: None) file path in the _PT instance_ filesystem to be opened.
 * __device_to_find__ (optional, default: None) name of the device which should be found in the PT instance.
