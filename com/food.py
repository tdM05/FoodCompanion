"""
    If this file is imported, then it provides utilities for reading and filtering the food database.
    If this file is run standalone, it provides an interface for editing the food database.
"""

import data, json
from prettytable import PrettyTable
from typing import cast, Dict, Any, List, Optional
from data import Food, ServingSizeUnit, MealOption, Macro, Carbohydrates, Fats, Meal, FoodCategory


foods: Dict[int, Food] = {}


def filter_meals(diet: MealOption) -> Dict[str, Any]:
    global foods

    load()

    limits = {
        FoodCategory.ENTREE.name.title():       2,
        FoodCategory.CONDIMENT.name.title():    None,
        FoodCategory.SOUPS.name.title():        1,
        FoodCategory.FRUIT.name.title():        2,
        FoodCategory.STARCH.name.title():       1,
        FoodCategory.BEVERAGE.name.title():     2,
        FoodCategory.DESERT.name.title():       1,
        FoodCategory.VEGETABLE.name.title():    None
    }

    d = {
        "breakfast": {},
        "lunch": {},
        "dinner": {}
    }

    dm = {
        Meal.B: "breakfast",
        Meal.L: "lunch",
        Meal.D: "dinner"
    }

    print(foods)

    for f in foods.values():
        if diet not in f.diets:
            continue

        for m in f.meal:
            d[dm[m]][f.category.name.title()] = [
                *d[dm[m]].get(f.category.name.title(), []),
                {
                    'id': f.id,
                    'name': f.name.title(),
                    'calories': f.calories,
                    'macros':
                        {
                            'carbohydrates':
                                {
                                    'starches': f.macros.carbohydrates.starches,
                                    'sugars': f.macros.carbohydrates.sugars,
                                    'fiber': f.macros.carbohydrates.fiber
                                },
                            'protein': f.macros.proteins,
                            'fats':
                                {
                                    'trans': f.macros.fats.trans,
                                    'saturated': f.macros.fats.saturated
                                }
                        },
                    'servingSize':
                        {
                            'count': f.serving_size_count,
                            'unit': f.serving_size_unit.name.lower()
                        },
                    'appliesTo': list(map(
                        lambda d: cast(MealOption, d).name,
                        f.diets
                    ))
                }
            ]

    d['limits'] = limits

    return d


def load():
    global foods

    d = {
        "breakfast": {},
        "lunch": {},
        "dinner": {}
    }

    dm = {
        "breakfast":    Meal.B,
        "lunch":        Meal.L,
        "dinner":       Meal.D
    }

    with open("food.json", "r") as i:
        inp = json.loads(i.read())
        i.close()

    for m in inp:
        M = dm[m]

        for c in inp[m]:
            C = FoodCategory._member_map_[c.upper()]

            for f in inp[m][c]:
                fid = f['id']
                if fid in foods:  # This food already exists, we just need to append another meal to it.
                    if M not in foods[fid].meal:
                        foods[fid].meal.append(M)

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

                foods[fid] = \
                    Food(
                        fid,
                        name,
                        cal,
                        Macro(Carbohydrates(CSt, CFi, CSu), Prt, Fats(FTr, FSa)),
                        cast(List[MealOption], list(map(lambda d: MealOption._member_map_[d], diets))),
                        SSC,
                        cast(ServingSizeUnit, ServingSizeUnit._member_map_[SSU.upper()]),
                        [M],
                        cast(FoodCategory, C)
                    )


def save():
    global foods

    d = {
        "breakfast": {},
        "lunch":     {},
        "dinner":    {}
    }

    dm = {
        Meal.B: "breakfast",
        Meal.L: "lunch",
        Meal.D: "dinner"
    }

    for f in foods.values():
        for m in f.meal:
            d[dm[m]][f.category.name.title()] = [
                *d[dm[m]].get(f.category.name.title(), []),
                {
                    'id':                       f.id,
                    'name':                     f.name.title(),
                    'calories':                 f.calories,
                    'macros':
                        {
                            'carbohydrates':
                                {
                                    'starches': f.macros.carbohydrates.starches,
                                    'sugars':   f.macros.carbohydrates.sugars,
                                    'fiber':    f.macros.carbohydrates.fiber
                                },
                            'protein':          f.macros.proteins,
                            'fats':
                                {
                                    'trans':   f.macros.fats.trans,
                                    'saturated': f.macros.fats.saturated
                                }
                        },
                    'servingSize':
                        {
                            'count':            f.serving_size_count,
                            'unit':             f.serving_size_unit.name.lower()
                        },
                    'appliesTo':                list(map(
                        lambda d: cast(MealOption, d).name,
                        f.diets
                    ))
                }
            ]

    with open("food.json", "w") as o:
        o.write(json.dumps(d, indent=4))
        o.close()


def get_new_id() -> int:
    global foods

    if foods == {}:
        return 0

    if len(foods) == (max(foods.keys()) + 1):
        return len(foods)

    for i in range(len(foods)):  # We're missing a number somewhere.
        if i not in foods:
            return i


