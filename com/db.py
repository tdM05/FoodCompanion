import data as sc_data, sqlite3
from typing import Dict, Any


class DB:
    def __init__(self) -> None:
        self.__conn = sqlite3.connect("pt.db")
        self.__curr = self.__conn.cursor()

    def _crtn(self) -> None:
        self.__curr.execute("CREATE TABLE pt(")

def lookup_patient_diet(
        __inst_id:  str,
        __p_dob:    int,
        __p_uid:    int
) -> sc_data.MealOption | None:

    # TODO: Create the DB lookup code.

    return sc_data.MealOption.REGULAR


def get_meal_options(
        diet: sc_data.MealOption
) -> Dict[str, Any]:
    return {}
