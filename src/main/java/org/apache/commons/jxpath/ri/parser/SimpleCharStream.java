/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Generated By:JavaCC: Do not edit this line. SimpleCharStream.java Version 3.0 */

package org.apache.commons.jxpath.ri.parser;

/**
 * An implementation of interface CharStream, where the stream is assumed to contain only ASCII characters (without unicode processing).
 */
public class SimpleCharStream {

    public static final boolean staticFlag = false;
    int bufsize;
    int available;
    int tokenBegin;
    public int bufpos = -1;
    protected int bufline[];
    protected int bufcolumn[];
    protected int column = 0;
    protected int line = 1;
    protected boolean prevCharIsCR = false;
    protected boolean prevCharIsLF = false;
    protected java.io.Reader inputStream;
    protected char[] buffer;
    protected int maxNextCharInd = 0;
    protected int inBuf = 0;

    public SimpleCharStream(final java.io.InputStream dstream) {
        this(dstream, 1, 1, 4096);
    }

    public SimpleCharStream(final java.io.InputStream dstream, final int startLine, final int startColumn) {
        this(dstream, startLine, startColumn, 4096);
    }

    public SimpleCharStream(final java.io.InputStream dstream, final int startLine, final int startColumn, final int buffersize) {
        this(new java.io.InputStreamReader(dstream), startLine, startColumn, 4096);
    }

    public SimpleCharStream(final java.io.Reader dstream) {
        this(dstream, 1, 1, 4096);
    }

    public SimpleCharStream(final java.io.Reader dstream, final int startLine, final int startColumn) {
        this(dstream, startLine, startColumn, 4096);
    }

    public SimpleCharStream(final java.io.Reader dstream, final int startLine, final int startColumn, final int bufferSize) {
        inputStream = dstream;
        line = startLine;
        column = startColumn - 1;
        available = bufsize = bufferSize;
        buffer = new char[bufferSize];
        bufline = new int[bufferSize];
        bufcolumn = new int[bufferSize];
    }

    /**
     * Method to adjust line and column numbers for the start of a token.<BR>
     *
     * @param newLine TODO
     * @param newCol  TODO
     */
    public void adjustBeginLineColumn(int newLine, final int newCol) {
        int start = tokenBegin;
        int len;
        if (bufpos >= tokenBegin) {
            len = bufpos - tokenBegin + inBuf + 1;
        } else {
            len = bufsize - tokenBegin + bufpos + 1 + inBuf;
        }
        int i = 0, j = 0, k = 0;
        int nextColDiff = 0, columnDiff = 0;
        while (i < len && bufline[j = start % bufsize] == bufline[k = ++start % bufsize]) {
            bufline[j] = newLine;
            nextColDiff = columnDiff + bufcolumn[k] - bufcolumn[j];
            bufcolumn[j] = newCol + columnDiff;
            columnDiff = nextColDiff;
            i++;
        }
        if (i < len) {
            bufline[j] = newLine++;
            bufcolumn[j] = newCol + columnDiff;
            while (i++ < len) {
                if (bufline[j = start % bufsize] != bufline[++start % bufsize]) {
                    bufline[j] = newLine++;
                } else {
                    bufline[j] = newLine;
                }
            }
        }
        line = bufline[j];
        column = bufcolumn[j];
    }

    public void backup(final int amount) {
        inBuf += amount;
        if ((bufpos -= amount) < 0) {
            bufpos += bufsize;
        }
    }

    public char BeginToken() throws java.io.IOException {
        tokenBegin = -1;
        final char c = readChar();
        tokenBegin = bufpos;
        return c;
    }

    public void Done() {
        buffer = null;
        bufline = null;
        bufcolumn = null;
    }

