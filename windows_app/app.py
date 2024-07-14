import tkinter as tk
import winclient as sc_client, data as sc_data


if __name__ == "__main__":
    print(sc_client.WinClient.login('UHNBC', 20030101, 10020684))

