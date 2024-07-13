from com.data import (
    SHA3_256_HASH_SIZE,
    SESSION_TOKEN_SIZE,
    DATETIME_FRMT_SIZE,
    APP_VI_STRING_SIZE,
    MSG_SIZE_LENGTH,
    TIME_FRMT_SIZE,
    DATE_FRMT_SIZE,
    NEW_CONN_CODE,
    RSA_N_E_DELIM,
    HEADER_PAD_BYTE,
    MESSAGE_DELIM,
    CD_PATIENT_NOT_FOUND,
    APP_VERSION,

    SRVC_CLT_POL_IP,
    SRVC_TCP_RECV_N,

    TIME_FORMAT,
    DATE_FORMAT,
    DATETIME_FORMAT,

    H_TYPE,
    H_PAD_MODE,
    H_ITEM,
    Header,
    HeaderItems,
    HeaderUtils,

    Transmission,

    MealOption,
    Carbohydrates,
    Fats,
    Macro,
    ServingSizeUnit,
    Meal,
    FoodCategory,
    Food,
    TCP as srv_TCP
)
import socket


class TCP:
    IP = socket.gethostbyname(socket.gethostname())
    PORT = srv_TCP.PORT
