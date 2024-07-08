import socket
from enum import Enum
from dataclasses import dataclass


# ------------------ CONSTANTS ------------------

# IP POLLING FOR CLIENT (_SRVC_CLT_POL_IP)
#
#       Should the client script prompt for an IP?
#       This should be set to True only if a static IP is not available.
#
SRVC_CLT_POL_IP = True

# DEFAULT TCP RECV LENGTH (_SRVC_TCP_RECV_N)
#
#       Number of bytes that should be received by default in TCP communications.
#       This value should be at least the size of a standard header
#
SRVC_TCP_RECV_N = 256

SRVC_TCP_N_CONN = 10
SRVC_SL_TIMEOUT = 1

# -----------------------------------------------

# ------------------- HEADER --------------------

class H_TYPE(Enum):


# -----------------------------------------------

class TCP:
    # IP = socket.gethostbyname(socket.gethostname())
    IP = '0.0.0.0'  # Host on all IPs
    # Have a static IP ready for this server?
    # Host your server at that IP by modifying the above line as follows:
    # IP = '192.0.0.1'
    #
    # Of course, change the IP to your static IP.

    PORT = 12345
