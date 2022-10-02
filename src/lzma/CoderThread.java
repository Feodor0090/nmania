/*
 *  Copyright (c) 2011 Tamas Cservenak. All rights reserved.
 *
 *  <tamas@cservenak.com>
 *  http://www.cservenak.com/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package lzma;

import java.io.*;

public class CoderThread
        extends Thread
{
    private final PipedInputStream inSink;

    private final PipedOutputStream outSink;

    private final Runnable workhorse;

    private Throwable throwable;

    public CoderThread(final Coder coder, final InputStream in)
            throws IOException
    {
        this.inSink = new PipedInputStream();
        this.outSink = new PipedOutputStream(inSink);
        this.workhorse = new Runnable()
        {
            public void run()
            {
                try
                {
                    coder.code(in, outSink);

                    try {
                    	outSink.flush();
                    } catch (IOException e) {
                    }
                }
                catch (Throwable e)
                {
                    throwable = e;
                }
                finally
                {
                    try {
                    	outSink.close();
                    } catch (IOException e) {
                    }
                }
            }
        };
    }

    public CoderThread(final Coder coder, final OutputStream out)
            throws IOException
    {
        this.outSink = new PipedOutputStream();
        this.inSink = new PipedInputStream(outSink);
        this.workhorse = new Runnable()
        {
            public void run()
            {
                try
                {
                    coder.code(inSink, out);

                    try {
                    	out.flush();
                    } catch (IOException e) {
                    }
                }
                catch (Throwable e)
                {
                    throwable = e;
                }
                finally
                {
                    try {
                    	inSink.close();
                    } catch (IOException e) {
                    }
                }
            }
        };
    }

    public void run()
    {
        workhorse.run();
    }

    // ==

    public Throwable getThrowable()
    {
        return throwable;
    }

    public void checkForException()
            throws IOException
    {
        if (null != throwable)
        {
            if (throwable instanceof IOException)
            {
                throw (IOException) throwable;
            }
            else
            {
                throw new IOException(throwable.toString());
            }
        }
    }

    public PipedInputStream getInputStreamSink()
    {
        return inSink;
    }

    public PipedOutputStream getOutputStreamSink()
    {
        return outSink;
    }
}