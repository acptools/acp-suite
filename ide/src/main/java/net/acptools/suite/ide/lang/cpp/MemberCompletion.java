package net.acptools.suite.ide.lang.cpp;

/**
 * Extra methods defined by a completion for a Java member (fields and methods).
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface MemberCompletion extends CppSourceCompletion {


    /**
     * Returns the name of the enclosing class.
     *
     * @param fullyQualified Whether the name returned should be fully
     *                       qualified.
     * @return The class name.
     */
    public String getEnclosingClassName(boolean fullyQualified);


    /**
     * Returns the signature of this member.
     *
     * @return The signature.
     */
    public String getSignature();


    /**
     * Returns the type of this member (the return type for methods).
     *
     * @return The type of this member.
     */
    public String getType();


    /**
     * Returns whether this member is deprecated.
     *
     * @return Whether this member is deprecated.
     */
    public boolean isDeprecated();

}