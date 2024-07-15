import tkinter as tk, sys
import winclient as sc_client, data as sc_data, client_data as sc_helper
from threading import Timer
from tkinter import ttk
from typing import cast, List, Dict, Any, Tuple
from dataclasses import dataclass
from enum import Enum
from tkcalendar import Calendar, DateEntry
from datetime import datetime


@dataclass
class Theme:
    BG:     str
    FG:     str
    AC1:    str
    AC2:    str
    OK:     str
    ER:     str


FONT_TITLE = "Montserrat Black"
FONT_MAIN = "Montserrat Medium"

_id_to_food_map_: Dict[int, sc_data.Food] = {}
_name_to_fid_map: Dict[str, int] = {}


class UpdateReq(Enum):
    (
        BG,
        FG,
        ACTIVE_BG,
        ACTIVE_FG,
        FG_ER,
        FG_OK,
        FG_AC,
        BG_AC,
        REV_FG,
        FONT_MAIN
    ) = range(10)


_default_theme = Theme(
    '#F8F0FB',
    '#222222',
    '#1C5D99',
    '#639FAB',
    '#00AAA8',
    '#EE6055'
)


class App:
    def __init__(self) -> None:
        global _default_theme, FONT_MAIN, FONT_TITLE

        self.root = tk.Tk()

        self.lg_geo = (350, 500)
        self.lg_pos = (
            (self.root.winfo_screenwidth() // 2) - (self.lg_geo[0] // 2),
            (self.root.winfo_screenheight() // 2) - (self.lg_geo[1] // 2)
        )

        self.ml_geo = (1000, 800)
        self.ml_pos = (
            (self.root.winfo_screenwidth() // 2) - (self.ml_geo[0] // 2),
            (self.root.winfo_screenheight() // 2) - (self.ml_geo[1] // 2)
        )

        self.status_lbl = tk.Label(self.root)
        self.status_lbl_task: Timer | None = None

        self.style = ttk.Style(self.root)
        self.style.theme_use('clam')

        self.login_frame = tk.Frame(self.root)
        self.main_frame = tk.Frame(self.root)

        # Login Frame Elements
        self.dob_frm = tk.Frame(self.login_frame)
        self.dob_lbl = tk.Label(self.dob_frm)
        self.login_cal = DateEntry(
            self.dob_frm,
            borderwidth=0,
            maxdate=datetime.now(),
            date_pattern='y/mm/dd'
        )

        self.login_cal.config(
            style='TEntry',
            font=(FONT_MAIN, 12)
        )

        self.title_text = tk.Label(self.login_frame)

        self.pid_frm = tk.Frame(self.login_frame)
        self.pid = tk.StringVar(self.root)
        self.pid_lbl = tk.Label(self.pid_frm)
        self.patient_id = ttk.Entry(self.pid_frm, textvariable=self.pid)

        self.fid_frm = tk.Frame(self.login_frame)
        self.fid_lbl = tk.Label(self.fid_frm)
        self.fid = tk.StringVar(self.root)
        self.facility_id = ttk.Entry(self.fid_frm, textvariable=self.fid)

        self.login_button = ttk.Button(self.login_frame, text="Log In", command=self.login)

        # Main Frame Elements

        self.mf_tbar = tk.Frame(
            self.main_frame,
            bg=_default_theme.AC1
        )
        self.mf_title = tk.Label(
            self.mf_tbar,
            text="Food Companion",
            justify=tk.LEFT,
            anchor=tk.W,
            font=(FONT_TITLE, 20),
            bg=_default_theme.AC1,
            fg=_default_theme.BG
        )
        self.mf_do = tk.Label(
            self.mf_tbar,
            justify=tk.LEFT,
            anchor=tk.W,
            font=(FONT_MAIN, 10),
            bg=_default_theme.AC1,
            fg=_default_theme.BG
        )
        self.mf_calorie_bar = tk.Frame(
            self.root,
            bg=_default_theme.AC1
        )
        self.mf_calorie_counter = tk.Label(
            self.mf_calorie_bar,
            bg=_default_theme.AC1,
            fg=_default_theme.BG,
            font=(FONT_MAIN, 10),
            justify=tk.LEFT,
            anchor=tk.W,
        )
        self.mf_macro_counter   = tk.Label(
            self.mf_calorie_bar,
            bg=_default_theme.AC1,
            fg=_default_theme.BG,
            font=(FONT_MAIN, 10),
            justify=tk.LEFT,
            anchor=tk.W,
        )

        self.mf_frame = tk.Frame(
            self.main_frame,
            bg=_default_theme.BG
        )
        self.mf_iframe = tk.Canvas(
            self.mf_frame,
            bg=_default_theme.BG,
            highlightcolor=_default_theme.BG,
            highlightbackground=_default_theme.BG,
            highlightthickness=0,
            borderwidth=0
        )
        self.mf_aframe = tk.Frame(
            self.mf_iframe,
            bg=_default_theme.BG
        )
        self.mf_vsb = ttk.Scrollbar(self.mf_frame, orient=tk.VERTICAL, command=self.mf_iframe.yview)
        self.mf_iframe.config(yscrollcommand=self.mf_vsb.set)

        self.update_reqs = []

        self.dietOrder = None
        self.meals = {}
        self.limits = {}

        self.__data = {}
        self.__selected_food = {}
        self.__caloric_lookup = {}

        self.run()

    def _on_mousewheel(self, event) -> None:
        self.mf_iframe.yview_scroll(int(-1 * (event.delta / 120)), "units")

    def _on_frame_config(self, *_) -> None:
        self.root.update()
        self.mf_iframe.configure(scrollregion=self.mf_iframe.bbox("all"))

    def on_pt_login(self) -> None:
        global _name_to_fid_map, _id_to_food_map_
        in_data = sc_helper.UserData.load_pref(sc_helper.UserData.generate_user_hash(self.fid.get(), self.pid.get(), self.login_cal.get().replace('/', '')))

        if not in_data:
            return

        # Save stuff to self.__selected_foods
        for name, A in in_data.items():
            if name not in _name_to_fid_map:
                sys.stderr.write(f"Item '{name}' no longer available.\n")
                continue

            fid = _name_to_fid_map[name]

            # Make sure that the previous meal-time selections are still valid
            f = _id_to_food_map_[fid]
            B = [a for a in A if a in list(map(lambda mt: mt.value, f.meal))]

            if not len(B):
                sys.stderr.write(f"Item '{name}' no longer available at selected time(s).\n")
                continue

            self.__selected_food[fid] = B

    def on_app_close(self) -> None:
        global _id_to_food_map_

        if self.status_lbl_task is not None:
            self.status_lbl_task.cancel()

        # Save the current preferences (map IDs to names and save a subset of self.__selected_foods)
        uh = sc_helper.UserData.generate_user_hash(self.fid.get(), self.pid.get(), self.login_cal.get().replace('/', ''))
        out_data = {_id_to_food_map_[k].name: v for k, v in self.__selected_food.items()}

        sc_helper.UserData.save_pref(uh, out_data)

        self.root.quit()

    def clear_status(self) -> None:
        try:
            self.status_lbl.config(text='')
            self.status_lbl_task = None
        except RuntimeError:
            pass  # The app has probably been quit.

    def status_error(self, stat: str) -> None:
        global _default_theme, FONT_MAIN

        if self.status_lbl_task is not None:
            self.status_lbl_task.cancel()

        self.root.update()

        self.status_lbl.config(
            text=stat,
            fg=_default_theme.ER,
            bg=_default_theme.BG,
            font=(FONT_MAIN, 10),
            wraplength=self.root.winfo_width() - 40
        )

        self.status_lbl_task = Timer(8, self.clear_status)
        self.status_lbl_task.start()

    def status_ok(self, stat: str) -> None:
        global _default_theme, FONT_MAIN

        if self.status_lbl_task is not None:
            self.status_lbl_task.cancel()

        self.root.update()

        self.status_lbl.config(
            text=stat,
            fg=_default_theme.OK,
            bg=_default_theme.BG,
            font=(FONT_MAIN, 10),
            wraplength=self.root.winfo_width() - 40
        )

        self.status_lbl_task = Timer(8, self.clear_status)
        self.status_lbl_task.start()

    def login(self) -> None:
        if self.fid.get() == '' or self.pid.get() == '':
            self.status_error("Please provide the above information.")

        try:
            self.dietOrder, self.limits, self.meals = parse_options(
                self.fid.get(),
                int(self.login_cal.get().replace('/', '')),
                int(self.pid.get())
            )

            self.switch()

        except Exception as ae:
            self.status_error(f"Failed to login: {str(ae)}")

    def update_cal(self) -> None:
        if not len(self.__selected_food):
            self.mf_calorie_counter.config(text="Add food from the meal options to get caloric information.")

        S_cal = 0
        S_carb = 0
        S_prot = 0
        S_fats = 0

        for fid, n in self.__selected_food.items():
            cal, carb, prot, fats = self.__caloric_lookup[fid]

            S_cal += cal * len(n)
            S_carb += carb * len(n)
            S_prot += prot * len(n)
            S_fats += fats * len(n)

        self.mf_calorie_counter.config(text=f"Total: {S_cal} calories (Carbohydrates: {S_carb}, Protein: {S_prot}, Fats: {S_fats})")

    def switch(self) -> None:
        assert self.dietOrder is not None
        assert self.meals is not None
        assert self.limits is not None

        self.root.geometry("%dx%d+%d+%d" % (*self.ml_geo, *self.ml_pos))
        self.login_frame.pack_forget()

        self.status_ok(f"Logged in successfully and retrieved the {self.dietOrder.name.lower().replace('_', ' ')} diet.")
        self.main_frame.pack(side=tk.TOP, fill=tk.BOTH, expand=True)

        self.mf_do.config(text=f"{self.dietOrder.name.title().replace('_', ' ')} Meal Options")

        self.mf_tbar.pack(fill=tk.X, expand=False, side=tk.TOP)
        self.mf_title.pack(fill=tk.X, expand=False, padx=20, pady=(30, 0))
        self.mf_do.pack(fill=tk.X, expand=False, padx=20, pady=(0, 30))

        self.mf_frame.pack(fill=tk.BOTH, expand=True)
        self.mf_iframe.pack(fill=tk.BOTH, expand=True, side=tk.LEFT)
        self.mf_vsb.pack(fill=tk.Y, expand=False, side=tk.RIGHT)

        self.mf_calorie_counter.pack(fill=tk.X, expand=True, padx=20, pady=5)
        self.on_pt_login()  # Load previously saved selections.

        self.root.update()

        self.mf_iframe.create_window(
            0, 0,
            window=self.mf_aframe,
            anchor="nw",
            tags="self.mf_aframe",
            width=(self.root.winfo_width() - self.mf_vsb.winfo_width() - 10)
        )

        self.mf_aframe.update()
        self.mf_aframe.bind("<Configure>", self._on_frame_config)
        self.mf_iframe.bind("<MouseWheel>", self._on_mousewheel)

        # Re-organize by meal category.
        order = [
            'Entree',
            'Soup',
            'Starch',
            'Vegetable',
            'Fruit',
            'Desert',
            'Beverage',
            'Condiment'
        ]

        for n in order:
            if n not in self.meals:
                continue

            # Print the cateogry name
            tk.Label(
                self.mf_aframe,
                text=f'{n}%s' % ('s' if n != 'Starch' else 'es'),
                bg=_default_theme.BG,
                fg=_default_theme.AC1,
                font=(FONT_TITLE, 18),
                anchor=tk.W,
                justify=tk.LEFT
            ).pack(fill=tk.X, expand=False, padx=20, pady=10)

            for f in self.meals[n].values():
                self.__data[len(self.__data)] = {
                    'f': f,  # sc_data.Food
                }
                self.config_meal_opt(len(self.__data) - 1)

        self.update_cal()  # config_meal_opt fills in caloric lookup, which is needed by this function.

    def config_meal_opt(self, __i: int) -> None:
        global _default_theme, FONT_MAIN, FONT_TITLE

        d = self.__data[__i]
        f = cast(sc_data.Food, d['f'])

        # Label Frame
        #       Three Frames
        #       Frame 1:    Name, Calories, Serving Size
        #       Frame 2:    Nutrition
        #       Frame 3:    "Add to" buttons

        self.root.update()

        self.mf_aframe.config(width=self.root.winfo_width() - 50)

        lblf = tk.LabelFrame(
            self.mf_aframe,
            bg=_default_theme.BG,
            fg=_default_theme.FG,
            borderwidth=1,
            highlightcolor=_default_theme.AC2,
            highlightthickness=1,
            highlightbackground=_default_theme.AC2,
            width=self.root.winfo_width() - 50
        )

        lblf.pack(fill=tk.X, expand=True, padx=20, pady=5)
        f0 = tk.Frame(lblf, bg=_default_theme.BG, width=(lblf.winfo_width() - 40))
        f1 = tk.Frame(f0, bg=_default_theme.BG, width=(lblf.winfo_width() - 40) // 3)
        f2 = tk.Frame(f0, bg=_default_theme.BG, width=(lblf.winfo_width() - 40) // 3)
        f3 = tk.Frame(f0, bg=_default_theme.BG, width=(lblf.winfo_width() - 40) // 3)

        def _crt_lbl(parent, text, padx, pady, mode_ttl=False, fs=12) -> tk.Label:
            _lbl = tk.Label(
                master=parent,
                text=text,
                bg=_default_theme.BG,
                fg=_default_theme.FG,
                font=(FONT_TITLE if mode_ttl else FONT_MAIN, 20 if mode_ttl else fs),
                anchor=tk.NW,
                justify=tk.LEFT
            )

            _lbl.pack(fill=tk.X, expand=False, padx=padx, pady=pady)

            return _lbl

        def _crt_button(parent, text, style, pady) -> tk.Button:
            _btn = ttk.Button(parent, text=text, style=style)
            _btn.pack(fill=tk.X, expand=True, padx=(10, 20), pady=pady)
            return _btn


        _crt_lbl(f1, f.name.title(), (20, 10), (10, 5), True)

        _crt_lbl(f1, f'{f.calories} calories', (20, 10), 0, False)
        _crt_lbl(f1, str(f.serving_size_count) + {sc_data.ServingSizeUnit.COUNT: ' count',
                sc_data.ServingSizeUnit.ML: ' mL',sc_data.ServingSizeUnit.G: ' g'}[f.serving_size_unit],
                (20, 10), (0, 10)
        )

        _crt_lbl(f2, f"Carbohydrates: {f.macros.carbohydrates.starches + f.macros.carbohydrates.sugars + f.macros.carbohydrates.fiber}", 10, (10, 0))
        _crt_lbl(f2, f"    Starches: {f.macros.carbohydrates.starches}", 10, 0, fs=10)
        _crt_lbl(f2, f"    Sugars: {f.macros.carbohydrates.sugars}", 10, 0, fs=10)
        _crt_lbl(f2, f"    Fiber: {f.macros.carbohydrates.fiber}", 10, 0, fs=10)

        _crt_lbl(f2, f"Protein: {f.macros.proteins}", 10, 0)

        _crt_lbl(f2, f"Fats: {f.macros.fats.trans + f.macros.fats.saturated}", 10, 0)
        _crt_lbl(f2, f"    Trans Fats: {f.macros.fats.trans}", 10, 0, fs=10)
        _crt_lbl(f2, f"    Saturated Fats: {f.macros.fats.saturated}", 10, (0, 10), fs=10)

        bb = _crt_button(f3, "Add to Breakfast", "Off.TButton", (10, 5))
        bl = _crt_button(f3, "Add to Lunch", "Off.TButton", 5)
        bd = _crt_button(f3, "Add to Dinner", "Off.TButton", (5, 10))

        self.__data[__i]['B'] = [
            (bb, bl, bd),
            [
                True if sc_data.Meal.B.value in self.__selected_food.get(f.id, []) else False,  # Load dynamically from the loaded information.
                True if sc_data.Meal.L.value in self.__selected_food.get(f.id, []) else False,
                True if sc_data.Meal.D.value in self.__selected_food.get(f.id, []) else False
            ],
            ("Breakfast", "Lunch", "Dinner")
        ]

        for i, v in enumerate(self.__data[__i]['B'][1]):
            if v:
                self.__data[__i]['B'][0][i].configure(
                    style='On.TButton',
                    text='Added to %s' % self.__data[__i]['B'][2][i]
                )

        bb.config(
            command=lambda *_: self.toggle_button(__i, 0),
            state=tk.NORMAL if sc_data.Meal.B in f.meal else tk.DISABLED,
            text=bb.cget('text') if sc_data.Meal.B in f.meal else 'Cannot add to Breakfast'
        )
        bl.config(
            command=lambda *_: self.toggle_button(__i, 1),
            state=tk.NORMAL if sc_data.Meal.L in f.meal else tk.DISABLED,
            text=bl.cget('text') if sc_data.Meal.L in f.meal else 'Cannot add to Lunch'
        )
        bd.config(
            command=lambda *_: self.toggle_button(__i, 2),
            state=tk.NORMAL if sc_data.Meal.D in f.meal else tk.DISABLED,
            text=bd.cget('text') if sc_data.Meal.D in f.meal else 'Cannot add to Dinner'
        )

        self.__caloric_lookup[f.id] = [
            f.calories,
            f.macros.carbohydrates.starches + f.macros.carbohydrates.sugars + f.macros.carbohydrates.fiber,
            f.macros.proteins,
            f.macros.fats.trans + f.macros.fats.saturated
        ]

        f0.pack(fill=tk.BOTH, expand=True)
        f1.pack(side=tk.LEFT, fill=tk.BOTH, expand=False)
        f2.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        f3.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

    def toggle_button(self, __i, __j) -> None:
        s = not (self.__data[__i]['B'][1][__j])
        fid = cast(sc_data.Food, self.__data[__i]['f']).id

        self.__data[__i]['B'][1][__j] = s

        self.__data[__i]['B'][0][__j].configure(
            text="Add%s to %s" % (
                "ed" if s else "",
                self.__data[__i]['B'][2][__j]
            ),
            style="%s.TButton" % ("On" if s else "Off")
        )

        if s:
            # Add __j to selected foods
            self.__selected_food[fid] = [*self.__selected_food.get(fid, []), __j]
        else:
            # Remove __j from selected foods
            self.__selected_food[fid] = [v for v in self.__selected_food.get(fid, []) if v != __j]

        self.update_cal()

    def _fid_on_w(self, *_) -> None:
        self.fid.set(self.fid.get().strip().upper())

    def _pid_on_w(self, *_) -> None:
        self.pid.set(''.join([c for c in self.pid.get().strip() if c.isnumeric()]))

    def run(self) -> None:
        global FONT_MAIN, FONT_TITLE

        self.root.title("Food Companion Client")
        self.root.geometry("%dx%d+%d+%d" % (*self.lg_geo, *self.lg_pos))
        self.root.protocol("WM_DELETE_WINDOW", self.on_app_close)

        self.login_frame.pack(fill=tk.BOTH, expand=True)

        self.title_text.pack(fill=tk.X, expand=True)
        self.title_text.config(text="Food Companion", font=(FONT_TITLE, 18))

        self.fid_lbl.pack(padx=20, pady=(10, 0), fill=tk.X, expand=True)
        self.fid_lbl.config(text="Institution ID", anchor=tk.W, justify=tk.LEFT, font=(FONT_MAIN, 10))
        self.facility_id.pack(fill=tk.X, expand=False, padx=20, pady=(0, 10))
        self.fid_frm.pack(fill=tk.BOTH, expand=False)

        self.pid_lbl.pack(padx=20, pady=(10, 0), fill=tk.X, expand=True)
        self.pid_lbl.config(text="Numeric Patient ID", anchor=tk.W, justify=tk.LEFT, font=(FONT_MAIN, 10))
        self.patient_id.pack(fill=tk.X, expand=False, padx=20, pady=(0, 10))
        self.pid_frm.pack(fill=tk.BOTH, expand=False)

        self.pid.trace('w', self._pid_on_w)
        self.fid.trace('w', self._fid_on_w)

        self.dob_lbl.pack(padx=20, pady=(10, 0), fill=tk.X, expand=True)
        self.dob_lbl.config(text="Date of Birth", anchor=tk.W, justify=tk.LEFT, font=(FONT_MAIN, 10))
        self.login_cal.pack(fill=tk.X, expand=False, padx=20, pady=(0, 10))
        self.dob_frm.pack(fill=tk.BOTH, expand=False)

        self.login_button.pack(fill=tk.X, expand=True, padx=20, pady=10)

        self.mf_calorie_bar.pack(side=tk.BOTTOM, fill=tk.X, expand=False)
        self.status_lbl.pack(side=tk.BOTTOM, padx=20, pady=10, fill=tk.X, expand=False)

        self.update_reqs.append((UpdateReq.BG, self.login_frame))
        self.update_reqs.append((UpdateReq.BG, self.main_frame))
        self.update_reqs.append((UpdateReq.BG, self.pid_frm))
        self.update_reqs.append((UpdateReq.BG, self.fid_frm))
        self.update_reqs.append((UpdateReq.BG, self.dob_frm))
        self.update_reqs.append((UpdateReq.BG, self.root))
        self.update_reqs.append((UpdateReq.BG, self.status_lbl))

        self.update_reqs.append((UpdateReq.BG, self.fid_lbl))
        self.update_reqs.append((UpdateReq.FG, self.fid_lbl))

        self.update_reqs.append((UpdateReq.BG, self.pid_lbl))
        self.update_reqs.append((UpdateReq.FG, self.pid_lbl))

        self.update_reqs.append((UpdateReq.BG, self.dob_lbl))
        self.update_reqs.append((UpdateReq.FG, self.dob_lbl))

        self.update_reqs.append((UpdateReq.BG, self.title_text))
        self.update_reqs.append((UpdateReq.FG_AC, self.title_text))

        self.update_reqs.append((UpdateReq.FONT_MAIN, self.facility_id))
        self.update_reqs.append((UpdateReq.FONT_MAIN, self.patient_id))

        self.update()
        self.root.mainloop()

    def update(self) -> None:
        global _default_theme, FONT_MAIN, FONT_TITLE

        size_main = 12
        size_lg = 16

        self.style.configure(
            'TButton',
            background=_default_theme.AC1,
            foreground=_default_theme.BG,
            font=(FONT_MAIN, size_lg),
            focuscolor=_default_theme.AC1,
            bordercolor=_default_theme.BG,
            borderwidth=0,
            highlightcolor=_default_theme.BG,
            highlightthickness=0
        )

        self.style.map(
            'TButton',
            background=[('active', _default_theme.AC2), ('disabled', _default_theme.BG), ('readonly', _default_theme.BG)],
            foreground=[('active', _default_theme.BG), ('disabled', _default_theme.FG), ('readonly', _default_theme.FG)]
        )

        self.style.configure(
            'Off.TButton',
            background=_default_theme.BG,
            foreground=_default_theme.FG,
            font=(FONT_MAIN, 14),
            focuscolor=_default_theme.AC2,
            bordercolor=_default_theme.FG,
            borderwidth=1,
            highlightcolor=_default_theme.BG,
            highlightthickness=0
        )

        self.style.map(
            'Off.TButton',
            background=[('active', _default_theme.AC2), ('disabled', "#A0A0A0"), ('readonly', _default_theme.BG)],
            foreground=[('active', _default_theme.BG), ('disabled', "#000000"), ('readonly', _default_theme.FG)]
        )

        self.style.configure(
            'On.TButton',
            background=_default_theme.AC1,
            foreground=_default_theme.BG,
            font=(FONT_MAIN, 14),
            focuscolor=_default_theme.AC1,
            bordercolor=_default_theme.BG,
            borderwidth=0,
            highlightcolor=_default_theme.BG,
            highlightthickness=0
        )

        self.style.map(
            'On.TButton',
            background=[('active', _default_theme.AC2), ('disabled', "#A0A0A0"), ('readonly', _default_theme.BG)],
            foreground=[('active', _default_theme.BG), ('disabled', "#000000"), ('readonly', _default_theme.FG)]
        )

        self.style.configure(
            'TEntry',
            background=_default_theme.BG,
            foreground=_default_theme.AC1,
            fieldbackground=_default_theme.BG,
            selectbackground=_default_theme.AC2,
            selectforeground=_default_theme.BG,
            bordercolor=_default_theme.AC1,
            insertcolor=_default_theme.AC1,
            insertwidth=3,
        )

        self.style.map(
            'TEntry',
            background=[('disabled', _default_theme.BG), ('readonly', _default_theme.BG)],
            foreground=[('disabled', _default_theme.FG), ('readonly', _default_theme.FG)],
            fieldbackground=[('disabled', _default_theme.BG), ('readonly', _default_theme.BG)],
        )

        self.style.layout(f'TScrollbar',
                     [
                         (
                             f'TScrollbar.trough', {
                                 'children':
                                     [
                                         ('Vertical.Scrollbar.uparrow', {'side': 'top', 'sticky': ''}),
                                         ('Vertical.Scrollbar.downarrow', {'side': 'bottom', 'sticky': ''}),
                                         ('Vertical.Scrollbar.thumb',
                                          {'unit': '1', 'children': [
                                              ('Vertical.Scrollbar.grip',
                                               {'sticky': ''}
                                               )
                                          ], 'sticky': 'nswe'
                                           }
                                          )
                                     ],
                                 'sticky': 'ns'})
                     ])

        self.style.configure(f'TScrollbar', troughcolor=_default_theme.BG)

        self.style.configure(
            f'TScrollbar',
            background=_default_theme.BG,
            arrowcolor=_default_theme.AC1
        )
        self.style.map(
            f'TScrollbar',
            background=[
                ("active", _default_theme.AC1), ('disabled', _default_theme.BG)
            ],
            foreground=[
                ("active", _default_theme.AC2), ('disabled', _default_theme.BG)
            ],
            arrowcolor=[
                ('disabled', _default_theme.BG)
            ]
        )

        for (up, e) in self.update_reqs:
            try:
                match up:
                    case UpdateReq.BG:
                        e.config(bg=_default_theme.BG)

                    case UpdateReq.FG:
                        e.config(fg=_default_theme.FG)

                    case UpdateReq.ACTIVE_BG:
                        e.config(activebackground=_default_theme.AC2)

                    case UpdateReq.ACTIVE_FG:
                        e.config(activeforeground=_default_theme.BG)

                    case UpdateReq.FG_ER:
                        e.config(fg=_default_theme.ER)

                    case UpdateReq.FG_OK:
                        e.config(fg=_default_theme.OK)

                    case UpdateReq.FG_AC:
                        e.config(fg=_default_theme.AC1)

                    case UpdateReq.BG_AC:
                        e.config(bg=_default_theme.AC1)

                    case UpdateReq.REV_FG:
                        e.config(fg=_default_theme.BG)

                    case UpdateReq.FONT_MAIN:
                        e.config(font=(FONT_MAIN, size_main))

            except Exception as E:
                sys.stderr.write(f'[WinClient @ A.update] Could not apply {up} to {e}: {str(E)}\n')


def parse(inp: Dict[str, Any]) -> Dict[int, sc_data.Food]:

    d: Dict[int, sc_data.Food] = {}

    dm = {
        "breakfast":    sc_data.Meal.B,
        "lunch":        sc_data.Meal.L,
        "dinner":       sc_data.Meal.D
    }

    for m in inp:
        if m not in dm:
            continue  # Skips dietOrder and limits

        M = dm[m]

        for c in inp[m]:
            C = sc_data.FoodCategory._member_map_[c.upper()]

            for f in inp[m][c]:
                fid = f['id']
                if fid in d:  # This food already exists, we just need to append another meal to it.
                    if M not in d[fid].meal:
                        d[fid].meal.append(M)

                    continue      # Move on to the next food item.

                # This is a new food item and must be deserialized completely.

                name: str  = f['name']
                cal: float = f['calories']
                CSt: float = f['macros']['carbohydrates']['starches']
                CSu: float = f['macros']['carbohydrates']['sugars']
                CFi: float = f['macros']['carbohydrates']['fiber']
                Prt: float = f['macros']['protein']
                FTr: float = f['macros']['fats']['trans']
                FSa: float = f['macros']['fats']['saturated']
                SSC: float = f['servingSize']['count']
                SSU: str   = f['servingSize']['unit']
                diets: List[str] = f['appliesTo']

                d[fid] = sc_data.Food(
                    fid,
                    name,
                    cal,
                    sc_data.Macro(sc_data.Carbohydrates(CSt, CFi, CSu), Prt, sc_data.Fats(FTr, FSa)),
                    cast(List[sc_data.MealOption], list(map(lambda d: sc_data.MealOption._member_map_[d], diets))),
                    SSC,
                    cast(sc_data.ServingSizeUnit, sc_data.ServingSizeUnit._member_map_[SSU.upper()]),
                    [M],
                    cast(sc_data.FoodCategory, C)
                )

    return d


def parse_options(
        iid: str,
        pdob: int,
        pid: int
) -> tuple[sc_data.MealOption, dict[sc_data.FoodCategory, Any], dict[str, Dict[int, sc_data.Food]]] | None:
    global _id_to_food_map_, _name_to_fid_map

    mo = sc_client.WinClient.login(iid, pdob, pid)

    if not isinstance(mo, dict):
        return None

    food = parse(mo)
    _id_to_food_map_ = {**food}  # Copying this way to make sure the dictionaries aren't linked.
    _name_to_fid_map = {f.name: f.id for f in food.values()}

    do = cast(sc_data.MealOption, sc_data.MealOption._member_map_[mo['dietOrder'].upper()])

    lim = mo['limits']
    oLim: Dict[sc_data.FoodCategory, Any] = {}

    for l, L in lim.items():
        oLim[cast(sc_data.FoodCategory, sc_data.FoodCategory._member_map_[l.upper()])] = L

    # Sort do by FoodCategory -> add limits to each cat.
    food_p = {}

    for i, d in food.items():
        food_p[cast(sc_data.Food, d).category.name.title()] = {
            **food_p.get(cast(sc_data.Food, d).category.name.title(), {}),
            i: d
        }

    return do, oLim, food_p


if __name__ == "__main__":
    sc_helper.UserData.cleanDir()
    App()
