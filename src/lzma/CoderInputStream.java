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

import java.io.IOException;
import java.io.InputStream;

public class CoderInputStream
        extends InputStream
{
    private final CoderThread ct;

    private volatile InputStream in;

    protected CoderInputStream(final InputStream in, final Coder coder)
            throws IOException
    {
        this.ct = new CoderThread(coder, in);

        this.in = ct.getInputStreamSink();

        this.ct.start();
    }

    public int read()
            throws IOException
    {
        return in.read();
    }

    public int read(byte b[])
            throws IOException
    {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len)
            throws IOException
    {
        return in.read(b, off, len);
    }

    public long skip(long n)
            throws IOException
    {
        return in.skip(n);
    }

    public int available()
            throws IOException
    {
        return in.available();
    }

    public void close()
            throws IOException
    {
        in.close();

        try
        {
            ct.join();
        }
        catch (InterruptedException e)
        {
            throw new IOException(e.toString());
        }

        ct.checkForException();
    }

    public synchronized void mark(int readlimit)
    {
        in.mark(readlimit);
    }

    public synchronized void reset()
            throws IOException
    {
        in.reset();
    }

    public boolean markSupported()
    {
        return in.markSupported();
    }
}