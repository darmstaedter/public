/* Generated By:JJTree: Do not edit this line. ASTVariable.java */

package org.cs3.pl.cterm.internal.parser;

public class ASTVariable extends SimpleNode {
  public ASTVariable(int id) {
    super(id);
  }

  public ASTVariable(CanonicalTermParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CanonicalTermParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
