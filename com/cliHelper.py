import db as sc_db, data as sc_data
import sys
from typing import cast, Tuple, Union, Optional, List


class CLI:
    @staticmethod
    def _std(__get_diet: bool) -> Optional[Union[Tuple[str, int, int], Tuple[str, int, int, sc_data.MealOption]]]:
        print("\n\n", "-" * 41, sep="")
        print("\nFoodCompanion | Patient Management Wizard\n")
        print("-" * 41)

        print("Server logs have been disabled temporarily.")

        try:

            iid = input("Enter institution ID: ").strip()
            pid = int(input("Enter the patient's numeric ID: ").strip())
            pdobY = int(input("Enter the patient's YEAR of birth (YYYY): ").strip())
            pdobM = int(input("Enter the patient's MONTH of birth (MM): ").strip())
            pdobD = int(input("Enter the patient's DATE of birth (DD): ").strip())

            assert len(iid),                        'Invalid facility ID.'
            assert pid > 0,                         'Patient ID must be at least 1.'
            assert pdobY > 1900,                    'Invalid birth year.'
            assert pdobM in range(1, 13),           'Invalid birth month.'
            assert pdobD in range(1, 32),           'Invalid birth date.'

            pdob = int(f'{pdobY}%s{pdobM}%s{pdobD}' % (
                '0' if pdobM < 10 else '',
                '0' if pdobD < 10 else ''
            ))

            if __get_diet:

                print("The following DIETS are avaialble:")

                for (n, e) in sc_data.MealOption._member_map_.items():
                    print(f"| \t[{e.value}] {n} DIET.")

                d = int(input("Please enter the NUMBER of the diet applicable to this patient: ").strip())

                de = sc_data.MealOption._value2member_map_.get(d, None)
                assert de is not None,                    ' Invalid DIET selection.'

                return iid, pdob, pid, cast(sc_data.MealOption, de)

            else:
                return iid, pdob, pid

        except AssertionError as AE:
            sys.stderr.write(str(AE) + '\n')
            rt = (input("Would you like to retry? (y/N): ")).upper().strip()

            if rt == 'Y':
                return CLI._std(__get_diet)

            return None

        except KeyboardInterrupt:
            sys.stderr.write("Quitting Wizard...\n")
            return None

    @staticmethod
    def add_patient():
        out = CLI._std(True)

        if out is None:
            return None

        assert isinstance(out, tuple) and len(out) == 4

        (iid, pdob, pid, diet) = out

        if not sc_db.db.create_new_record(
            iid=iid,
            pid=pid,
            pdob=pdob,
            diet=diet
        ):
            sys.stderr.write("Failed to add patient.\n")
        else:
            sys.stdout.write("Created new patient record successfully!")
            return

    @staticmethod
    def help_doc():
        print(f"""
-------------------------------------------------------------------------
                   FoodCompanion for MediHacks 2024
-------------------------------------------------------------------------

Hosting server at {sc_data.TCP.IP}:{sc_data.TCP.PORT}

Server Management:
    HELP.......................................Display this documentation
    STOP....................Stop the server (DISABLES MOBILE/DESKTOP APP)           

Patient Record Management:
    NEW..........................................Add a new patient record
    LIST...................................List all patients at a facilty
    UPDATE..................................Change a patient's diet order
    REMOVE........................................Delete a patient record

-------------------------------------------------------------------------
""")

    @staticmethod
    def update_patient():
        out = CLI._std(True)

        if out is None:
            return None

        assert isinstance(out, tuple) and len(out) == 4

        (iid, pdob, pid, diet) = out

        if not sc_db.db.update_diet_options(
                iid=iid,
                pid=pid,
                pdob=pdob,
                new_diet=diet
        ):
            sys.stderr.write("Failed to update patient record.\n")
        else:
            sys.stdout.write("Updated patient record successfully!")
            return

    @staticmethod
    def remove_patient():
        out = CLI._std(False)

        if out is None:
            return None

        assert isinstance(out, tuple) and len(out) == 3

        (iid, pdob, pid) = out

        if not sc_db.db.delete_patient_record(
                iid=iid,
                pid=pid,
                pdob=pdob
        ):
            sys.stderr.write("Failed to remove patient.\n")
        else:
            sys.stdout.write("Removed patient record successfully!")
            return

    @staticmethod
    def list_patients():
        inst = input("Please enter the institution ID: ").strip()

        lst = sc_db.db.get_patient_list(iid=inst)

        if not lst:
            sys.stderr.write("Failed to list patient records.\n")
            return

        print('.\n\n' + ''"-" * 41 + '\n')
        print(f"Patient list for {inst}\n\n")

        print('' + "-" * 80)
        print("[Patient ID]        | DOB (YYYYMMDD)      | Diet Order ")
        print('' + "-" * 80)

        for pt in cast(List[sc_db.PTRecord], lst):
            sys.stdout.write(
                f"[{pt.PID}]%s| {pt.PDOB}%s| {pt.DIET.name}\n" %
                (
                    (' ' * (20 - len(f'[{pt.PID}]'))) if len(f'[{pt.PID}]') < 20 else ' ',
                    ' ' * 12
                )
            )

        print('' + "-" * 80)

