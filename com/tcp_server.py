"""
com/tcp_server.py

Author: Geetansh Gautam
Created July 7, 2024

For MediHacks 2024: The FoodCompanion App
Hosts a TCP server that clients connect to for personalized meal options depending
    on the patient's health condition(s).

The FoodCompanion app was contributed to by (GitHub usernames sorted alphabetically):
    https://github.com/elhadjisall
    https://github.com/GeetanshGautam0
    https://github.com/tdM05
    https://github.com/wesyuxd

"""
import traceback

import select, sys, hashlib, rsa, socket, re, random, json
from cliHelper import CLI
from time import sleep
from datetime import datetime
from threading import Thread, Event, Timer
from typing import cast, Type, Any, Dict, Tuple, List


try:
    from . import data as sc_data
    from . import db as sc_db

    _EXT_SC_CALL = True

except ImportError:
    import data as sc_data
    import db as sc_db

    _EXT_SC_CALL = False

# Server Management


class _S_THREAD(Thread):

    def __init__(self, *args, **kwargs) -> None:
        super(_S_THREAD, self).__init__(*args, **kwargs)
        self._done_event = Event()

    def done(self) -> None:
        self._done_event.set()

    @property
    def is_done(self) -> bool:
        return self._done_event.is_set()

    def join_sf(self, timeout: int = 0) -> None:
        try:
            self.join(timeout)
        except RuntimeError:
            pass


_SERVER_THREAD: _S_THREAD


