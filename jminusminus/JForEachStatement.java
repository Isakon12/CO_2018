// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

import java.util.HashSet;

/**
 * The AST node for a for-each-statement.
 */

class JForEachStatement extends JStatement {

	/** Variable **/
	private JVariableDeclaration init;
	
    /** Final expression. */
    private JVariable identifier;
    
    /** The body. */
    private JStatement body;
    
    /**
     * The new context (built in analyze()) represented by this block.
     */
    private LocalContext context;

    /**
     * Construct an AST node for a for-each-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the for-statement occurs in the source file.
     * @param condition
     *            test expression.
     * @param body
     *            the body.
     */

    public JForEachStatement(int line, JVariableDeclaration init, 
    		JVariable identifier, JStatement body) {
        super(line);
        this.init = init;
        this.identifier = identifier;
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

    public JForEachStatement analyze(Context context) {
    	
    	this.context = new LocalContext(context);
    	
    	init = (JVariableDeclaration) init.analyze(this.context);
    	identifier = (JVariable) identifier.analyze(this.context);
        init.getVar().type().mustMatchExpected(line(), identifier.type().componentType());
    	return this;
    }

    /**
     * Generate code for the for each loop.
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
        p.printf("<JForEachStatement line=\"%d\">\n", line());
        p.indentRight();
        if (context != null) {
            p.indentRight();
            context.writeToStdOut(p);
            p.indentLeft();
        }
        p.printf("<InitVariable>\n");
        p.indentRight();
        init.writeToStdOut(p);
        p.indentLeft();
        p.printf("</InitVariable>\n");
        p.printf("<Variable>\n");
        p.indentRight();
        identifier.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Variable>\n");
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForEachStatement>\n");
    }

}
