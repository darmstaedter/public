/* Generated By:JJTree: Do not edit this line. .\CanonicalTermParserVisitor.java */

package org.cs3.pl.cterm.internal.parser;

public interface CanonicalTermParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTAtom node, Object data);
  public Object visit(ASTVariable node, Object data);
  public Object visit(ASTCompound node, Object data);
  public Object visit(ASTInteger node, Object data);
  public Object visit(ASTFloat node, Object data);
}
