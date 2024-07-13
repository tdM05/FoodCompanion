import customtkinter as ctk

ctk.set_appearance_mode("System")  # Follow the system appearance (dark/light mode)
ctk.set_default_color_theme("dark-blue")


app = ctk.CTk()
app.geometry('400x240')

def button_pressed():
    print("btn")

button = ctk.CTkButton(master=app, text="CTKButton", command=button_pressed)
button.pack(expand=False)

app.mainloop()