class Server:
    __is_alive: bool = True
    __sessions: Dict[str, Tuple[rsa.PublicKey, rsa.PrivateKey]] = {}
    __cost_timer = 15

    @staticmethod
    def is_compatible(vi: int) -> bool:
        return vi >= 20240708000000

    @staticmethod
    def gen_ses_tok(__salt: Any) -> str:
        return hashlib.md5(
            f'{__salt}{datetime.now().strftime("%Y%M%D%H%m%S")}'.encode()
        ).hexdigest()

    def __init__(self, __ip: str, __port: int) -> None:
        self.__socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__net = (__ip, __port)
        self.__threads: Dict[str, _S_THREAD] = {}
        self.__connections: Dict[str, Tuple[socket.socket, Tuple[str, int], rsa.PublicKey | None, rsa.PrivateKey | None]] = {}
        self.__curr_task: Timer

        self.__shutdown = False

        self._net_check()

    def request_exit(self) -> None:
        global _SERVER_THREAD

        stdout("Submitting an exit request now...\n")

        Server.__is_alive = False
        _SERVER_THREAD.done()
        self.__curr_task.cancel()

        self._shutdown()

    def bind(self) -> None:
        self.__socket.bind(self.__net)
        self.__socket.listen(sc_data.SRVC_TCP_N_CONN)

        self._main_loop()

    def _net_check(self) -> None:
        if self.__net[0] != 'localhost':
            _nip = re.search(r'^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\.(?!$)|$)){4}$', self.__net[0])
            assert _nip is not None,                              f'Invalid Server IP "{self.__net[0]}".'

            self.__net = (_nip.string, self.__net[-1])

        assert isinstance(self.__net[-1], int),                   f'Invalid Server PORT "{self.__net[-1]}" (1).'
        assert (self.__net[-1] <= 65535) & (self.__net[-1] >= 0), f'Invalid Server PORT "{self.__net[-1]}" (2).'

    def _remove(self, __c_name) -> None:
        _sock = self.__connections.pop(__c_name)[0]
        _thread = self.__threads.pop(__c_name)

        try:
            _sock.close()
        except Exception:
            pass

        _thread.done()
        _thread.join_sf()

    def _clear_ost(self) -> None:
        for n, t in self.__threads.items():
            if t.is_done:
                self.__threads.pop(n)
                s = self.__connections.pop(n)[0] if n in self.__connections else None

                t.join_sf()

                if s is not None:
                    try:
                        s.close()
                    except Exception:
                        pass

        self.__curr_task = Timer(function=self._clear_ost, interval=Server.__cost_timer)
        self.__curr_task.start()

    def _main_loop(self) -> None:
        global _SERVER_THREAD

        self.__socket.setblocking(False)

        self.__curr_task = Timer(function=self._clear_ost, interval=Server.__cost_timer)
        self.__curr_task.start()

        while Server.__is_alive & (~_SERVER_THREAD.is_done):
            try:
                R, *_ = select.select(
                    [self.__socket, *[c[0] for c in self.__connections.values() if not c[0]._closed]],
                    [],
                    [],
                    sc_data.SRVC_SL_TIMEOUT
                )

            except:
                self._clear_ost()
                continue  # Go back to the top of the loop and try again.

            for r in R:
                if _SERVER_THREAD.is_done:
                    break

                if r == self.__socket:
                    _conn, _addr = cast(socket.socket, r).accept()

                    _conn.setblocking(True)

                    _c_name = '%s%d-%d' % (*cast(Tuple[str, int], _addr), random.randint(100, 999))

                    self.__connections[_c_name] = (_conn, cast(Tuple[str, int], _addr), None, None)  # type: ignore
                    self.__threads[_c_name] = _S_THREAD(target=self._handle_client, args=(_c_name, ))

                    self.__threads[_c_name].start()

        else:
            stdout('Server exiting main loop.\n')

        self._shutdown()

    def _handle_client(self, __c_name: str) -> None:
        stdout(f'[{__c_name}] Replying.\n')

        _conn, *_ = self.__connections[__c_name]
        _rcv = b'' + _conn.recv(sc_data.SRVC_TCP_RECV_N)

        if not _rcv:
            # stderr(f'[{__c_name}] Connection closed.\n')
            self._remove(__c_name)
            return

        stdout(f'[{__c_name}] Received "{_rcv}"\n')

        if sc_data.NEW_CONN_CODE in _rcv:
            try:
                self._new_client(_rcv, __c_name)
                _conn.close()

            except AssertionError as _AE:
                stderr(f'[{__c_name}] Invalid NW_CON request <CONN. ABORTED>: {str(_AE).split("//")[0].strip()}\n')
                _conn.send(str(_AE).split('//')[-1].strip().encode())
                self._remove(__c_name)

                return

            except Exception as E:
                stderr(f'[{__c_name}] Failed to handle NW_CON req: {E.__class__.__name__}({str(E)})')
                _conn.send(sc_data.Errors.GENERAL)

        elif _rcv[:sc_data.DATETIME_FRMT_SIZE].decode().isnumeric():  # The first part of the header must be the date
            try:
                self._old_client(_rcv, __c_name)
                _conn.close()

            except AssertionError as _AE:
                stderr(f'[{__c_name}] Invalid MEAL_OPTIONS request <CONN. ABORTED>: {str(_AE).split("//")[0].strip()}\n')
                _conn.send(str(_AE).split('//')[-1].strip().encode())
                self._remove(__c_name)

                return

            except Exception as E:
                stderr(f'[{__c_name}] Failed to handle MEAL_OPTIONS req: {E.__class__.__name__}({str(E)})')
                _conn.send(sc_data.Errors.GENERAL.encode())

        else:
            if b'GET' in _rcv and b'HTTP' in _rcv: # This is an HTTP request
                hdrs, sep, body = _rcv.partition(b'\r\n\r\n')
                hdrs = hdrs.decode()

                html_body = "<html><body><h1>FoodCompanion</h1><p>This is a TCP server, not an HTTP server.</p></body></html>"
                resp_hdrs = {
                    'Content-Type':     'text/html; encoding=utf8',
                    'Content-Length':   len(html_body),
                    'Connection':       'close'
                }

                resp_hdr_str = ''.join('%s: %s\r\n' % (h, v) for h, v in resp_hdrs.items())
                resp_proto = 'HTTP/1.1'
                resp_status = '200'
                resp_status_text = 'OK'

                _reply = ('%s %s %s\r\n' % (resp_proto, resp_status, resp_status_text)).encode()
                _conn.sendall(_reply)
                _conn.sendall(resp_hdr_str.encode())
                _conn.sendall(b'\r\n')
                _conn.sendall(html_body.encode())

                sleep(5)

            _conn.close()

        stdout(f'[{__c_name}] Done.\n\n')

        self.__threads[__c_name].done()
        self._remove(__c_name)

    def _new_client(self, __rcv: bytes, __c_name: str) -> None:
        assert len(__rcv) >= (len(sc_data.NEW_CONN_CODE) + sc_data.APP_VI_STRING_SIZE), f'Bad Request (0) // {sc_data.Errors.INCOMPLETE_MESSAGE}'

        cd = __rcv[:len(sc_data.NEW_CONN_CODE):]
        vi = int(__rcv[len(sc_data.NEW_CONN_CODE)::])

        assert cd == sc_data.NEW_CONN_CODE,                                             f'Bad Request (1) // {sc_data.Errors.BAD_REQUEST}'
        assert Server.is_compatible(int(vi)),                                           f'Incompatible client version. // {sc_data.Errors.CLIENT_VERSION}'

        # The app is compatible, make a session token, RSA encryption key, and send it over.
        _conn, _addr, *_ = self.__connections[__c_name]

        # Create the RSA key
        (__pb, __pr) = rsa.newkeys(512)
        _ses_tok = Server.gen_ses_tok(_addr[1])

        self.__connections[__c_name] = (_conn, _addr, __pb, __pr)
        self.__sessions[_ses_tok] = (__pb, __pr)

        stdout(f'[{__c_name}] New session {_ses_tok}\n')

        dout =  sc_data.NEW_CONN_CODE
        dout += _ses_tok.encode()
        # dout += str(__pb.n).encode() + sc_data.RSA_N_E_DELIM + str(__pb.e).encode()
        dout += __pb.save_pkcs1('PEM')

        stdout(f'[{__c_name}] {dout=}\n')
        _conn.send(dout)

    def _old_client(self, __rcv: bytes, __c_name: str) -> None:
        _std_hdr_len = sum([i.SIZE for i in sc_data.HeaderItems.items()])
        _conn, *_ = self.__connections[__c_name]

        if len(__rcv) < _std_hdr_len:
            # We need to receive the rest of the header to complete the decoding process.
            _n_bytes = _std_hdr_len - len(__rcv)

            _rest_of_hdr = _conn.recv(_n_bytes)

            assert len(_rest_of_hdr) == _n_bytes, f'Could not receive a complete header. // {sc_data.Errors.BAD_HEADER}'
            __rcv += _rest_of_hdr

        # Decode the header
        _hdr = sc_data.HeaderUtils.load_header(__rcv[:_std_hdr_len], sc_data.HEADER_PAD_BYTE)

        _complete_message_len = _std_hdr_len + sc_data.SHA3_256_HASH_SIZE + _hdr.H_MSG_LEN
        _il = len(__rcv)

        if _complete_message_len <= _il:
            __rcv += _conn.recv(_complete_message_len - _il)

            assert len(__rcv) == _complete_message_len, f'Could not receive complete message. // {sc_data.Errors.INCOMPLETE_MESSAGE}'

        _chk = __rcv[_std_hdr_len:_std_hdr_len + sc_data.SHA3_256_HASH_SIZE:].decode()
        _msg = __rcv[_std_hdr_len + sc_data.SHA3_256_HASH_SIZE::]

        rx = sc_data.Transmission(_hdr, _chk, _msg)
        comp_hash = hashlib.sha256(rx.message).hexdigest()

        assert rx.msg_hash == comp_hash, f'E({rx.msg_hash}) | R({comp_hash}) // {sc_data.Errors.BAD_TRANSMISSION}'
        __prk = Server.__sessions[rx.header.H_SES_TOK][-1]

        __msg_dec = rsa.decrypt(rx.message, __prk)
        __inst_id, __p_dob, __p_uid = __msg_dec.split(sc_data.MESSAGE_DELIM)

        print(__inst_id, __p_dob, __p_uid)

        __inst_id = __inst_id.decode().strip().upper()
        __p_dob = __p_dob.decode().strip()
        __p_uid = __p_uid.decode().strip()

        pt_diet = sc_db.lookup_patient_diet(__inst_id, int(__p_dob), int(__p_uid))

        def _reply_to(__conn: socket.socket, __message: bytes, __ses_tok: str) -> None:
            _o_chk = hashlib.sha256(__message).hexdigest().encode()
            _o_hdr = sc_data.HeaderUtils.create_bytes(__message, __ses_tok, True)

            __conn.send(_o_hdr + _o_chk + __message)

        if pt_diet is None:
            stderr(f'[{__c_name}] Patient not found.\n')

            # Send back the patient not found error code.
            # _reply_to(_conn, sc_data.CD_PATIENT_NOT_FOUND.encode(), rx.header.H_SES_TOK)
            # The above line was commented out because the server will automatically send the error code
            #   below to the client.

            assert False, f'Patient not found. // {sc_data.Errors.PATIENT_NOT_FOUND}'

        # Send meal options
        _reply_to(_conn, json.dumps(sc_db.get_meal_options(pt_diet)).encode(), rx.header.H_SES_TOK)
        
        stdout(f"[{__c_name}] Sent out {pt_diet.name} meal options.\n")

        return

    def _shutdown(self) -> None:
        global _SERVER_THREAD

        if self.__shutdown:
            return

        self.__shutdown = True

        self.__socket.close()

        try:
            self.__curr_task.cancel()
        except Exception:
            pass

        for (s, *_) in self.__connections.values():
            try:
                stdout(f"Closing {s}\n")
                s.close()
            except Exception:
                pass

        for t in self.__threads.values():
            stdout(f"Joining {s}\n")
            t.join_sf()

        _SERVER_THREAD.done()
        _SERVER_THREAD.join_sf(1)

        sc_quit()

    def __del__(self) -> None:
        global _SERVER_THREAD

        self.__curr_task.cancel()

        try:
            self.__socket.close()
        except Exception:
            pass

        _SERVER_THREAD.join_sf()


