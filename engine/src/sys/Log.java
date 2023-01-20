package sys;


import com.strongjoshua.console.GUIConsole;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log extends Formatter {

    public static GUIConsole console = new GUIConsole();

    static {
//        console = EditorStage.getInstance().getConsole();
    }

    static public final int LEVEL_NONE = 6;
    /** Critical errors. The application may no longer work correctly. */
    static public final int LEVEL_ERROR = 5;
    /** Important warnings. The application will continue to work correctly. */
    static public final int LEVEL_WARN = 4;
    /** Informative messages. Typically used for deployment. */
    static public final int LEVEL_INFO = 3;
    /** Debug messages. This level is useful during development. */
    static public final int LEVEL_DEBUG = 2;
    /** Trace messages. A lot of information is logged, so this level is usually only needed when debugging a problem. */
    static public final int LEVEL_TRACE = 1;

    /** The level of messages that will be logged. Compiling this and the booleans below as "final" will cause the compiler to
     * remove all "if (Log.info) ..." type statements below the set level. */
    static private int level = LEVEL_INFO;

    /** True when the ERROR level will be logged. */
    static public boolean ERROR = level <= LEVEL_ERROR;
    /** True when the WARN level will be logged. */
    static public boolean WARN = level <= LEVEL_WARN;
    /** True when the INFO level will be logged. */
    static public boolean INFO = level <= LEVEL_INFO;
    /** True when the DEBUG level will be logged. */
    static public boolean DEBUG = level <= LEVEL_DEBUG;
    /** True when the TRACE level will be logged. */
    static public boolean TRACE = level <= LEVEL_TRACE;

    @Override
    public String format(LogRecord record) {
        String builder = "["+record.getLevel() + "] "+record.getSourceClassName()+"."+
                record.getSourceMethodName()+" "+"\n"+
                this.formatMessage(record) +
                System.lineSeparator();
        // pre-Java7: builder.append(System.getProperty('line.separator'));
        //     BaseScreen.MyCommandExecutor.log(builder);
        return builder;
    }
    //conveinence method to add a logger to a class
    public static Logger addLogger(Class<?> clazz) {
        Logger logger = new Logger(clazz.getName(), null) {
            @Override
            public void info(String msg) {
                super.info(msg);
               /* if (EditorStage.logWindow != null) {
                    EditorStage.logWindow.addText(msg+"\n");
                }*/



            }
        };

        Handler handler = new java.util.logging.ConsoleHandler();
        handler.setFormatter(new Log());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        return logger;

    }


    public static Log Log = new Log();
    static Throwable ex;
        public static void info(String message){
            info(null, message);
        }
        public static void info(String category,String message) {
            level= LEVEL_INFO;

             final long firstLogTime = System.currentTimeMillis();
            StringBuilder builder = new StringBuilder(256);

            long time = System.currentTimeMillis() - firstLogTime;
            long minutes = time / (1000 * 60);
            long seconds = time / (1000) % 60;
            if (minutes <= 9) builder.append('0');
            builder.append(minutes);
            builder.append(':');
            if (seconds <= 9) builder.append('0');
            builder.append(seconds);

            switch (level) {
                case LEVEL_ERROR:
                    builder.append(" ERROR: ");
                    break;
                case LEVEL_WARN:
                    builder.append("  WARN: ");
                    break;
                case LEVEL_INFO:
                    builder.append("  INFO: ");
                    break;
                case LEVEL_DEBUG:
                    builder.append(" DEBUG: ");
                    break;
                case LEVEL_TRACE:
                    builder.append(" TRACE: ");
                    break;
            }

            if (category != null) {
                builder.append('[');
                builder.append(category);
                builder.append("] ");
            }

            builder.append(message);

            if (ex != null) {
                StringWriter writer = new StringWriter(256);
                ex.printStackTrace(new PrintWriter(writer));
                builder.append('\n');
                builder.append(writer.toString().trim());
            }


           // EditorStage.logWindow.addText(builder.toString()+"\n");
//            EditorConsole.console.log(builder.toString());
         /*   if (EditorStage.getInstance()!= null) {
                EditorStage.getInstance().getConsole().log(builder.toString());
            }
*/

            System.out.println(builder.toString());

        }}






