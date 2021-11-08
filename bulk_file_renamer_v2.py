import os

import regex
from natsort import natsort_key

print("Range changer?")
if input(">"):
    while True:
        count = 0
        oldRange = input("\nRange:\n>")
        shift = int(input("Shift:\n>"))
        files = list(next(os.walk("."))[2])
        success = []
        failed = []
        lowLim, upLim = [int(x) for x in oldRange.split(" -> ")]
        if lowLim + shift < 0 or lowLim > upLim:
            print("Kimochi Warui!")
            continue
        if shift < 0:
            digits = len(str(upLim))
            files.sort(key=natsort_key)
            files.pop(-1)
        else:
            digits = len(str(upLim + shift))
            files.sort(key=natsort_key, reverse=True)
            files.pop(0)
        for old in files:
            num, new = os.path.splitext(old)
            num = int(num)
            if lowLim <= num <= upLim:
                num += shift
                new = str(num) + new
                for _ in range(digits - len(str(num))):
                    new = "0" + new
                if old == new:
                    continue
                try:
                    if os.path.exists(new):
                        raise FileExistsError
                    os.rename(old, new)
                    success.append((old, new))
                    count += 1
                except:
                    failed.append(old)
        if count == 0 and not failed:
            print(f"YARE YARE DAZE... No files in \"{oldRange}\" range exist!")
        elif count == 0:
            print("SUGE! All files failed!\n"
                  "Nani ga Anta?!")
            for file in failed:
                print(file)
        else:
            print(f"YATTA! Renamed {count} files")
            if failed:
                print("But i have failed to rename some files for some reason.\n"
                      "You'll figure it out.\n"
                      "Have a nice day.\n"
                      "Files failed:")
                for file in failed:
                    print(file)
            print("\nHey... You there... Yes you... Want a reverse? i got all kinds of reverses... even reverse ****s.")
            if input(">"):
                failed = []
                for tupl in success:
                    try:
                        if os.path.exists(tupl[0]):
                            raise FileExistsError
                        os.rename(tupl[1], tupl[0])
                    except:
                        failed.append(tupl)
                if failed:
                    print("Dude really... go see someone for this bad luck.\n"
                          "Files failed:")
                    for old, new in failed:
                        print(f"Reversing {old} -> {new}")
                print("Ah Yokatta! Reverse Kanryou!")
else:
    print("\nUseful shit in case you need it:\n"
          "   .    : Any character\n"
          "   \\d   : Any digit\n"
          "   \\-   : Escape hyphen inside [ ... ]\n"
          "( ... ) : Group\n"
          "   ^    : Not\n"
          "   ?    : Once or none\n"
          "   *    : Zero or more\n"
          " {#,#}  : At least # and at most #\n"
          " {num}  : Placeholder for the number to remain unchanged (has to be in both old and new name to work)\n"
          "{title} : Placeholder for the title to remain unchanged")
    while True:
        count = 0
        digits = 0
        numHolder = "{num}"
        titleHolder = "{title}"
        titles = None
        regx = bool(input("\nSmart enough for regex?\n>"))
        oldName = input("Old name:\n>")
        newName = input("New name:\n>")
        if (numHolder in oldName and numHolder in newName) or titleHolder in newName:
            if titleHolder in newName and titleHolder not in oldName:
                try:
                    with open("Titles.txt", "r") as f:
                        titles = {}
                        for i, l in enumerate(f):
                            titles[l.partition(" ")[0]] = l.partition(" ")[2].replace("\n", "")
                except IOError:
                    print("Dude.. Want titles, provide titles. That's how life works.")
                    continue
            files = list(next(os.walk("."))[1]) + list(next(os.walk("."))[2])
            data = []
            success = []
            failed = []
            files.sort(key=natsort_key)
            if regx:
                filePattern = oldName
            else:
                filePattern = regex.escape(oldName)
                numHolder = r"\{num\}"
                titleHolder = r"\{title\}"
            numPattern = "(" + filePattern.replace(titleHolder, ".+").replace(numHolder, r")\K(\d+(-\d+)*)(?=") + ")"
            titlePattern = "(" + filePattern.replace(titleHolder, r")\K(.+)(?=").replace(numHolder, r"\d+(-\d+)*") + ")"
            filePattern = filePattern.replace(numHolder, r"\d+(-\d+)*").replace(titleHolder, r".+")
            for file in files:
                if regex.fullmatch(filePattern, file):
                    if "{num}" in oldName and "{num}" in newName:
                        num = regex.search(numPattern, file)[0]
                    else:
                        num = ""
                    if titles:
                        try:
                            title = titles[num]
                        except KeyError:
                            title = ""
                    elif "{title}" in newName:
                        title = regex.search(titlePattern, file)[0]
                    else:
                        title = ""
                    data.append((file, num, title))
                    if num:
                        for number in num.split("-"):
                            digits = max(digits, len(str(int(number))))
            for file, n, title in data:
                num = ""
                if n:
                    for number in reversed(n.split("-")):
                        number = str(int(number))
                        num = f"{number}-{num}"
                        for _ in range(digits - len(number)):
                            num = f"0{num}"
                num = num[:-1]
                name = newName.format(num=num, title=title)
                try:
                    if os.path.exists(name):
                        raise FileExistsError
                    os.rename(file, name)
                    success.append((file, name))
                    count += 1
                except:
                    failed.append((file, name))
            if count == 0 and not failed:
                print(f"YARE YARE DAZE.. \"{oldName}\" doesn't even exist!")
            elif count == 0:
                print("SUGE! All files failed!\n"
                      "Nani ga Anta?!")
                for old, new in failed:
                    print(f"{old} -> {new}")
            else:
                print(f"YATTA.. Renamed {count} files, From \"{success[0][0]}\" to \"{success[-1][1]}\".")
                if failed:
                    print("But i have failed to rename some files for some reason.\n"
                          "You'll figure it out.\n"
                          "Have a nice day.\n"
                          "Files failed:")
                    for old, new in failed:
                        print(f"{old} -> {new}")
                print("\nPsst... Looking for a special treatment? xoxo")
                if input(">"):
                    failed = []
                    for tupl in success:
                        try:
                            if os.path.exists(tupl[0]):
                                raise FileExistsError
                            os.rename(tupl[1], tupl[0])
                        except:
                            failed.append(tupl)
                    if failed:
                        print("Oh... I broke... I guess i'm not enough for you, You sure are lively.\n")
                        for old, new in failed:
                            print(f"{old} -> {new}")
                    else:
                        print("Ah Yokatta! Special treatment Kanryou!")
        else:
            try:
                os.rename(oldName, newName)
                print("BEHOLD MY SUPREME POWER! I HAVE RENAMED ONE FILE!\n"
                      "I can try to reverse my power if you want.")
                if input(">"):
                    try:
                        os.rename(newName, oldName)
                    except:
                        print("After a second thought... You do it!")
            except:
                print(r"Meh... I knew it wouldn't work. ¯\_(ツ)_/¯")