__server: Server


def sc_quit() -> None:
    __server.request_exit()

    while _SERVER_THREAD.is_alive():
        pass # Waiting

    sys.exit(0)


def _excepthook(exc_type: Type[BaseException], value: Any, traceback: Any) -> None:
    global __server

    if exc_type in (KeyboardInterrupt, ):
        stderr('KB_INT\n')

    else:
        __server.request_exit()
        sys.__excepthook__(exc_type, value, traceback)

    sc_quit()


_stdout_bk: List[str] = []
_enable_logging: bool = True


def stderr(data: str) -> int:
    global _enable_logging

    if not _enable_logging:
        stdout(f'[ERROR] {data}')
        return 0

    else:
        return sys.stderr.write(data)


def stdout(data: str) -> int:
    global _enable_logging, _stdout_bk

    if _enable_logging:
        return sys.stdout.write(data)

    else:
        _stdout_bk.append(data)

    return 0


def pause_stdout() -> None:
    global _enable_logging
    _enable_logging = False


def resume_stdout() -> None:
    global _stdout_bk, _enable_logging

    for s in [*_stdout_bk]:  # To prevent the case in which the size of __STDOUT_BK is changed during iteration.
        sys.stdout.write(f'[PAST MESSAGE] {s}')

    _enable_logging = True


