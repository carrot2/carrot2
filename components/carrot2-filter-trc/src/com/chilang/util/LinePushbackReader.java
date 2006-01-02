package com.chilang.util;

import java.io.*;

public class LinePushbackReader extends PushbackReader {

    public static int defaultLineBufferSize = 80;

    //remember last linebreak characters
    private StringBuffer linebreaks = new StringBuffer(0);

    //size of buffer for one line of text
    protected int lineBufferSize;

    /**
     * Construct a reader with capability to pushback one line of text.
     * Default line text buffer size is used (text amount of each line can't exceeds this).
     * @param in a chained reader
     */
    public LinePushbackReader(Reader in) {
        super(in, defaultLineBufferSize);
        lineBufferSize = defaultLineBufferSize;
    }

    /**
     * Construct a reader with capability to pushback one line of text.
     * @param in a chained reader
     * @param size size of buffer for one line of text
     * (file with lines that exceed this amount will cause error)
     */
    public LinePushbackReader(Reader in, int size) {
        super(in, size);
        lineBufferSize = size;
    }

    /**
     * Read a line of text. A line is considered to be terminated by
     * a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed
     * @return A String containing the contents of the line, not including
     *         any line-termination characters, or null if the end of the
     *         stream has been reached.
     * @see java.io.BufferedReader#readLine()
     *
     * @throws IOException
     */
    public String readLine() throws IOException {
        char[] line = new char[lineBufferSize];
        int count = super.read(line,0,lineBufferSize);
        if (count == -1)
            return null;

        linebreaks = new StringBuffer(2);
        char c;
        int i = 0;
        String content = null;
        for ( ;i<count; i++) {
            c = line[i];
            //case of CR
            if ((c == '\r')) {
                linebreaks.append(c);
                content = new String(line, 0, i);
                i++;
                //case of CRLF
                if (line[i] == '\n') {
                    linebreaks.append(line[i]);
                    i++;
                }
                break;
            }
            //case of LF
            if (c == '\n') {
                linebreaks.append(c);
                content = new String(line, 0, i);
                i++;
                break;
            }

        }
        if (i < count) {
            //line breaks inside the buffer
            //pushback contents of the "next" line
            super.unread(line, i, count-i);
        }
        return content;
    }

    /**
     * Pushback a line of text by copying it and termination character (previous line break)
     * to the front of the puhsback buffer.
     * appeding
     * @param line
     * @throws IOException
     */
    public void unreadLine(String line) throws IOException {
        super.unread((line+linebreaks.toString()).toCharArray());
        linebreaks = new StringBuffer(0);
    }

    public static void main(String argv[]) throws Exception {
        File file = new File("test.txt");
//        PrintWriter out = new PrintWriter(new FileOutputStream(file));
//        out.println("I 1");
//        out.println("B");
//        out.println("1234 456");
//        out.println("I 2");
//        out.println(".X");
//        out.println("1223 4455 xass 22");
//        out.flush();
//        out.close();
        LinePushbackReader in = new LinePushbackReader(new BufferedReader(new FileReader(file)), 80);
        String line = null;
        
        while ((line = in.readLine()) != null) {
            System.out.println(">"+line+"]");
            in.unreadLine(line);
            line = in.readLine();
            System.out.println("<"+line+"]");
        }
        in.close();
    }
}