def new_item():
    global foods

    bld = input("BLD (separate w/ space if available for multiple): ").strip().upper().split()
    cat = input("CAT [E/So/St/V/F/D/B/C]: ").strip().upper()
    cal = float(input("CAL: ").strip())
    cSt = float(input("STARCH: ").strip())
    cSu = float(input("SUGARS: ").strip())
    cFi = float(input("FIBER:  ").strip())
    prt = float(input("PROTEIN: ").strip())
    fTr = float(input("TRANS FATS: ").strip())
    fSa = float(input("SAT. FATS: ").strip())
    sU = input("Serving Unit [ML/G/CNT]: ").strip().upper()
    sC = float(input("Serving size: ").strip())
    nom = input("Name: ").strip()

    print("The following DIETS are avaialble:")

    for (n, e) in MealOption._member_map_.items():
        print(f"| \t[{e.value}] {n} DIET.")

    diets = input("Enter the NUMBER of the DIET(S) that this food is available to (separate w/ a space): ").split()
    diets = list(map(lambda d: int(d), diets))

    for b in bld:
        assert b in ('B', 'L', 'D')
    assert max(diets) <= max(MealOption._value2member_map_.keys())
    assert max(diets) >= 0
    assert cat in ('E', 'SO', 'ST', 'V', 'F', 'D', 'B', 'C')
    assert sU in ('ML', 'G', 'CNT')
    assert len(bld)
    assert cal > 0
    assert sC > 0

    assert cSt >= 0
    assert cSu >= 0
    assert cFi >= 0
    assert prt >= 0
    assert fTr >= 0
    assert fSa >= 0

    nid = get_new_id()
    print(f"Assigned ID {nid} to {nom}")

    foods[nid] = \
        Food(
            nid,
            nom,
            cal,
            Macro(
                Carbohydrates(cSt, cFi, cSu),
                prt,
                Fats(fTr, fSa)
            ),
            cast(List[MealOption], list(map(lambda d: MealOption._value2member_map_[d], diets))),
            sC,
            {
                'ML': ServingSizeUnit.ML,
                'G': ServingSizeUnit.G,
                'CNT': ServingSizeUnit.COUNT
            }[sU],
            cast(List[Meal], list(map(lambda d: Meal._member_map_[d], bld))),
            {
                'E': FoodCategory.ENTREE,
                'SO': FoodCategory.SOUPS,
                'ST': FoodCategory.STARCH,
                'V': FoodCategory.VEGETABLE,
                'F': FoodCategory.FRUIT,
                'D': FoodCategory.DESERT,
                'B': FoodCategory.BEVERAGE,
                'C': FoodCategory.CONDIMENT
            }.get(cat, FoodCategory.CONDIMENT)
        )


def update():
    save()
    load()


def delete():
    global foods

    update()
    print_list()

    fid = int(input("Select the ID of the food that you want to delete: ").strip())
    if fid not in foods:
        print("Invalid selection. Aborted.")
        return

    conf = input(f"Are you sure you want to delete \"{foods[fid].name.upper()}\" (y/N): ").strip().upper()
    if conf not in ('Y', 'YES'):
        print("Aborted.")
        return

    foods.pop(fid)
    update()

    print("Updated food database.")


def print_list():
    global foods
    t = PrettyTable(['ID', 'Name', 'Meal Times', 'Category', 'Calories', 'Starches', 'Sugars', 'Fiber', 'Protein', 'Trans Fats', 'Saturated Fats', 'Serving Size', 'Diet Orders'])

    for f in foods.values():
        t.add_row(
            [
                f.id,
                f.name.title(),
                '/'.join(map(lambda m: {
                    Meal.B: 'Breakfast',
                    Meal.L: 'Lunch',
                    Meal.D: 'Dinner'
                }[m], f.meal)),
                f.category.name.title(),
                f.calories,
                f.macros.carbohydrates.starches,
                f.macros.carbohydrates.sugars,
                f.macros.carbohydrates.fiber,
                f.macros.proteins,
                f.macros.fats.trans,
                f.macros.fats.saturated,
                f'{f.serving_size_count} {f.serving_size_unit.name.lower()}',
                ', '.join(
                    map(
                        lambda d: cast(MealOption, d).name.replace('_', ' '),
                        f.diets
                    )
                )
            ]
        )

    print(t)


if __name__ == "__main__":
    load()
    print(foods)

    while True:
        comm = input("COMMAND: ").strip().upper()

        if comm in ("NEW", "CREATE", "ADD"):
            new_item()

        elif comm in ("SAVE", "WRITE"):
            save()

        elif comm == 'LIST':
            update()
            print_list()

        elif comm in ('DELETE', 'REMOVE'):
            delete()

        elif comm == 'HELP':
            print("Commands: ")
            print()
            print("NEW..........................................Add a food item to the menu.")
            print("     Aliases: CREATE, ADD")
            print()
            print("SAVE....................................Save any changes to the database.")
            print("     Aliases: WRITE")
            print()
            print("LIST............................Get a list of all food items in the menu.")
            print()
            print("DELETE..................................Remove a food item from the menu.")
            print("     Aliases: REMOVE")
            print()

        else:
            save()
            break  # Exit

