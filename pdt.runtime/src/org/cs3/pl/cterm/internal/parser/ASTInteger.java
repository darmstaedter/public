/* Generated By:JJTree: Do not edit this line. ASTInteger.java */

package org.cs3.pl.cterm.internal.parser;

public class ASTInteger extends SimpleNode {
  public ASTInteger(int id) {
    super(id);
  }

  public ASTInteger(CanonicalTermParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CanonicalTermParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
