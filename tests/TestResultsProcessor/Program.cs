namespace TestResultsProcessor;

public static class Program
{
    private static int Main()
    {
        return new Analyzer().Run() ? 0 : 1;
    }
}