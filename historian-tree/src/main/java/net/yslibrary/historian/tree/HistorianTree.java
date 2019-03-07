package net.yslibrary.historian.tree;

import net.yslibrary.historian.Historian;

import timber.log.Timber;


public class HistorianTree extends Timber.Tree {

    private final Historian historian;

    private HistorianTree(Historian historian) {
        this.historian = historian;
    }

    public static HistorianTree with(Historian historian) {
        return new HistorianTree(historian);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (tag == null)
            tag = "";
        historian.log(priority, tag, message);
    }

    private static final int STACK_TRACE_LEVELS_UP = 5;


    /**
     * Get the current line number. Note, this will only work as called from
     * this class as it has to go a predetermined number of steps up the stack
     * trace. In this case 5.
     *
     * @return int - Current line number.
     * @author kvarela
     */
    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getLineNumber();
    }

    /**
     * Get the current class name. Note, this will only work as called from this
     * class as it has to go a predetermined number of steps up the stack trace.
     * In this case 5.
     *
     * @return String - Current line number.
     * @author kvarela
     */
    private static String getClassName() {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getFileName();

        // kvarela: Removing ".java" and returning class name
        return fileName.substring(0, fileName.length() - 5);
    }

    /**
     * Get the current method name. Note, this will only work as called from
     * this class as it has to go a predetermined number of steps up the stack
     * trace. In this case 5.
     *
     * @return String - Current line number.
     * @author kvarela
     */
    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getMethodName();
    }

    /**
     * Returns the class name, method name, and line number from the currently
     * executing log call in the form .()-
     *
     * @return String - String representing class name, method name, and line
     * number.
     * @author kvarela
     */
    private static String getClassNameMethodNameAndLineNumber() {
        return "[" + getClassName() + "." + getMethodName() + "()-" + getLineNumber() + "]: ";
    }
}