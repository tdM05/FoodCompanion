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
    def cleanDir() -> None:
        # Iterate through the directory, look for files that were created yestereday or earlier, nuke them
        dr = UserData.getDir()
        today = int(datetime.now().strftime('%Y%m%d'))

        for file in os.listdir(dr):
            try:
                fnp_datecode = file.split('.')[-2]
                dc = int(fnp_datecode)

            except Exception as E:
                os.remove(f'{dr}\\{file}')  # This file name is not of the right format
                continue

            if dc < today:
                sys.stdout.write(f'[WinClient @ cleanUserData] Removing "{file}" as it is too old.\n')
                os.remove(f'{dr}\\{file}')  # This file name is not of the right format

            else:
                sys.stdout.write(f'[WinClient @ cleanUserData] Skipping "{file}" as it contain\'s today\'s selections.\n')

    @staticmethod
    def generate_user_hash(iid: str, pid: str | int, pdob: str | int) -> str:
        return hashlib.sha256(f'{iid}{pid}{pdob}'.encode()).hexdigest()

    @staticmethod
    def load_pref(user_hash: str) -> Dict[str, List[int]]:
        date = datetime.now().strftime('%Y%m%d')
        ext = 'fcFile'

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

    @staticmethod
    def save_pref(user_hash: str, formatted_pref: Dict[str, List[int]]):
        date = datetime.now().strftime('%Y%m%d')
        ext = 'fcFile'

        fn = f'{user_hash}.{date}.{ext}'
        fp = f'{UserData.getDir()}\\{fn}'

        with open(fp, 'w') as out_file:
            out_file.write(json.dumps(formatted_pref, indent=4))
            out_file.close()
