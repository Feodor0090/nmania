using System.Reflection;
using static System.Console;

// ReSharper disable UnusedMember.Local

namespace TestResultsProcessor;

public class Analyzer
{
    public Analyzer()
    {
        var t = File.ReadAllLines("emulator.log").Where(s => !s.StartsWith("Get class"))
            .Where(s => !s.StartsWith("System.getProperty#")).Select(s =>
            {
                if (s.StartsWith(">>"))
                    return s.Substring(2);
                return s;
            }).Where(s => !s.StartsWith('\t')).Where(s => !s.StartsWith("Custom.jar.getResourceStream"))
            .Where(s => !s.StartsWith("Launch MIDlet class"));
        Log = t.ToList();
        Sets = File.ReadAllText("sets.json");
    }

    public readonly List<string> Log;
    public readonly string Sets;

    public IEnumerable<string> GetCat(string cat)
    {
        return Log.Where(s => s.StartsWith($"({cat})")).Select(s => s.Substring(3 + cat.Length));
    }

    public bool CatHas(string cat, string data)
    {
        return GetCat(cat).Any(s => s.Contains(data));
    }

    public bool Run()
    {
        bool ok = true;
        var allMethods = typeof(Analyzer).GetMethods(BindingFlags.NonPublic | BindingFlags.Instance);
        foreach (var mi in allMethods)
        {
            if (mi.GetCustomAttribute<CheckAttribute>() == null) continue;
            ForegroundColor = ConsoleColor.Green;
            WriteLine($"Performing check \"{mi.Name}\"");
            ForegroundColor = ConsoleColor.White;
            bool lok = (bool) mi.Invoke(this, null)!;
            if (lok)
            {
                ForegroundColor = ConsoleColor.Green;
                WriteLine("[OK]");
            }
            else
            {
                ForegroundColor = ConsoleColor.Red;
                WriteLine("[FAILED]");
            }

            WriteLine();
            ok = ok && lok;
        }

        ForegroundColor = ConsoleColor.White;
        return ok;
    }

    [Check]
    private bool NoErrors()
    {
        var all = Log
            // get all lines with sus text
            .Where(x =>
                x.Contains("exception", StringComparison.InvariantCultureIgnoreCase) ||
                x.Contains("error", StringComparison.InvariantCultureIgnoreCase) ||
                x.Contains("failed", StringComparison.InvariantCultureIgnoreCase))
            // filter out expected errors
            .Where(x => !x.Contains("No line matching interface") && !x.Contains("..\\kemulator.cfg")).ToList();

        if (all.Any())
        {
            WriteLine("There are some errors in logs!");
            foreach (var s in all)
            {
                WriteLine(s);
            }

            return false;
        }

        return true;
    }

    [Check]
    private bool CorrectMap()
    {
        const string hash = "bdfacc5dc315b92b757eb0bb2472bdd3";
        return CatHas("player", hash) && CatHas("player", "riu's MX");
    }

    [Check]
    private bool AccurateAutoplay()
    {
        return GetCat("judgment").All(s => s.Contains("PERFECT"));
    }

    [Check]
    private bool CorrectNotesCount()
    {
        int c = GetCat("judgment").Count();
        if (c != 321 + 6)
        {
            WriteLine($"Judgments count was {c}, 327 expected!");
            return false;
        }

        return true;
    }

    [Check]
    private bool CorrectScreensVisited()
    {
        string[] chain = Log.SelectMany(s => Wrap(x =>
        {
            if (x.StartsWith("(ui)"))
            {
                if ((x.Contains("Returning") || x.Contains("Pushing")) && x.Contains('>'))
                    return x.Split('.')[^1].Split(' ')[0];
            }

            if (x.StartsWith("(app)") && x.Contains("Changing global displayable"))
                return x.Split('.')[^1];
            return null;
        }, s)).Select(x => new string(x.Where(c => c == c.ToString().ToUpperInvariant()[0]).ToArray())).ToArray();

        string[] exp =
            "nd as ms sss ms bs ms ss vs nb vs ss as ss ss ss dss fss dss ss ms ss a ss ms bmss a bu a bmss ms bmss ds mss ds bmss ms ss a ss ms bmss ds pls p rs nd ds bmss ms"
                .ToUpper().Split(' ');

        for (int i = 0; i < exp.Length; i++)
        {
            if (i >= chain.Length)
            {
                WriteLine($"{exp[i]} was expected, but game already exited.");
                return false;
            }

            if (exp[i] != chain[i])
            {
                WriteLine($"{exp[i]} was expected, but {chain[i]} was active (screen #{i + 1}).");
                return false;
            }
        }

        if (chain.Length > exp.Length)
        {
            WriteLine("More screens were visited than expected!");
            return false;
        }

        return true;
    }

    [Check]
    private bool ThreadSuspendedBeforePlay()
    {
        int fji = Log.FindIndex(x => x.StartsWith("(judgment)"));
        int i = Log.IndexOf("(ui) Suspending rendering thread...", fji - 10, 10);
        return i != -1;
    }

    [Check]
    private bool SettingsOk()
    {
        if (!Sets.Contains("\"mods\":2,"))
        {
            WriteLine("Mods were not saved!");
            return false;
        }

        if (!Sets.Contains("\"drawcounters\":true,"))
        {
            WriteLine("FPS was not enabled!");
            return false;
        }

        if (!Sets.Contains("\"dim\":75,"))
        {
            WriteLine("Dim is not 75!");
            return false;
        }

        return true;
    }


    private static string[] Wrap(Func<string, string?> func, string s)
    {
        var res = func(s);
        if (res == null) return Array.Empty<string>();
        return new[] {res};
    }

    [AttributeUsage(AttributeTargets.Method)]
    private class CheckAttribute : Attribute
    {
    }
}