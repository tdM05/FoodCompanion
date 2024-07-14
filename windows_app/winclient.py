import socket, data as sc_data, rsa, sys, hashlib, json
from typing import Tuple, Dict, Any


class _ClientHelper:
    @staticmethod
    def connect_to_server(__ip: str, __port: int) -> socket.socket:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((__ip, __port))

        return sock

    @staticmethod
    def HDrHMFMTRecv(__conn: socket.socket) -> None | sc_data.Transmission:
        # 1. Receive the header and hash
        HDrHMFMTHeaderLen = sum([i.SIZE for i in sc_data.HeaderItems.items()])
        HDrHMBytes = __conn.recv(HDrHMFMTHeaderLen + sc_data.SHA3_256_HASH_SIZE)

        if HDrHMBytes == '' or len(HDrHMBytes) != (HDrHMFMTHeaderLen + sc_data.SHA3_256_HASH_SIZE):
            return None

        hdr_bytes = HDrHMBytes[:HDrHMFMTHeaderLen:]
        chk_bytes = HDrHMBytes[HDrHMFMTHeaderLen::]

        hdr = sc_data.HeaderUtils.load_header(hdr_bytes, sc_data.HEADER_PAD_BYTE)

        msg_bytes = __conn.recv(hdr.H_MSG_LEN)
        msg_hash = hashlib.sha256(msg_bytes).hexdigest().encode()

        if msg_hash != chk_bytes:
            sys.stderr.write("[E] Bad transmission.\n")
            return None

        return sc_data.Transmission(hdr, chk_bytes.decode(), msg_bytes)

    @staticmethod
    def setup_session(__socket: socket.socket) -> Tuple[
        None | str,
        None | rsa.PublicKey
    ]:
        __socket.send(sc_data.NEW_CONN_CODE + f'{sc_data.APP_VERSION}'.encode())
        rcv = __socket.recv(sc_data.SRVC_TCP_RECV_N)

        if sc_data.NEW_CONN_CODE not in rcv:
            return None, None

        ses_tok = rcv[len(sc_data.NEW_CONN_CODE) : len(sc_data.NEW_CONN_CODE) + sc_data.SESSION_TOKEN_SIZE].decode()
        pub_key = rcv[len(sc_data.NEW_CONN_CODE) + sc_data.SESSION_TOKEN_SIZE::]

        try:
            rsa_key = rsa.PublicKey.load_pkcs1(pub_key, "PEM")
            return ses_tok, rsa_key

        except Exception as _:
            return None, None


class WinClient:
    __ip = '127.0.0.1'
    __port = 12345

    __ses_tok: str           | None = None
    __pub_key: rsa.PublicKey | None = None

    @staticmethod
    def get_session() -> str | None:
        if WinClient.__ses_tok is None:
            WinClient.__ses_tok, WinClient.__pub_key = (
                _ClientHelper.setup_session(_ClientHelper.connect_to_server(WinClient.__ip, WinClient.__port))
            )

        return WinClient.__ses_tok

    @staticmethod
    def get_public_key() -> rsa.PublicKey | None:
        if WinClient.__pub_key is None:
            WinClient.__ses_tok, WinClient.__pub_key = (
                _ClientHelper.setup_session(_ClientHelper.connect_to_server(WinClient.__ip, WinClient.__port))
            )

        return WinClient.__pub_key

    @staticmethod
    def login(
            __inst_id: str,
            __p_dob: int,
            __p_uid: int
    ) -> str | Dict[str, Any]:
        session_token = WinClient.get_session()
        public_key = WinClient.get_public_key()

        iid = __inst_id.strip().upper()
        pid = str(__p_uid)
        pdb = str(__p_dob)

        assert session_token is not None and public_key is not None, "Could not establish a session with the server."
        assert len(iid),                                             "Invalid facility ID (C0)."
        assert len(pdb) == sc_data.DATE_FRMT_SIZE,                   "Invalid date of birth (C0)."
        assert len(pid) and __p_uid != 0,                            "Invalid patient ID (C0)."

        msg = rsa.encrypt(f'{iid}~{pdb}~{pid}'.encode(), public_key)
        hsh = hashlib.sha256(msg).hexdigest().encode()
        hdr = sc_data.HeaderUtils.create_bytes(msg, session_token, False)

        sock = _ClientHelper.connect_to_server(WinClient.__ip, WinClient.__port)
        sock.send(hdr + hsh + msg)

        rx = _ClientHelper.HDrHMFMTRecv(sock)
        assert isinstance(rx, sc_data.Transmission),                "Invalid response from server."

        rx_msg_str = rx.message.decode().strip()

        if rx_msg_str in sc_data.Errors.Errors:
            sys.stderr.write(f"[WinClient :: ERROR] SRV_ERR/{rx_msg_str}")
            return rx_msg_str

        try:
            return json.loads(rx_msg_str)

        except:
            return sc_data.Errors.BAD_TRANSMISSION
