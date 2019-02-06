package training.selenium.commandline;

import java.io.StringWriter;
import java.io.PrintWriter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Args {
    private JCommander jCommander;
    private String lineSeparator;

    public Args(String lineSeparator) {
        jCommander = JCommander.newBuilder().addObject(this).build();
        jCommander.setProgramName("training-selenium");

        this.lineSeparator = lineSeparator;
    }

    public boolean parseCommandLine(String[] args) {
        try {
            jCommander.parse(args);
            return true;
        } catch (ParameterException e) {
            String message = e.getMessage();
            String exceptionMessage = "Error: ";
            if (message != null)
                exceptionMessage += message;
            else
                exceptionMessage += e.getClass().getSimpleName() + "; " + e;

            System.err.println(lineSeparator + exceptionMessage + lineSeparator + "---== Stack begin ==---" + lineSeparator + getStackTrace(e) + "---==  Stack end  ==---");
            e.usage();
            return false;
        }
    }

    public void printArgsInfo(String[] args) {
        System.out.println("argsNum: " + args.length);
        for (int argsIndex = 0; argsIndex < args.length; argsIndex++)
            System.out.println("arg[" + argsIndex + "]: " + args[argsIndex]);
        System.out.println();
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public boolean needHelp() {
        return help;
    }

    public void usage() {
        jCommander.usage();
    }

    public String getWebDriver() {
        return webDriver;
    }

    @Parameter(names = "-webdriver", description = "WebDriver - 'c' or 'f' (without quotes)", required = true)
    private String webDriver;

    @Parameter(names = {"--help", "/?"}, description = "Print usage information", help = true)
    private boolean help = false;
}
