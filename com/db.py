import data as sc_data, sqlite3, sys
from typing import cast, List, Dict, Any, Optional
from dataclasses import dataclass


@dataclass
class PTRecord:
    IID: str
    PID: int
    PDOB: int
    DIET: sc_data.MealOption


class DB:
    def __init__(self) -> None:
        self.__db_name = "pt.db"
        self._crtn()

    def _crtn(self) -> None:
        # Check if the table PTS (patients) exists

        with sqlite3.connect(self.__db_name) as conn:
            curr = conn.cursor()

            curr.execute(
                """
                CREATE TABLE IF NOT EXISTS PTS (
                    n       INTEGER PRIMARY KEY AUTOINCREMENT,
                    pid     INTEGER NOT NULL,
                    diet    INTEGER NOT NULL,
                    iid     text NOT NULL,
                    pdob    INTEGER NOT NULL
                );
                """
            )

            curr.close()
            conn.commit()

    def _id_in_db(self, iid: str, pid: int) -> bool:
        res = None

        with sqlite3.connect(self.__db_name) as conn:
            curr = conn.cursor()

            curr.execute(f"SELECT * FROM PTS WHERE iid = \"{iid.upper()}\" AND pid = {pid}")
            res = curr.fetchone()

            curr.close()
            conn.commit()

        return res

    def get_record(
            self,
            iid: str,
            pdob: int,
            pid: int
    ) -> Optional[PTRecord]:
        with sqlite3.connect(self.__db_name) as conn:
            curr = conn.cursor()

            curr.execute(f"""SELECT DISTINCT diet FROM PTS WHERE iid = \"{iid.upper()}\" AND pdob = {pdob} AND pid = {pid}""")
            out = curr.fetchone()

            curr.close()
            conn.commit()

        if out is None:
            return None

        out = out[0]
        if out not in sc_data.MealOption._value2member_map_.keys():
            return None

        return PTRecord(
            IID=iid,
            PID=pid,
            PDOB=pdob,
            DIET=cast(sc_data.MealOption, sc_data.MealOption._value2member_map_[out])
        )

    def update_diet_options(
            self,
            iid: str,
            pdob: int,
            pid: int,
            new_diet: sc_data.MealOption
    ) -> bool:
        try:
            with sqlite3.connect(self.__db_name) as conn:
                curr = conn.cursor()

                curr.execute(
                    f"SELECT DISTINCT * FROM PTS WHERE iid=\"{iid.upper()}\" AND pid={pid} AND pdob={pdob}"
                )
                assert curr.fetchone(),                     'No patient record with matching information found.'

                curr.execute(f"""
                UPDATE PTS
                    SET 
                        diet = {new_diet.value}
                    WHERE
                            iid=\"{iid.upper()}\" 
                        AND pid={pid} 
                        AND pdob={pdob}
                """)

                curr.close()
                conn.commit()

        except AssertionError as AE:
            sys.stderr.write(str(AE) + '\n')
            return False
        else:
            return True

    def create_new_record(
            self,
            iid: str,
            pdob: int,
            pid: int,
            diet: sc_data.MealOption
    ) -> bool:

        try:
            assert len(str(pdob)) == sc_data.DATE_FRMT_SIZE,    'Invalid date of birth. Please use the format YYYYMMDD.'
            iid = iid.strip().upper()
            assert len(iid),                                    'Invalid institution/facility ID.'
            assert not self._id_in_db(iid, pid),                'A patient with the same ID already exists at this facility.'

            # All good, add the patient

            with sqlite3.connect(self.__db_name) as conn:
                curr = conn.cursor()

                curr.execute(f"""
                    INSERT INTO 
                        PTS (pid, diet, iid, pdob) 
                    values 
                        ({pid}, {diet.value}, \"{iid}\", {pdob})
                """)

                curr.close()
                conn.commit()

        except AssertionError as AE:
            sys.stderr.write(str(AE) + '\n')
            return False

        else:
            return True

    def delete_patient_record(
            self,
            iid: str,
            pid: int,
            pdob: int
    ) -> bool:
        try:
            with sqlite3.connect(self.__db_name) as conn:
                curr = conn.cursor()

                curr.execute(f'SELECT * FROM PTS WHERE iid="{iid.upper()}" AND pid={pid} AND pdob={pdob}')
                assert curr.fetchall(), 'No such patient record found.'

                curr.execute(f'''
                DELETE FROM PTS 
                WHERE 
                        iid = "{iid.upper()}"
                    AND pid = {pid}
                    AND pdob = {pdob}
                ''')

                curr.close()
                conn.commit()

        except AssertionError as AE:
            sys.stderr.write(str(AE) + '\n')
            return False
        else:
            return True

    def get_patient_list(self, iid: str) -> Optional[List[PTRecord]]:
        with sqlite3.connect(self.__db_name) as conn:
            curr = conn.cursor()

            curr.execute(f"SELECT * FROM PTS WHERE iid = \"{iid.upper()}\"")
            res = curr.fetchall()

            curr.close()
            conn.commit()

        if not res:
            return None

        return [
            PTRecord(
                IID=p[3],
                PID=p[1],
                PDOB=p[4],
                DIET=cast(sc_data.MealOption, sc_data.MealOption._value2member_map_[p[2]])
            )
            for p in res
        ]

db = DB()


def lookup_patient_diet(
        __inst_id:  str,
        __p_dob:    int,
        __p_uid:    int
) -> sc_data.MealOption | None:
    global db

    rc = db.get_record(__inst_id, __p_dob, __p_uid)
    return rc.DIET if isinstance(rc, PTRecord) else None


def get_meal_options(
        diet: sc_data.MealOption
) -> Dict[str, Any]:
    return {}
