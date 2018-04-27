// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

import java.util.ArrayList;
import java.util.HashSet;

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
    
    /** Exception captured offset for the catch blocks */
    private int offset;
    
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
     * Return the inner throwed exceptions
     * 
     */
    public HashSet<Type> throwedExceptions() {
    	HashSet<Type> tmp = new HashSet<Type>();
        for (JBlock catchBlocks : catchPart) {
            HashSet<Type> innerExceptions = catchBlocks.throwedExceptions();
            tmp.addAll(innerExceptions);
        }
        if(finallyPart != null)
        	tmp.addAll(finallyPart.throwedExceptions());
        return tmp;
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
        offset = this.context.get(0).offset() - 1;
        
        if(finallyPart != null)
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

        String startLabel = output.createLabel();
        String endLabel = output.createLabel();
        String finallyLabel = output.createLabel();
        String outLabel = output.createLabel();
    	
        output.addLabel(startLabel);
        
    	tryPart.codegen(output);
    	
        output.addLabel(endLabel);
        
        if(finallyPart != null)
        	finallyPart.codegen(output);
        
        output.addBranchInstruction(GOTO, outLabel);
    	
    	for (int i = 0; i < exception.size(); i++) {
    		//Catch
    		String handlerLabel = output.createLabel();
    		String endHandlerLabel = output.createLabel();
    		
    		output.addExceptionHandler(startLabel,endLabel,handlerLabel,
    				exception.get(i).type().jvmName());
    		
            output.addLabel(handlerLabel);
            
            switch (offset) {
            case 0:
                output.addNoArgInstruction(ASTORE_0);
                break;
            case 1:
                output.addNoArgInstruction(ASTORE_1);
                break;
            case 2:
                output.addNoArgInstruction(ASTORE_2);
                break;
            case 3:
                output.addNoArgInstruction(ASTORE_3);
                break;
            default:
                output.addOneArgInstruction(ASTORE, offset);
                break;
            }
            
            catchPart.get(i).codegen(output);      
            if(finallyPart != null) {
            	output.addLabel(endHandlerLabel);
            	output.addExceptionHandler(handlerLabel,endHandlerLabel,
            			finallyLabel,null);
            }
            
            if(finallyPart != null)
            	finallyPart.codegen(output);
            
            output.addBranchInstruction(GOTO, outLabel);
        }
    	
    	if(finallyPart != null) {
        	output.addLabel(finallyLabel);    		
    		
    		finallyPart.codegen(output);
        	output.addNoArgInstruction(ATHROW);
        	output.addExceptionHandler(startLabel,endLabel,finallyLabel,null);
    	}
    	
    	output.addLabel(outLabel);
    	
        
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
            if(context.size() != 0)
            	this.context.get(i).writeToStdOut(p);
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
