import appdirs, hashlib, os, sys, json
from typing import Dict, List
from datetime import datetime


class UserData:
    __appdata_loc = appdirs.user_data_dir('FoodCompanion', 'Geetansh Gautam', '1', False)

    @staticmethod
    def getDir() -> str:
        if not os.path.isdir(UserData.__appdata_loc):
            os.makedirs(UserData.__appdata_loc)

        return UserData.__appdata_loc.replace('/', '\\')

    @staticmethod
    def generate_user_hash(iid: str, pid: int, pdob: int) -> str:
        return hashlib.sha256(f'{iid}{pid}{pdob}'.encode()).hexdigest()

    def load_pref(self, user_hash) -> Dict[str, List[int]]:
        date = datetime.now().strftime('%Y%m%d')
        ext = '.fcFile'

        fn = f'{user_hash}.{date}.{ext}'
        fp = f'{UserData.getDir()}\\{fn}'

        if not os.path.isfile(fp):
            sys.stderr.write(f"[WinClient @ UserData] '{fn}' not found.\n")
            return {}

        else:
            with open(fp, 'r') as in_file:
                o = json.loads(in_file.read())
                in_file.close()

            return o


    def save_pref(self):
        pass