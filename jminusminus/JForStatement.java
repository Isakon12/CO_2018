// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

import java.util.HashSet;
import java.util.ArrayList;

/**
 * The AST node for a for-statement.
 */

class JForStatement extends JStatement {

	private JExpression init_expr;
	
	/** Variable **/
	private JVariableDeclaration init;
	
    /** Test expression. */
    private JExpression condition;

    /** Final expression. */
    private JStatement incr;
    
    /** The body. */
    private JStatement body;
    
    /**
     * The new context (built in analyze()) represented by this block.
     */
    private LocalContext context;

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

    public JForStatement(int line, JVariableDeclaration init, 
    		JExpression init_expr, JExpression condition, JStatement incr, 
    		JStatement body) {
        super(line);
        this.init = init;
        this.init_expr = init_expr;
        this.condition = condition;
        this.incr = incr;
        this.body = body;
    }
    
    /**
     * 
     * @return throwed exceptions in the body of the loop
     */
    
    public HashSet<Type> throwedExceptions() {
        return body.throwedExceptions();
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
		this.context = new LocalContext(context);
    	if(init != null) init = (JVariableDeclaration) init.analyze(this.context);
    	if(init_expr != null) init_expr = (JExpression) init_expr.analyze(this.context);
    	condition = condition.analyze(this.context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        incr = (JStatement) incr.analyze(this.context);
        body = (JStatement) body.analyze(this.context);
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
        // Need two labels
        String test = output.createLabel();
        String out = output.createLabel();

        //Initialization of the variables
        if(init != null) init.codegen(output);
        
        //Initial expression
        if(init_expr != null) init_expr.codegen(output);
        
        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        condition.codegen(output, out, false);

        // Codegen body
        body.codegen(output);
        
        //Codegen increment expression
        incr.codegen(output);

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForStatement line=\"%d\">\n", line());
        p.indentRight();
        if (context != null) {
            p.indentRight();
            context.writeToStdOut(p);
            p.indentLeft();
        }
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
        p.printf("</JForStatement>\n");
    }

}
