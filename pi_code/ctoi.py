def ctoi():
    print("------------------------------------------------0123456789ABCDEF")
    str = raw_input("Enter name to convert (less then 16 Character): ")
    if len(str)<=16:
        x = [0] * 16  #array filled with zeroes

        for i, w in enumerate(str):
            x[i] = ord(w)
        return x
    else:
        print("The put in string is too long: ", str, len(str))
        return 0
    
