/* Generated By:JJTree&JavaCC: Do not edit this line. CanonicalTermParser.java */
package org.cs3.pl.cterm.internal.parser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CanonicalTermParser/*@bgen(jjtree)*/implements CanonicalTermParserTreeConstants, CanonicalTermParserConstants {/*@bgen(jjtree)*/
  protected JJTCanonicalTermParserState jjtree = new JJTCanonicalTermParserState();
        private List errors = new ArrayList();

        public List getErrors() {
                return errors;
        }

                void jjtreeOpenNodeScope(Node n)
    {
        Token t = getToken(1);
        //System.err.println("open node of type "+PrologTermParserTreeConstants.jjtNodeName[((SimpleNode)n).id]);
        //System.err.prinln("   first token: "+t.image+" at line "+t.beginLine+", column "+t.beginColumn+".");
      ((SimpleNode)n).setFirstToken(t);

    }

    void jjtreeCloseNodeScope(Node n)
    {
      Token t = getToken(0);
        //System.err.prinln("closing node of type "+n);
        //System.err.prinln("   last token: "+t.image+" at line "+t.beginLine+", column "+t.beginColumn+".");      
      ((SimpleNode)n).setLastToken(t);



                //need to "manualy" figure out the start token, as node scope hooks are
                //called "to late" for nodes like ASTInfixTerm 
                //if the nodes children have already been poped, this should work:
                SimpleNode s = (SimpleNode)n;
                if(s.jjtGetNumChildren()>0){
                         SimpleNode firstChild = (SimpleNode)s.jjtGetChild(0);
                         s.setFirstToken(firstChild.getFirstToken());
                }
                //System.err.println("created: "+ s+" : "+s.getImage());



          }


        public void error_skipto(int kind) {
          ParseException e = generateParseException();
          errors.add(e);
          Token t;
          do {
            t = getNextToken();
          } while (t.kind != kind && t.kind != EOF);
        }

public static void main(String args[]) throws FileNotFoundException {
        InputStream stream = null;
        if(args==null||args.length==0){
            //System.err.println("Reading from standard input...");
            stream=System.in;
        }
        else{
                return;
        }
    CanonicalTermParser t = new CanonicalTermParser(stream);
    try {
      t.Term();
      Node n = t.getASTRoot();

      System.err.println("Thank you.");
    } catch (Exception e) {
      System.err.println("Oops.");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
  public Node getASTRoot(){
                return  jjtree.rootNode();
        }

  final public void Term() throws ParseException {
    if (jj_2_1(2147483647)) {
      Compound();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ATOM_IDENTIFIER:
      case QUOTED_ATOM:
        Atom();
        break;
      case DECIMAL_LITERAL:
      case BIN_LITERAL:
      case OCT_LITERAL:
      case HEX_LITERAL:
      case FLOATING_POINT_LITERAL:
        Number();
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void Atom() throws ParseException {
             /*@bgen(jjtree) Atom */
  ASTAtom jjtn000 = new ASTAtom(this, JJTATOM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
  jjtreeOpenNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ATOM_IDENTIFIER:
        jj_consume_token(ATOM_IDENTIFIER);
        break;
      case QUOTED_ATOM:
        jj_consume_token(QUOTED_ATOM);
        break;
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
            jjtreeCloseNodeScope(jjtn000);
          }
    }
  }

  final public void Variable() throws ParseException {
                 /*@bgen(jjtree) Variable */
  ASTVariable jjtn000 = new ASTVariable(this, JJTVARIABLE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
  jjtreeOpenNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VARIABLE_IDENTIFIER:
        jj_consume_token(VARIABLE_IDENTIFIER);
        break;
      case QUOTED_ATOM:
        jj_consume_token(QUOTED_ATOM);
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
            jjtreeCloseNodeScope(jjtn000);
          }
    }
  }

  final public void Compound() throws ParseException {
                 /*@bgen(jjtree) Compound */
  ASTCompound jjtn000 = new ASTCompound(this, JJTCOMPOUND);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
  jjtreeOpenNodeScope(jjtn000);
    try {
      Atom();
      jj_consume_token(LPAREN);
      Term();
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_1;
        }
        jj_consume_token(COMMA);
        Term();
      }
      jj_consume_token(RPAREN);
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
            jjtreeCloseNodeScope(jjtn000);
          }
    }
  }

  final public void Number() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DECIMAL_LITERAL:
    case BIN_LITERAL:
    case OCT_LITERAL:
    case HEX_LITERAL:
      Integer();
      break;
    case FLOATING_POINT_LITERAL:
      Float();
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Integer() throws ParseException {
                  /*@bgen(jjtree) Integer */
  ASTInteger jjtn000 = new ASTInteger(this, JJTINTEGER);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
  jjtreeOpenNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DECIMAL_LITERAL:
        jj_consume_token(DECIMAL_LITERAL);
        break;
      case BIN_LITERAL:
        jj_consume_token(BIN_LITERAL);
        break;
      case OCT_LITERAL:
        jj_consume_token(OCT_LITERAL);
        break;
      case HEX_LITERAL:
        jj_consume_token(HEX_LITERAL);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
      jjtreeCloseNodeScope(jjtn000);
    }
    }
  }

  final public void Float() throws ParseException {
               /*@bgen(jjtree) Float */
  ASTFloat jjtn000 = new ASTFloat(this, JJTFLOAT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
  jjtreeOpenNodeScope(jjtn000);
    try {
      jj_consume_token(FLOATING_POINT_LITERAL);
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
            jjtreeCloseNodeScope(jjtn000);
          }
    }
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_3_1() {
    if (jj_3R_2()) return true;
    if (jj_scan_token(LPAREN)) return true;
    return false;
  }

  final private boolean jj_3R_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(11)) {
    jj_scanpos = xsp;
    if (jj_scan_token(23)) return true;
    }
    return false;
  }

  public CanonicalTermParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[6];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xbe0800,0x800800,0x801000,0x200,0x3e0000,0x1e0000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public CanonicalTermParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public CanonicalTermParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new CanonicalTermParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public CanonicalTermParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new CanonicalTermParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public CanonicalTermParser(CanonicalTermParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(CanonicalTermParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 6; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[24];
    for (int i = 0; i < 24; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 6; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 24; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
