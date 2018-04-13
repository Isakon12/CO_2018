// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

import java.util.ArrayList;

/**
 * The AST node for an if-statement.
 */

class JTryCatchStatement extends JStatement {

    /** Then clause. */
    private JBlock tryPart;
    
    /** exception clause. */
    private ArrayList<JFormalParameter> exception;

    /** Else clause. */
    private ArrayList<JBlock> catchPart;
    
    /** Else clause. */
    private JBlock finallyPart;
    
    /**
     * The new context (built in analyze()) represented by this block.
     */
    private ArrayList<LocalContext> context;


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

    public JTryCatchStatement(int line, JBlock tryPart, ArrayList<JFormalParameter> exception,
            ArrayList<JBlock> catchPart, JBlock finallyPart) {
        super(line);
        this.tryPart = tryPart;
        this.exception= exception;
        this.catchPart = catchPart;
        this.finallyPart = finallyPart;
        this.context = new ArrayList<LocalContext>();
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
        tryPart = tryPart.analyze(context);
        
        ArrayList<Type> typeTmp = new ArrayList<Type>();
        // Resolve types of the formal parameters
        for (JFormalParameter param : exception) {
            param.setType(param.type().resolve(context));  
            for(Type t : typeTmp) {
            	param.type().mustNotMatchExpected(line,t);
            }
            typeTmp.add(param.type());
        }    
        
        ArrayList<JBlock> catchPartTmp = new ArrayList<JBlock>();
        ArrayList<JFormalParameter> exceptionTmp = new ArrayList<JFormalParameter>();
        for (int i = 0; i < exception.size(); i++) {
    		this.context.add(new LocalContext(context));
    		LocalVariableDefn defn = new LocalVariableDefn(exception.get(i).type(), 
                    this.context.get(i).nextOffset());
    		defn.initialize();
            this.context.get(i).addEntry(exception.get(i).line(), exception.get(i).name(), defn);
            catchPartTmp.add(catchPart.get(i).analyze(this.context.get(i)));
            
        }
        catchPart = catchPartTmp;
        finallyPart = finallyPart.analyze(context);
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
        for(int i = 0; i < exception.size(); i++) {
            p.printf("<CatchBlock>\n");
            p.indentRight();      
            context.get(i).writeToStdOut(p);
        	p.println("<FormalParameter>");
            p.indentRight();
            exception.get(i).writeToStdOut(p);
            p.indentLeft();
            p.printf("</FormalParameter>\n");
            p.indentLeft();
            p.indentRight();
            catchPart.get(i).writeToStdOut(p);
            p.indentLeft();
            p.printf("</CatchBlock>\n");
        }
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
