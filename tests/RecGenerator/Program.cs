List<string> o = new();
o.Add(@"C:\Users\sym_ansel\workspace\nmania\deployed\Nokia_SDK_2_0_Java\nmania.jar");
o.Add("3743186032164333677");

int time = 60;

foreach (var l in File.ReadAllLines(args[0] + ".txt"))
{
    if(string.IsNullOrWhiteSpace(l))
        continue;
    if (l[0] == '/')
        continue;
    if (l.StartsWith("wait"))
    {
        int dt;
        if (l.Length == 4)
            dt = 100;
        else
            dt = int.Parse(l.Substring(4));
        time += dt;
        continue;
    }

    string k;

    switch (l)
    {
        case "w":
            k = "-1";
            break;
        case "s":
            k = "-2";
            break;
        case "a":
            k = "-3";
            break;
        case "d":
            k = "-4";
            break;
        case "q":
            k = "-6";
            break;
        case "e":
            k = "-7";
            break;
        case "f":
            k = "-5";
            break;
        default:
            k = l;
            break;
    }

    o.Add($"{time}:0{k}");
    o.Add($"{time + 2}:1{k}");
    time += 4;
}

File.WriteAllLines(args[0] + ".rec", o.ToArray());