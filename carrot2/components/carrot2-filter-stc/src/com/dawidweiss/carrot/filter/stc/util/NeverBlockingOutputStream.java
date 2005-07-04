package com.dawidweiss.carrot.filter.stc.util;

import java.io.*;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 * This implementation of FilterOutputStream ensures that the calling thread
 * will never block on write methods. A background thread is created and data is
 * spooled in memory until the output stream can be accessed. The drawback of
 * this approach is that IOExceptions are deferred and may even never be thrown.
 * Also, the amount of memory may increase up to the size of buffered output.
 * The background thread also uses certain amount of processing power.
 */
public class NeverBlockingOutputStream extends OutputStream {
    private final static Logger log = Logger
            .getLogger(NeverBlockingOutputStream.class);

    private final byte[] buffer;

    private final int bufferSize;

    private LinkedList spool;

    private OutputStream realOutput;

    private Throwable delayedThrowable;

    private boolean closeOnExit = false;

    private boolean flushNow = false;

    private Thread writer = new Thread("NeverBlockingOutputStream writer") {
        public void run() {
            try {
                while (true) {
                    byte[] chunk;

                    synchronized (spool) {
                        if (writer != this && spool.size() == 0) {
                            if (closeOnExit) {
                                log.debug("closing...");
                                realOutput.close();
                                log.debug("closed...");
                            }
                            return;
                        }

                        if (spool.size() == 0) {
                            log.debug("Waiting for chunks...");
                            spool.wait();
                            continue;
                        }

                        chunk = (byte[]) spool.getFirst();
                    }

                    log.debug("Writing chunk...");
                    realOutput.write(chunk);
                    if (flushNow) {
                        flushNow = false;
                        log.debug("Flushing...");
                        realOutput.flush();
                    }
                    log.debug("writing finished.");

                    synchronized (spool) {
                        spool.removeFirst();
                        spool.notify(); // Notify any clients hanging on flush()
                                        // or close();
                    }
                }
            } catch (Throwable e) {
                log.debug("Exception thrown", e);
                synchronized (spool) {
                    delayedThrowable = e;
                    spool.notify();
                }
            } finally {
                log.debug("Finishing.");
                synchronized (this) {
                    notify();
                }
                log.debug("Finished.");
            }
        }
    };

    private int bufferPosition;

    public NeverBlockingOutputStream(OutputStream output) {
        bufferSize = 8000;
        buffer = new byte[bufferSize];
        bufferPosition = 0;
        spool = new LinkedList();
        realOutput = output;
        writer.start();
    }

    public void close() throws IOException {
        flush();

        synchronized (writer) {
            Thread writer = this.writer;

            synchronized (spool) {
                rethrowDelayedException();

                this.closeOnExit = true;
                this.writer = null;
                spool.notify();
            }

            // wait for the writer to finish.
            try {
                if (writer.isAlive())
                    writer.wait();
            } catch (InterruptedException e) {/* Ignore interrupted exception. */
            }
        }
    }

    /**
     * Flushing the neverblocking stream has no immediate effect (because it
     * could block!)
     */
    public void flush() throws IOException {
        flushBufferAsChunk();

        synchronized (spool) {
            rethrowDelayedException();
            flushNow = true;
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        flushBufferAsChunk();

        byte[] chunk = new byte[len];
        System.arraycopy(b, off, chunk, 0, len);

        synchronized (spool) {
            rethrowDelayedException();
            spool.addLast(chunk);
            spool.notify();
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        buffer[bufferPosition] = (byte) b;
        bufferPosition++;

        if (bufferPosition >= bufferSize) {
            flushBufferAsChunk();
        }
    }

    private final void flushBufferAsChunk() throws IOException {
        if (bufferPosition == 0) {
            return;
        }

        byte[] chunk = new byte[bufferPosition];
        System.arraycopy(buffer, 0, chunk, 0, bufferPosition);
        bufferPosition = 0;

        synchronized (spool) {
            rethrowDelayedException();
            spool.addLast(chunk);
            spool.notify();
        }
    }

    private void rethrowDelayedException() throws IOException {
        if (delayedThrowable != null) {
            throw new IOException("Exception from background writer: "
                    + delayedThrowable.toString());
        }
    }
}