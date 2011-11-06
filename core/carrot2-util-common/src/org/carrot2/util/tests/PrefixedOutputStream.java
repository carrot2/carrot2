package org.carrot2.util.tests;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.mutable.MutableInt;

class PrefixedOutputStream extends LineOrientedOutputStream
{
    private final OutputStream sink;
    private final MutableInt mutex;
    private final byte [] prefix;

    public PrefixedOutputStream(byte [] prefix, MutableInt mutex, OutputStream sink)
    {
        this.prefix = prefix;
        this.sink = sink;
        this.mutex = mutex;
    }

    @Override
    protected void processLine(byte [] line) throws IOException
    {
        synchronized (mutex)
        {
            if (mutex.intValue() == 0)
            {
                sink.write("... (output follows)\n".getBytes());
            }
            mutex.increment();

            sink.write(prefix);
            sink.write(line);
            sink.write('\n');
        }
    }
}
