package training.selenium;

import java.util.Locale;

import training.selenium.commandline.Args;

public class App {
    static public final String LineSeparator = System.getProperty("line.separator");

    public static void main(String[] rowArgs) {
        Locale.setDefault(Locale.US);
        Args args = new Args(LineSeparator);
        if (!args.parseCommandLine(rowArgs))
            System.exit(1);
        if (args.needHelp()) {
            args.printArgsInfo(rowArgs);
            args.usage();
            System.exit(0);
        }

        System.out.println(args.getWebDriver());

        System.out.println(LineSeparator + "All done successfully" + LineSeparator);
        System.exit(0);
    }
}
