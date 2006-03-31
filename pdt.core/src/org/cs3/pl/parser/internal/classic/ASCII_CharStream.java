/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * Author: Lukas Degener (among others) 
 * E-mail: degenerl@cs.uni-bonn.de
 * WWW: http://roots.iai.uni-bonn.de/research/pdt 
 * Copyright (C): 2004-2006, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * In addition, you may at your option use, modify and redistribute any
 * part of this program under the terms of the GNU Lesser General Public
 * License (LGPL), version 2.1 or, at your option, any later version of the
 * same license, as long as
 * 
 * 1) The program part in question does not depend, either directly or
 *   indirectly, on parts of the Eclipse framework and
 *   
 * 2) the program part in question does not include files that contain or
 *   are derived from third-party work and are therefor covered by special
 *   license agreements.
 *   
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *   
 * ad 1: A program part is said to "depend, either directly or indirectly,
 *   on parts of the Eclipse framework", if it cannot be compiled or cannot
 *   be run without the help or presence of some part of the Eclipse
 *   framework. All java classes in packages containing the "pdt" package
 *   fragment in their name fall into this category.
 *   
 * ad 2: "Third-party code" means any code that was originaly written as
 *   part of a project other than the PDT. Files that contain or are based on
 *   such code contain a notice telling you so, and telling you the
 *   particular conditions under which they may be used, modified and/or
 *   distributed.
 ****************************************************************************/

package org.cs3.pl.parser.internal.classic;

/**
 * An implementation of interface CharStream, where the stream is assumed to
 * contain only ASCII characters (without unicode processing).
 */

public final class ASCII_CharStream
{
  int beginOffset;
  int endOffset;
  int bufsize;
  int available;
  int tokenBegin;
  public int bufpos = -1;
  private int bufline[];
  private int bufcolumn[];

  private int column = 0;
  private int line = 1;

  private boolean prevCharIsCR = false;
  private boolean prevCharIsLF = false;

  private java.io.InputStream inputStream;

  private byte[] buffer;
  private int maxNextCharInd = 0;

  private final void ExpandBuff(boolean wrapAround)
  {
     byte[] newbuffer = new byte[bufsize + 2048];
     int newbufline[] = new int[bufsize + 2048];
     int newbufcolumn[] = new int[bufsize + 2048];

     try
     {
        if (wrapAround)
        {
           System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
           System.arraycopy(buffer, 0, newbuffer, bufsize - tokenBegin, bufpos);
           buffer = newbuffer;

           System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
           System.arraycopy(bufline, 0, newbufline, bufsize - tokenBegin, bufpos);

           System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
           System.arraycopy(bufcolumn, 0, newbufcolumn, bufsize - tokenBegin, bufpos);
           bufcolumn = newbufcolumn;

           maxNextCharInd = (bufpos += (bufsize - tokenBegin));
        }
        else
        {
           System.arraycopy(buffer, tokenBegin, newbuffer, 0, bufsize - tokenBegin);
           buffer = newbuffer;

           System.arraycopy(bufline, tokenBegin, newbufline, 0, bufsize - tokenBegin);
           bufline = newbufline;

           System.arraycopy(bufcolumn, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin);
           bufcolumn = newbufcolumn;

           maxNextCharInd = (bufpos -= tokenBegin);
        }
     }
     catch (Throwable t)
     {
        System.out.println("Error : " + t.getClass().getName());
        throw new Error();
     }


     bufsize += 2048;
     available = bufsize;
     tokenBegin = 0;
  }

  private final void FillBuff() throws java.io.IOException
  {
     if (maxNextCharInd == available)
     {
        if (available == bufsize)
        {
           if (tokenBegin > 2048)
           {
              bufpos = maxNextCharInd = 0;
              available = tokenBegin;
           }
           else if (tokenBegin < 0)
              bufpos = maxNextCharInd = 0;
           else
              ExpandBuff(false);
        }
        else if (available > tokenBegin)
           available = bufsize;
        else if ((tokenBegin - available) < 2048)
           ExpandBuff(true);
        else
           available = tokenBegin;
     }

     int i;
     if ((i = inputStream.read(buffer, maxNextCharInd,
                                    available - maxNextCharInd)) == -1)
     {
        --bufpos;
        backup(0);
        if (tokenBegin == -1)
           tokenBegin = bufpos;
        throw new java.io.IOException();
     }
     else
        maxNextCharInd += i;
  }

