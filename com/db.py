import data as sc_data, sqlite3
from typing import Dict, Any


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
