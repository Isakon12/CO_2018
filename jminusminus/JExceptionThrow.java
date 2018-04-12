// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
class JExceptionThrow extends JStatement {

    /** Arguments. */
    private ArrayList<JExpression> args;

    /** Type name */
    private Type name;

    /**
     * Construct an AST node for a throw exception given the line number,
     * parameters, and name.
     * 
     * @param line
     *            line in which the variable declaration occurs in the source
     *            file.
     * @param mods
     *            modifiers.
     * @param decls
     *            variable declarators.
     */

    public JExceptionThrow(int line, Type name,
    		ArrayList<JExpression> args) {
        super(line);
        this.name = name;
        this.args = args;
    }

    /**
     * We declare the variable(s). Initializations are rewritten as assignment
     * statements.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JStatement analyze(Context context) {
        return this;
    }

    /**
     * Local variable initializations (rewritten as assignments in analyze())
     * are generated here.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {

    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JExceptionThrow line=\"%d\" exception=\"%s\">\n",line(),
        		name.toString());
        p.indentRight();
        if (args != null) {
            p.println("<Arguments>");
            p.indentRight();
            for (JExpression expr : args) {
                expr.writeToStdOut(p);
            }
            p.indentLeft();
            p.println("</Arguments>");
        }
        p.indentLeft();
        p.println("</JExceptionThrow>");
    }

}
