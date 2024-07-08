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

import click, select, sys, hashlib, rsa, socket, re, random
from datetime import datetime
from threading import Thread, Event, Timer
from typing import cast, Type, Any, Dict, Tuple


try:
    from . import data as sc_data
    _EXT_SC_CALL = True

except ImportError:
    import data as sc_data
    _EXT_SC_CALL = False


# Constants

SHA3_256_HASH_SIZE = 64
SESSION_TOKEN_SIZE = 32
DATETIME_FRMT_SIZE = 14
APP_VI_STRING_SIZE = 6
TIME_FRMT_SIZE     = 6
DATE_FRMT_SIZE     = 8
NEW_CONN_CODE      = b'NW_CON'
RSA_N_E_DELIM      = b'!'

# Header

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
    __sessions: Dict[str, Any] = {}
    __cost_timer = 15

    def request_exit(self) -> None:

        Server.__is_alive = False

        self.__curr_task.cancel()
        self._shutdown()

    @staticmethod
    def is_compatible(vi: int) -> bool:
        return True

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

    def bind(self) -> None:
        print(f'binding to {self.__net}')

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
        self.__curr_task = Timer(function=self._clear_ost, interval=Server.__cost_timer)
        self.__curr_task.start()

        self.__socket.setblocking(False)

        while Server.__is_alive:
            R, *_ = select.select(
                [self.__socket, *[c[0] for c in self.__connections.values() if not c[0]._closed]],
                [],
                [c[0] for c in self.__connections.values()],
                sc_data.SRVC_SL_TIMEOUT
            )

            for r in R:
                if r is self.__socket:
                    _conn, _addr = self.__socket.accept()
                    _conn.setblocking(False)

                    _c_name = '%s%d-%d' % (*cast(Tuple[str, int], _addr), random.randint(100, 999))

                    self.__connections[_c_name] = (_conn, cast(Tuple[str, int], _addr), None, None)  # type: ignore
                    self.__threads[_c_name] = _S_THREAD(target=self._handle_client, args=(_c_name, ))

                    self.__threads[_c_name].start()

        else:
            sys.stdout.write('Server exiting main loop.\n')

        self.__curr_task.cancel()
        self._shutdown()

    def _handle_client(self, __c_name: str) -> None:
        global SHA3_256_HASH_SIZE, SESSION_TOKEN_SIZE, DATETIME_FRMT_SIZE, NEW_CONN_CODE

        sys.stdout.write(f'[{__c_name}] Replying.\n')

        _conn, *_ = self.__connections[__c_name]
        _rcv = _conn.recv(sc_data.SRVC_TCP_RECV_N)

        if not _rcv:
            sys.stderr.write(f'[{__c_name}] Connection closed.\n')
            self._remove(__c_name)

            return

        if NEW_CONN_CODE in _rcv:
            try:
                self._new_client(_rcv, __c_name)
                _conn.close()

            except AssertionError as _AE:
                sys.stderr.write(f'[{__c_name}] Invalid NW_CON request <CONN. ABORTED>: {str(_AE)}\n')
                self._remove(__c_name)

                return

        else:
            try:
                self._old_client(_rcv, __c_name)
                _conn.close()

            except AssertionError as _AE:
                sys.stderr.write(f'[{__c_name}] Invalid MEAL_OPTIONS request <CONN. ABORTED>: {str(_AE)}\n')
                self._remove(__c_name)

                return

        sys.stdout.write(f'[{__c_name}] Done.\n')

        self.__threads[__c_name].done()
        self._remove(__c_name)

    def _new_client(self, __rcv: bytes, __c_name: str) -> None:
        global NEW_CONN_CODE, APP_VI_STRING_SIZE, RSA_N_E_DELIM

        assert len(__rcv) >= (len(NEW_CONN_CODE) + APP_VI_STRING_SIZE), 'Bad Request (0)'

        cd = __rcv[:len(NEW_CONN_CODE):]
        vi = int(__rcv[len(NEW_CONN_CODE)::])

        print(cd, vi)

        assert cd == NEW_CONN_CODE,                                     'Bad Request (1)'
        assert Server.is_compatible(int(vi)),                           'Incompatible client version.'

        # The app is compatible, make a session token, RSA encryption key, and send it over.
        _conn, _addr, *_ = self.__connections[__c_name]

        # Create the RSA key
        (__pb, __pr) = rsa.newkeys(512)
        self.__connections[__c_name] = (_conn, _addr, __pb, __pr)

        dout =  NEW_CONN_CODE
        dout += Server.gen_ses_tok(_addr[1]).encode()
        dout += str(__pb.n).encode() + RSA_N_E_DELIM + str(__pb.e).encode()

        sys.stdout.write(f'[{__c_name}] {dout=}\n')
        _conn.send(dout)

    def _old_client(self, __rcv: bytes, __c_name: str) -> None:
        print(__rcv, __c_name)

        return

    def _shutdown(self) -> None:
        global _SERVER_THREAD

        if self.__shutdown:
            return

        self.__shutdown = True

        try:
            self.__curr_task.cancel()
        except Exception as E:
            pass

        self.__socket.close()

        for (s, *_) in self.__connections.values():
            try:
                s.close()
            except Exception:
                pass

        for t in self.__threads.values():
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


@click.group()
def cli() -> None:
    pass


def sc_quit() -> None:
    sys.stdout.write("Submitting an exit request now...\n")

    __server.request_exit()
    sys.exit(0)


def exception_hook(exc_type: Type[BaseException], value: Any, traceback: Any) -> None:
    print(exc_type)

    if exc_type is KeyboardInterrupt:
        sys.stderr.write('KB_INT\n')
        sc_quit()

    else:
        Server.request_exit()
        sys.__excepthook__(exc_type, value, traceback)
        sc_quit()



@cli.command()
@click.option(
    '-ip', '--ip',
    type=str,
    default=sc_data.TCP.IP,
    help="IPV4 address to host server at.",
    show_default=True
)
@click.option(
    '-p', '--port',
    type=int,
    default=sc_data.TCP.PORT,
    help="PORT to host the server at.",
    show_default=True
)
def start(*args, **kwargs) -> None:
    global _SERVER_THREAD, __server

    sys.stdout.writelines([
        "-----------------------------------------------\r\n",
        "                                               \r\n",
        "       MediHacks 2024 | FoodCompanion App      \r\n",
        f" Hosting server at {kwargs['ip']} (PORT {kwargs['port']}) \r\n",
        "                                               \r\n",
        "-----------------------------------------------\r\n"
    ])

    # Start the server on a new thread
    __server = Server(kwargs['ip'], kwargs['port'])
    _SERVER_THREAD = _S_THREAD(target=__server.bind)
    _SERVER_THREAD.start()

    while True:
        sys.stdout.write('Stuck? Press CTRL+C to exit at any time.\n')
        comm = input('Enter a command at any time, then press ENTER/RETURN.\n').strip().upper()

        match comm:
            case 'EXIT':
                sc_quit()

            case _:
                print(f'Invalid command "{comm}".')


if __name__ == "__main__":
    sys.excepthook = exception_hook

    cli()

    # Unreachable code
    sys.exit()
