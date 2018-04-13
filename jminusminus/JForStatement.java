// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */

class JForStatement extends JStatement {

	private JExpression init_expr;
	
	/** Variable **/
	private JVariableDeclarator init;
	
    /** Test expression. */
    private JExpression condition;

    /** Final expression. */
    private JExpression incr;
    
    /** The body. */
    private JStatement body;

    /**
     * Construct an AST node for a for-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the for-statement occurs in the source file.
     * @param condition
     *            test expression.
     * @param body
     *            the body.
     */

    public JForStatement(int line, JVariableDeclarator init, JExpression init_expr,
    		JExpression condition, JExpression incr, JStatement body) {
        super(line);
        this.init = init;
        this.init_expr = init_expr;
        this.condition = condition;
        this.incr = incr;
        this.body = body;
    }

    /**
     * Analysis involves analyzing the test, checking its type and analyzing the
     * body statement.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JForStatement analyze(Context context) {
    	return this;
    }

    /**
     * Generate code for the for loop.
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
        p.printf("<JForStatement line=\"%d\">\n", line());
        p.indentRight();
        if(init != null)  {
            p.printf("<InitVariable>\n");
            p.indentRight();
            init.writeToStdOut(p);
            p.indentLeft();
            p.printf("</InitVariable>\n");
        }
        if(init_expr != null)  {
            p.printf("<InitExpression>\n");
            p.indentRight();
            init_expr.writeToStdOut(p);
            p.indentLeft();
            p.printf("</InitExpression>\n");
        }
        p.printf("<TestExpression>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TestExpression>\n");
        p.printf("<IncrementExpression>\n");
        p.indentRight();
        incr.writeToStdOut(p);
        p.indentLeft();
        p.printf("</IncrementExpression>\n");
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForeStatement>\n");
    }

}
