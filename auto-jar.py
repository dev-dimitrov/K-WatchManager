import os

def make_class():
    os.system('javac --module-path %FX_PATH% --add-modules=javafx.controls,javafx.fxml src\\*.java -d classes')

def ask_4_src():
    print("Do you have any resource folder?, just type enter if you haven't: ",end="")
    choice = input()
    if len(choice) != 0:
        os.system('powershell cp -r src/{}/ classes'.format(choice))
        

def make_jar():
    print('Type a name for the jar: ',end="")
    choice1 = input()
    
    print("Type the main-class name: ",end="")
    choice2 = input()
    
    os.system('jar --create --file=app/{}.jar --main-class={} -C classes .'.format(choice1,choice2))
    print('JAR executable created!')


def main():
    make_class()
    ask_4_src()
    make_jar()
    
    print('\nEverything done!')



if __name__ == '__main__':
    main()
