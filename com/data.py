import socket
from enum import Enum
from dataclasses import dataclass
from datetime import datetime
from typing import cast, List, Union, Dict

# ------------------ CONSTANTS ------------------

SHA3_256_HASH_SIZE = 64
SESSION_TOKEN_SIZE = 32
DATETIME_FRMT_SIZE = 14
APP_VI_STRING_SIZE = 6
MSG_SIZE_LENGTH    = 6
TIME_FRMT_SIZE     = 6
DATE_FRMT_SIZE     = 8
NEW_CONN_CODE      = b'NW_CON'
RSA_N_E_DELIM      = b'!'
HEADER_PAD_BYTE    = b'='
MESSAGE_DELIM      = b'\x01'

CD_PATIENT_NOT_FOUND = 'ERR.PTNF'

# The app version should be loaded from a configuration file, ideally
APP_VERSION        = 20240708122100

# -----------------------------------------------

# ------------------ SETTINGS -------------------

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

TIME_FORMAT        = '%H%M%S'
DATE_FORMAT        = '%Y%m%d'
DATETIME_FORMAT    = DATE_FORMAT + TIME_FORMAT

# -----------------------------------------------

# ------------------- HEADER --------------------

class H_TYPE(Enum):
    (
        BIT,
        INT,
        STR
    ) = range(3)


class H_PAD_MODE(Enum):
    (
        NONE,
        PREPEND,
        APPEND
    ) = range(3)


@dataclass
class H_ITEM:
    NAME:   str
    TYPE:   H_TYPE
    SIZE:   int
    INDEX:  int
    PAD:    H_PAD_MODE


class HeaderItems:
    global SESSION_TOKEN_SIZE, MSG_SIZE_LENGTH, DATETIME_FRMT_SIZE

    #                | Name         | Type     | Size              | Start Index                         |  Padding Mode
    #                ---------------------------------------------------------------------------------------------------------
    H_TX_TIME = H_ITEM('H_TX_TIME', H_TYPE.INT, DATETIME_FRMT_SIZE, 0,                                      H_PAD_MODE.NONE)
    H_MC_TYPE = H_ITEM('H_MC_TYPE', H_TYPE.BIT, 1,                  (H_TX_TIME.INDEX + H_TX_TIME.SIZE),     H_PAD_MODE.NONE)
    H_SES_TOK = H_ITEM('H_SES_TOK', H_TYPE.STR, SESSION_TOKEN_SIZE, (H_MC_TYPE.INDEX + H_MC_TYPE.SIZE),     H_PAD_MODE.NONE)
    H_APP_VIS = H_ITEM('H_APP_VIS', H_TYPE.INT, DATETIME_FRMT_SIZE, (H_SES_TOK.INDEX + H_SES_TOK.SIZE),     H_PAD_MODE.NONE)
    H_MSG_LEN = H_ITEM('H_MSG_LEN', H_TYPE.INT, MSG_SIZE_LENGTH,    (H_APP_VIS.INDEX + H_APP_VIS.SIZE),     H_PAD_MODE.PREPEND)

    @staticmethod
    def items() -> List[H_ITEM]:
        return [
            # NOTE: The order in which the following items are listed is the order they will be in
            #       in the final header. THIS ORDER MUST BE IN COMPLIANCE WITH com/SPEC.

            HeaderItems.H_TX_TIME,
            HeaderItems.H_MC_TYPE,
            HeaderItems.H_SES_TOK,
            HeaderItems.H_APP_VIS,
            HeaderItems.H_MSG_LEN
        ]


@dataclass
class Header:
    H_TX_TIME: int
    H_MC_TYPE: bool
    H_SES_TOK: str
    H_APP_VIS: int
    H_MSG_LEN: int

    def create_bytes(self) -> bytes:
        global HEADER_PAD_BYTE

        _hdr_data = {
            # NOTE: The order in which the following items are listed is the order they will be in
            #       in the final header. THIS ORDER MUST BE IN COMPLIANCE WITH com/SPEC.

            'H_TX_TIME': (self.H_TX_TIME, HeaderItems.H_TX_TIME),
            'H_MC_TYPE': (self.H_MC_TYPE, HeaderItems.H_MC_TYPE),
            'H_SES_TOK': (self.H_SES_TOK, HeaderItems.H_SES_TOK),
            'H_APP_VIS': (self.H_APP_VIS, HeaderItems.H_APP_VIS),
            'H_MSG_LEN': (self.H_MSG_LEN, HeaderItems.H_MSG_LEN),
        }

        assert len(_hdr_data) == len(HeaderItems.items()),              'Function Header.create_bytes not up to date.'

        def _ass(__data: Union[int, bool, str, bytes], __item: H_ITEM, __padding: bytes) -> bytes:
            assert len(__padding) == 1

            dout: bytes
            draw: bytes

            match __item.TYPE:
                case H_TYPE.INT:
                    assert isinstance(__data, int)
                    draw = str(__data).encode()

                case H_TYPE.STR:
                    assert isinstance(__data, (str, bytes))
                    if isinstance(__data, str):
                        draw = __data.encode()
                    else:
                        draw = __data

                case H_TYPE.BIT:
                    assert isinstance(__data, (int, bool))
                    draw = b'1' if __data else b'0'

                case _:
                    raise ValueError("Invalid H_TYPE.")

            if __item.PAD is H_PAD_MODE.NONE:
                assert len(draw) == __item.SIZE
                dout = draw

            else:
                dout = HeaderUtils.pad(draw, __item.SIZE, __padding, __item.PAD)

            return dout

        return b''.join([_ass(hd, hi, HEADER_PAD_BYTE) for (hd, hi) in _hdr_data.values()])


