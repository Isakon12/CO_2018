// Copyright 2011 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * The AST node for a method declaration.
 */

class JBlockDeclaration extends JAST implements JMember {

    /** Method modifiers. */
    protected ArrayList<String> mods;

    /** Method body. */
    protected JBlock body;

    /** Built in analyze(). */
    protected MethodContext context;

    /** Is method static. */
    protected boolean isStatic;
    
    /** Is method private. */
    protected boolean isPrivate;
    
    /** Is method public. */
    protected boolean isPublic;
    
    /** Is method protected. */
    protected boolean isProtected;
    
    /** Is method abstrac. */
    protected boolean isAbstract;


    /**
     * Construct an AST node for a block declaration given the
     * line number, and the method body.
     * 
     * @param line
     *                line in which the block declaration occurs
     *                in the source file.
     * @param mods
     *                modifiers.
     * @param body
     *                method body.
     */

    public JBlockDeclaration(int line, ArrayList<String> mods, JBlock body)

    {
        super(line);
        this.mods = mods;
        this.body = body;
        this.isStatic = mods.contains("static");
        this.isPublic = mods.contains("public");
        this.isPrivate = mods.contains("private");
        this.isProtected = mods.contains("protected");
        this.isAbstract = mods.contains("abstract");
    }

    /**
     * Declare this method in the parent (class) context.
     * 
     * @param context
     *                the parent (class) context.
     * @param partial
     *                the code emitter (basically an abstraction
     *                for producing the partial class).
     */

    public void preAnalyze(Context context, CLEmitter partial) {
       
    }

    /**
     * Analysis for a method declaration involves (1) creating a
     * new method context (that records the return type; this is
     * used in the analysis of the method body), (2) bumping up
     * the offset (for instance methods), (3) declaring the
     * formal parameters in the method context, and (4) analyzing
     * the method's body.
     * 
     * @param context
     *                context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
       
	return this;
    }

    /**
     * Add this method declaration to the partial class.
     * 
     * @param context
     *                the parent (class) context.
     * @param partial
     *                the code emitter (basically an abstraction
     *                for producing the partial class).
     */

    public void partialCodegen(Context context, CLEmitter partial) {
        
    }

    /**
     * Generate code for the method declaration.
     * 
     * @param output
     *                the code emitter (basically an abstraction
     *                for producing the .class file).
     */

    public void codegen(CLEmitter output) {

       
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JBlockDeclaration line=\"%d\">\n", line());
        p.indentRight();
        if (context != null) {
            context.writeToStdOut(p);
        }
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }
        if (body != null) {
            p.println("<Body>");
            p.indentRight();
            body.writeToStdOut(p);
            p.indentLeft();
            p.println("</Body>");
        }
        p.indentLeft();
        p.println("</JBlockDeclaration>");
    }
}
