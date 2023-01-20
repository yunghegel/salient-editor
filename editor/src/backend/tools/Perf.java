package backend.tools;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import editor.project.settings.Settings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Perf
{
    private static long total =0;

    public static class Metric{
        public int index;
        public boolean started;
        public long startTime;
        public long duration;

    }
    static FileWriter fileWriter;
    static PrintWriter writer;
    static {
        try {
            fileWriter = new FileWriter("profiler.txt");
            fileWriter.flush();
            fileWriter.close();
            fileWriter = new FileWriter("profiler.txt",true);

            writer = new PrintWriter(fileWriter);


        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    public static ObjectMap<String, Metric> metrics = new ObjectMap<String, Metric>();
    private static Array<String> names = new Array<String>();
    private static Array<PerfEndedListener> listeners = new Array<PerfEndedListener>();

    public static int start(String name){
        if(Settings.GameConfig.DEBUG){
            Metric m = metrics.get(name);
            if(m != null){
                if(!m.started){
                    m.started = true;
                    m.startTime = getTime();
                }
                return m.index;
            }else{
                m = new Metric();
                m.index = names.size;
                names.add(name);
                metrics.put(name, m);
                m.started = true;
                m.startTime = getTime();
                return m.index;
            }
        }
        return -1;
    }

    public static void end(int index){
        if(Settings.GameConfig.DEBUG){
            Metric m = metrics.get(names.get(index));
            if(m.started){
                m.duration = getTime() - m.startTime;
                m.started = false;
            }
            for (PerfEndedListener listener : listeners) {
                listener.perfEnded(m, names.get(index));
            }
            writer.println(names.get(index) + ": " + m.duration);
            writer.flush();
            total += m.duration;
        }
    }
    public static void flush(int index) {
        if(Settings.GameConfig.DEBUG){
            String name = names.get(index);
            Metric m = metrics.get(name);
            m.duration = getTime() - m.startTime;
            log(name, m);
            m.started = false;

        }
    }

    private static void log(String name, Metric m) {
        if(m.duration < 2000L){
            Log.info(name, m.duration + "ms");
        }else{
            Log.info(name, Float.toString((float)(m.duration / 1000L)));
        }

    }

    public static void flush() {
        if(Settings.GameConfig.DEBUG){
            for(String name : names){
                Metric m = metrics.get(name);
                log(name, m);

            }
            names.clear();
            metrics.clear();
        }
        writer.println("total: "+total);
    }

    private static long getTime() {
        return System.currentTimeMillis();
    }

    public static void addListener(PerfEndedListener listener){
        listeners.add(listener);
    }

    public interface PerfEndedListener{
        boolean perfEnded(Metric metric,String name);
    }

}