class HeaderUtils:

    @staticmethod
    def pad(__data: bytes, __size: int, _pb: bytes, _pm: H_PAD_MODE) -> bytes:
        assert len(__data) <= __size
        assert _pm is not H_PAD_MODE.NONE

        match _pm:
            case H_PAD_MODE.PREPEND:
                return (_pb * (__size - len(__data))) + __data

            case H_PAD_MODE.APPEND:
                return __data + (_pb * (__size - len(__data)))

            case _:
                raise ValueError('Invalid padding mode.')

    @staticmethod
    def create_bytes(__message: bytes | str, __session_token: str, __is_server: bool) -> bytes:
        global APP_VERSION, DATETIME_FORMAT

        _time = datetime.now().strftime(DATETIME_FORMAT)

        return Header(
            int(_time),
            __is_server,
            __session_token,
            APP_VERSION,
            len(__message)
        ).create_bytes()

    @staticmethod
    def load_header(__hdr_bytes: bytes, __padding: bytes) -> Header:
        global HEADER_PAD_BYTE
        assert len(__hdr_bytes) == sum([i.SIZE for i in HeaderItems.items()])

        fns = {
            H_TYPE.INT: int,
            H_TYPE.STR: lambda b: cast(bytes, b).decode(),
            H_TYPE.BIT: bool
        }
        # return Header(*[fns[i.TYPE](__hdr_bytes[i.INDEX:(i.INDEX + i.SIZE):].strip(HEADER_PAD_BYTE)) for i in HeaderItems.items()])

        items = []
        for i in HeaderItems.items():
            items.append(fns[i.TYPE](__hdr_bytes[i.INDEX:(i.INDEX + i.SIZE):].strip(HEADER_PAD_BYTE)))

            if i.TYPE in (H_TYPE.STR, H_TYPE.INT) and i.PAD == H_PAD_MODE.NONE:
                assert len(str(items[-1])) == i.SIZE

        return Header(*items)

# -----------------------------------------------


class MealOption(Enum):
    (
        REGULAR,
        DIABETIC,
        LOW_CHOLESTEROL,
        LOW_SODIUM,
    ) = range(4)


@dataclass
class Transmission:
    header: Header
    msg_hash: str
    message: bytes


class TCP:
    # IP = socket.gethostbyname(socket.gethostname())
    IP = '0.0.0.0'  # Host on all IPs

    # Have a static IP ready for this server?
    # Host your server at that IP by modifying the above line as follows:
    # IP = '192.0.0.1'
    #
    # Of course, change the IP to your static IP.

    PORT = 12345


# Food Management


class Carbohydrates:
    def __init__(self, starches, fiber, sugars):
        self.starches = starches
        self.fiber = fiber
        self.sugars = sugars


class Fats:
    def __init__(self, trans, saturated):
        self.trans = trans
        self.saturated = saturated


class Macro:
    def __init__(self, carbohydrates, proteins, fats):
        self.carbohydrates = carbohydrates
        self.proteins = proteins
        self.fats = fats


class ServingSizeUnit(Enum):
    (
        COUNT,
        ML,
        G
    ) = range(3)


class Meal(Enum):
    (B, L, D) = range(3)


class FoodCategory(Enum):
    (
        ENTREE,
        SOUPS,
        STARCH,
        VEGETABLE,
        FRUIT,
        DESERT,
        BEVERAGE,
        CONDIMENT
    ) = range(8)


@dataclass
class Food:
    id:                 int
    name:               str
    calories:           float
    macros:             Macro
    diets:              List[MealOption]
    serving_size_count: float
    serving_size_unit:  ServingSizeUnit
    meal:               List[Meal]
    category:           FoodCategory


def filter_meals_by_diet_option(
        meal_options: Dict[str, Dict[str, List[Food]]],
        diet_option: MealOption
) -> Dict[str, List[Dict]]:
    filtered_meals = {
        "categories": []
    }

    diet_option_str = diet_option.value

    for meal, categories in meal_options.items():
        for category, foods in categories.items():
            category_dict = {
                "categoryName": category.capitalize(),
                "foods": []
            }
            for food in foods:
                if diet_option_str in food.diets or True:
                    food_dict = {
                        "id": food.id,
                        "name": food.name,
                        "calories": food.calories,
                        "macros": {
                            "carbohydrates": {
                                "starches": food.macros.carbohydrates.starches,
                                "sugars": food.macros.carbohydrates.sugars,
                                "fiber": food.macros.carbohydrates.fiber
                            },
                            "proteins": food.macros.proteins,
                            "fats": {
                                "trans": food.macros.fats.trans,
                                "saturated": food.macros.fats.saturated
                            }
                        },
                        "servingSize": food.serving_size
                    }
                    category_dict["foods"].append(food_dict)
            if category_dict["foods"]:
                filtered_meals["categories"].append(category_dict)

    return filtered_meals