def _start() -> None:
    global _SERVER_THREAD, __server

    CLI.help_doc()

    # Start the server on a new thread
    __server = Server(sc_data.TCP.IP, sc_data.TCP.PORT)
    _SERVER_THREAD = _S_THREAD(target=__server.bind)
    _SERVER_THREAD.start()

    while True:
        stdout('*** Stuck? Press CTRL+C to exit at any time.\n*** Use the command "HELP" to get a list of commands.\n\n')

        try:
            comm = input('Enter a command at any time, then press ENTER/RETURN.\n').strip().upper()

            match comm:
                case 'STOP':
                    sc_quit()

                case 'NEW':
                    pause_stdout()
                    CLI.add_patient()
                    resume_stdout()

                case 'UPDATE':
                    pause_stdout()
                    CLI.update_patient()
                    resume_stdout()

                case 'REMOVE':
                    pause_stdout()
                    CLI.remove_patient()
                    resume_stdout()

                case 'LIST':
                    pause_stdout()
                    CLI.list_patients()
                    resume_stdout()

                case 'HELP':
                    pause_stdout()
                    CLI.help_doc()
                    resume_stdout()

                case _:
                    stderr(f"Invalid command \"{comm}\"\n")

                    pause_stdout()
                    CLI.help_doc()
                    resume_stdout()

        except Exception:
            resume_stdout()
            stderr(traceback.format_exc() + '\n')


if __name__ == "__main__":
    sys.excepthook = _excepthook
    _start()

    # Unreachable code
    sys.exit(-1)
