// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * A interface declaration has a list of modifiers, a name, a super interface and a
 * interface block; it distinguishes between instance fields and static (interface)
 * fields for initialization, and it defines a type. It also introduces its own
 * (interface) context.
 */

class JInterfaceDeclaration extends JAST implements JTypeDecl {

    /** interface modifiers. */
    private ArrayList<String> mods;

    /** Interface name. */
    private String name;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;

    /** Super interface types. */
    private ArrayList<Type> superInterfaces;

    /** This interface type. */
    private Type thisType;

    /** Context for this interface. */
    private ClassContext context;

    /** Whether this class has an explicit constructor. */
    private boolean hasExplicitConstructor;

    /** Instance fields of this class. */
    private ArrayList<JFieldDeclaration> instanceFieldInitializations;

    /** Static (class) fields of this class. */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;

    /**
     * Construct an AST node for a class declaration given the line number, list
     * of class modifiers, name of the class, its super class type, and the
     * class block.
     * 
     * @param line
     *            line in which the class declaration occurs in the source file.
     * @param mods
     *            class modifiers.
     * @param name
     *            class name.
     * @param superType
     *            super class type.
     * @param classBlock
     *            class block.
     */

    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name,
    		ArrayList<Type> superInterfaces, ArrayList<JMember> interfaceBlock) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.superInterfaces = superInterfaces;
        this.interfaceBlock = interfaceBlock;
        hasExplicitConstructor = false;
        instanceFieldInitializations = new ArrayList<JFieldDeclaration>();
        staticFieldInitializations = new ArrayList<JFieldDeclaration>();
    }

    /**
     * Return the interface name.
     * 
     * @return the interface name.
     */

    public String name() {
        return name;
    }

    /**
     * Return the type that this interface declaration defines.
     * 
     * @return the defined type.
     */

    public Type thisType() {
        return thisType;
    }
    
    public Type superType() {
        return Type.VOID;
    }

    /**
     * The initializations for instance fields (now expressed as assignment
     * statments).
     * 
     * @return the field declarations having initializations.
     */

    public ArrayList<JFieldDeclaration> instanceFieldInitializations() {
        return instanceFieldInitializations;
    }

    /**
     * Declare this interface in the parent (compilation unit) context.
     * 
     * @param context
     *            the parent (compilation unit) context.
     */

    public void declareThisType(Context context) {
       
    }

    /**
     * Pre-analyze the members of this declaration in the parent context.
     * Pre-analysis extends to the member headers (including method headers) but
     * not into the bodies.
     * 
     * @param context
     *            the parent (compilation unit) context.
     */

    public void preAnalyze(Context context) {
      
    }

    /**
     * Perform semantic analysis on the interface and all of its members within the
     * given context. Analysis includes field initializations and the method
     * bodies.
     * 
     * @param context
     *            the parent (compilation unit) context. Ignored here.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
    	
        return this;
    }

    /**
     * Generate code for the interface declaration.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .interface file).
     */

    public void codegen(CLEmitter output) {
      
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
    	String interfaces = "";
    	if(superInterfaces.isEmpty()) {
    		interfaces = "null";
    	} else {
    		for(Type t : superInterfaces)
    			interfaces = interfaces + t.toString() + ",";
    		interfaces = interfaces.substring(0, interfaces.length() - 1);
    	}
    	
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\""
                + " super=\"%s\">\n", line(), name, interfaces);
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
        if (interfaceBlock != null) {
            p.println("<InterfaceBlock>");
            for (JMember member : interfaceBlock) {
                ((JAST) member).writeToStdOut(p);
            }
            p.println("</InterfaceBlock>");
        }
        p.indentLeft();
        p.println("</JInterfaceDeclaration>");
    }

    /**
     * Generate code for an implicit empty constructor. (Necessary only if there
     * is not already an explicit one.)
     * 
     * @param partial
     *            the code emitter (basically an abstraction for producing a
     *            Java interface).
     */

    private void codegenPartialImplicitConstructor(CLEmitter partial) {
    
    }

    /**
     * Generate code for an implicit empty constructor. (Necessary only if there
     * is not already an explicit one.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .interface file).
     */

    private void codegenImplicitConstructor(CLEmitter output) {
      
    }

    /**
     * Generate code for interface initialization, in j-- this means static field
     * initializations.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .interface file).
     */

    private void codegenInterfaceInit(CLEmitter output) {
        
    }

}
