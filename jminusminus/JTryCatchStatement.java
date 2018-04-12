// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an if-statement.
 */

class JTryCatchStatement extends JStatement {

    /** Then clause. */
    private JBlock tryPart;
    
    /** exception clause. */
    private JFormalParameter exception;

    /** Else clause. */
    private JBlock catchPart;
    
    /** Else clause. */
    private JBlock finallyPart;

    /**
     * Construct an AST node for an try-statement given its line number, the test
     * expression, the consequent, and the alternate.
     * 
     * @param line
     *            line in which the try-statement occurs in the source file.
     * @param condition
     *            try expression.
     * @param catchPart
     *            catch clause.
     *           
     */

    public JTryCatchStatement(int line, JBlock tryPart, JFormalParameter exception, JBlock catchPart, JBlock finallyPart) {
        super(line);
        this.tryPart = tryPart;
        this.exception= exception;
        this.catchPart = catchPart;
        this.finallyPart = finallyPart;
        
    }

    /**
     * Analyzing the try-statement means analyzing its components and checking
     * that the test is boolean.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JStatement analyze(Context context) {
        
        return this;
    }

    /**
     * Code generation for an try -statement. We generate code to branch over the
     * consequent if !test; the consequent is followed by an unconditonal branch
     * over (any) alternate.
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
        p.printf("<JTryStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<TryBlock>\n");
        p.indentRight();
        tryPart.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TryBlock>\n");
        p.println("<FormalParameter>");
        p.indentRight();
        exception.writeToStdOut(p);
        p.indentLeft();
        p.printf("</FormalParameter>\n");
        p.printf("<CatchBlock>\n");
        p.indentRight();
        catchPart.writeToStdOut(p);
        p.indentLeft();
        p.printf("</CatchBlock>\n");
        if (finallyPart != null) {
            p.printf("<FinallyBlock>\n");
            p.indentRight();
            finallyPart.writeToStdOut(p);
            p.indentLeft();
            p.printf("</FinallyBlock>\n");
        }
        p.indentLeft();
        p.printf("</JTryStatement>\n");
    }

}