  public char BeginToken() throws java.io.IOException
  {
     beginOffset = endOffset;
     tokenBegin = -1;
     char c = readChar();
     tokenBegin = bufpos;

     return c;
  }

  private final void UpdateLineColumn(char c)
  {
     column++;

     if (prevCharIsLF)
     {
        prevCharIsLF = false;
        line += (column = 1);
     }
     else if (prevCharIsCR)
     {
        prevCharIsCR = false;
        if (c == '\n')
        {
           prevCharIsLF = true;
        }
        else
           line += (column = 1);
     }

     switch (c)
     {
        case '\r' :
           prevCharIsCR = true;
           break;
        case '\n' :
           prevCharIsLF = true;
           break;
        case '\t' :
           column += (9 - (column & 07));
           break;
        default :
           break;
     }

     bufline[bufpos] = line;
     bufcolumn[bufpos] = column;
  }

  private int inBuf = 0;
  public final char readChar() throws java.io.IOException
  {
     endOffset++;
     if (inBuf > 0)
     {
        --inBuf;
        return (char)((char)0xff & buffer[(bufpos == bufsize - 1) ? (bufpos = 0) : ++bufpos]);
     }

     if (++bufpos >= maxNextCharInd)
        FillBuff();

     char c = (char)((char)0xff & buffer[bufpos]);

     UpdateLineColumn(c);
     return (c);
  }

  /**
   * @deprecated 
   * @see #getEndColumn
   */

  public final int getColumn() {
      return bufcolumn[bufpos];
  }

  /**
   * @deprecated 
   * @see #getEndLine
   */

  public final int getLine() {
      return bufline[bufpos];
  }

  public final int getEndColumn() {
      return bufcolumn[bufpos];
  }

  public final int getEndLine() {
      return bufline[bufpos];
  }

  public final int getBeginColumn() {
      return bufcolumn[tokenBegin];
  }

  public final int getBeginLine() {
      return bufline[tokenBegin];
  }

  public final void backup(int amount) {

    endOffset -= amount;
    inBuf += amount;
    if ((bufpos -= amount) < 0)
       bufpos += bufsize;
  }

  public ASCII_CharStream(java.io.InputStream dstream, int startline,
  int startcolumn, int buffersize)
  {
    inputStream = dstream;
    line = startline;
    column = startcolumn - 1;

    available = bufsize = buffersize;
    buffer = new byte[buffersize];
    bufline = new int[buffersize];
    bufcolumn = new int[buffersize];
  }

  public ASCII_CharStream(java.io.InputStream dstream, int startline,
                                                           int startcolumn)
  {
    inputStream = dstream;
    line = startline;
    column = startcolumn - 1;

    available = bufsize = 4096;
    buffer = new byte[4096];
    bufline = new int[4096];
    bufcolumn = new int[4096];
  }

  public final String GetImage()
  {
     if (bufpos >= tokenBegin)
        return new String(buffer, 0, tokenBegin, bufpos - tokenBegin + 1);
     else
        return new String(buffer, 0, tokenBegin, bufsize - tokenBegin) +
                              new String(buffer, 0, 0, bufpos + 1);
  }

  public final byte[] GetSuffix(int len)
  {
     byte[] ret = new byte[len];

     if ((bufpos + 1) >= len)
        System.arraycopy(buffer, bufpos - len + 1, ret, 0, len);
     else
     {
        System.arraycopy(buffer, bufsize - (len - bufpos - 1), ret, 0,
                                                          len - bufpos - 1);
        System.arraycopy(buffer, 0, ret, len - bufpos, bufpos + 1);
     }

     return ret;
  }

  public void Done()
  {
     buffer = null;
     bufline = null;
     bufcolumn = null;
  }

  /**
   * Returns the byte offset for the beginning of the current token.
   */
  public final int getBeginOffset()
  {
     return beginOffset;
  }

  /**
   * Returns the byte offset for the end of the current token.
   */
  public final int getEndOffset()
  {
     return endOffset;
  }

}