    protected void ExpandBuff(final boolean wrapAround) {
        final char[] newbuffer = new char[bufsize + 2048];
        final int newbufline[] = new int[bufsize + 2048];
        final int newbufcolumn[] = new int[bufsize + 2048];
        try {
            if (wrapAround) {
                System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
                System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
                buffer = newbuffer;
                System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
                System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);
                bufline = newbufline;
                System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
                System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
                bufcolumn = newbufcolumn;
                maxNextCharInd = bufpos += bufsize - tokenBegin;
            } else {
                System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
                buffer = newbuffer;
                System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
                bufline = newbufline;
                System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
                bufcolumn = newbufcolumn;
                maxNextCharInd = bufpos -= tokenBegin;
            }
        } catch (final Throwable t) {
            throw new Error(t.getMessage());
        }
        bufsize += 2048;
        available = bufsize;
        tokenBegin = 0;
    }

    protected void FillBuff() throws java.io.IOException {
        if (maxNextCharInd == available) {
            if (available == bufsize) {
                if (tokenBegin > 2048) {
                    bufpos = maxNextCharInd = 0;
                    available = tokenBegin;
                } else if (tokenBegin < 0) {
                    bufpos = maxNextCharInd = 0;
                } else {
                    ExpandBuff(false);
                }
            } else if (available > tokenBegin) {
                available = bufsize;
            } else if (tokenBegin - available < 2048) {
                ExpandBuff(true);
            } else {
                available = tokenBegin;
            }
        }
        int i;
        try {
            if ((i = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1) {
                inputStream.close();
                throw new java.io.IOException();
            }
            maxNextCharInd += i;
        } catch (final java.io.IOException e) {
            --bufpos;
            backup(0);
            if (tokenBegin == -1) {
                tokenBegin = bufpos;
            }
            throw e;
        }
    }

    public int getBeginColumn() {
        return bufcolumn[tokenBegin];
    }

    public int getBeginLine() {
        return bufline[tokenBegin];
    }

    /**
     * @deprecated
     * @return the end column.
     * @see #getEndColumn
     */
    @Deprecated
    public int getColumn() {
        return bufcolumn[bufpos];
    }

    public int getEndColumn() {
        return bufcolumn[bufpos];
    }

    public int getEndLine() {
        return bufline[bufpos];
    }

    public String GetImage() {
        if (bufpos >= tokenBegin) {
            return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
        }
        return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
    }

    /**
     * @deprecated
     * @return the line number.
     * @see #getEndLine
     */
    @Deprecated
    public int getLine() {
        return bufline[bufpos];
    }

    public char[] GetSuffix(final int len) {
        final char[] ret = new char[len];
        if (bufpos + 1 >= len) {
            System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
        } else {
            System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0, len - bufpos - 1);
            System.arraycopy(buffer, 0, ret, len - bufpos - 1, bufpos + 1);
        }
        return ret;
    }

    public char readChar() throws java.io.IOException {
        if (inBuf > 0) {
            --inBuf;
            if (++bufpos == bufsize) {
                bufpos = 0;
            }
            return buffer[bufpos];
        }
        if (++bufpos >= maxNextCharInd) {
            FillBuff();
        }
        final char c = buffer[bufpos];
        UpdateLineColumn(c);
        return c;
    }

    public void ReInit(final java.io.InputStream dstream) {
        ReInit(dstream, 1, 1, 4096);
    }

    public void ReInit(final java.io.InputStream dstream, final int startLine, final int startColumn) {
        ReInit(dstream, startLine, startColumn, 4096);
    }

    public void ReInit(final java.io.InputStream dstream, final int startLine, final int startColumn, final int buffersize) {
        ReInit(new java.io.InputStreamReader(dstream), startLine, startColumn, 4096);
    }

    public void ReInit(final java.io.Reader dstream) {
        ReInit(dstream, 1, 1, 4096);
    }

    public void ReInit(final java.io.Reader dstream, final int startLine, final int startColumn) {
        ReInit(dstream, startLine, startColumn, 4096);
    }

    public void ReInit(final java.io.Reader dstream, final int startLine, final int startColumn, final int bufferSize) {
        inputStream = dstream;
        line = startLine;
        column = startColumn - 1;
        if (buffer == null || bufferSize != buffer.length) {
            available = bufsize = bufferSize;
            buffer = new char[bufferSize];
            bufline = new int[bufferSize];
            bufcolumn = new int[bufferSize];
        }
        prevCharIsLF = prevCharIsCR = false;
        tokenBegin = inBuf = maxNextCharInd = 0;
        bufpos = -1;
    }

    protected void UpdateLineColumn(final char c) {
        column++;
        if (prevCharIsLF) {
            prevCharIsLF = false;
            line += column = 1;
        } else if (prevCharIsCR) {
            prevCharIsCR = false;
            if (c == '\n') {
                prevCharIsLF = true;
            } else {
                line += column = 1;
            }
        }
        switch (c) {
        case '\r':
            prevCharIsCR = true;
            break;
        case '\n':
            prevCharIsLF = true;
            break;
        case '\t':
            column--;
            column += 8 - (column & 07);
            break;
        default:
            break;
        }
        bufline[bufpos] = line;
        bufcolumn[bufpos] = column;
    }
}
