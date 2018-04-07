
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20160615 (GIT 4ac7450)
//----------------------------------------------------

package net.acptools.suite.ide.lang.cpp.generated;

/** CUP generated interface containing symbol constants. */
public interface sym {
  /* terminals */
  public static final int SHORT = 100;
  public static final int SIGNED = 101;
  public static final int EQOP = 53;
  public static final int IDENTIFIER = 111;
  public static final int BOOL = 93;
  public static final int TYPEID = 68;
  public static final int GT = 21;
  public static final int ARROW = 12;
  public static final int FLOATING = 96;
  public static final int DIVASSIGN = 24;
  public static final int CONSTEXPR = 46;
  public static final int CONST = 13;
  public static final int WORD = 95;
  public static final int REGISTER = 98;
  public static final int DIVOP = 55;
  public static final int ASSIGNMENT = 6;
  public static final int SIZEOF = 65;
  public static final int MODOP = 56;
  public static final int EXPLICIT = 41;
  public static final int COMMA = 42;
  public static final int ANDASSIGN = 19;
  public static final int LBRK = 3;
  public static final int LT = 20;
  public static final int DOTS = 36;
  public static final int INTEGER = 77;
  public static final int DOUBLE = 86;
  public static final int STRUCT = 38;
  public static final int OROP = 50;
  public static final int MODASSIGN = 25;
  public static final int FRIEND = 59;
  public static final int PROTECTED = 74;
  public static final int FALSE = 113;
  public static final int PLUSASSIGN = 26;
  public static final int NEQOP = 44;
  public static final int FINAL = 40;
  public static final int FLOAT = 88;
  public static final int GOTO = 90;
  public static final int DOUBLEAND = 16;
  public static final int RBRK = 4;
  public static final int RSQRBRK = 8;
  public static final int MINUSOP = 60;
  public static final int LSHIFTASSIGN = 29;
  public static final int CLASS = 37;
  public static final int INCLUDE = 109;
  public static final int NULLPTR = 71;
  public static final int SEPPTR = 49;
  public static final int TRUE = 112;
  public static final int LONG = 97;
  public static final int QUESTION = 18;
  public static final int WHILE = 106;
  public static final int LPAR = 33;
  public static final int UNION = 39;
  public static final int PLUSOP = 61;
  public static final int LSQRBRK = 7;
  public static final int CHAR = 83;
  public static final int MULTASSIGN = 23;
  public static final int SWITCH = 103;
  public static final int FOR = 89;
  public static final int DO = 85;
  public static final int STAR = 48;
  public static final int VOID = 105;
  public static final int EXTERN = 11;
  public static final int RETURN = 99;
  public static final int PUBLIC = 73;
  public static final int SINGLEAND = 15;
  public static final int ELSE = 87;
  public static final int BREAK = 81;
  public static final int DOT = 31;
  public static final int INT = 92;
  public static final int AUTODECRM = 64;
  public static final int SOROP = 51;
  public static final int LTE = 30;
  public static final int BINNEG = 67;
  public static final int STRING_LITERAL = 78;
  public static final int ARROWSTAR = 58;
  public static final int LOGNEGATION = 66;
  public static final int DECLTYPE = 75;
  public static final int EOF = 0;
  public static final int SEMICOLON = 2;
  public static final int THIS = 70;
  public static final int RPAR = 34;
  public static final int DEFAULT = 76;
  public static final int XOROP = 52;
  public static final int OPERATOR = 107;
  public static final int NOEXCEPT = 17;
  public static final int ORASSIGN = 9;
  public static final int error = 1;
  public static final int GTE = 10;
  public static final int CONTINUE = 84;
  public static final int ALIGNOF = 62;
  public static final int IF = 91;
  public static final int UNSIGNED = 104;
  public static final int COLON = 43;
  public static final int USING = 5;
  public static final int STATIC_ASSERT = 22;
  public static final int INCLUDE_LITERAL = 110;
  public static final int RSHIFTASSIGN = 28;
  public static final int VOLATILE = 14;
  public static final int CASE = 82;
  public static final int MINUSASSIGN = 27;
  public static final int DOTSTAR = 57;
  public static final int NEW = 108;
  public static final int TYPENAME = 32;
  public static final int AUTOINCRM = 63;
  public static final int STRING = 79;
  public static final int RSHIFT = 54;
  public static final int BYTE = 94;
  public static final int PRIVATE = 72;
  public static final int STATIC = 102;
  public static final int LSHIFT = 45;
  public static final int TYPEDEF = 69;
  public static final int AUTO = 80;
  public static final int ALIGNAS = 35;
  public static final int XORASSIGN = 47;
  public static final String[] terminalNames = new String[] {
  "EOF",
  "error",
  "SEMICOLON",
  "LBRK",
  "RBRK",
  "USING",
  "ASSIGNMENT",
  "LSQRBRK",
  "RSQRBRK",
  "ORASSIGN",
  "GTE",
  "EXTERN",
  "ARROW",
  "CONST",
  "VOLATILE",
  "SINGLEAND",
  "DOUBLEAND",
  "NOEXCEPT",
  "QUESTION",
  "ANDASSIGN",
  "LT",
  "GT",
  "STATIC_ASSERT",
  "MULTASSIGN",
  "DIVASSIGN",
  "MODASSIGN",
  "PLUSASSIGN",
  "MINUSASSIGN",
  "RSHIFTASSIGN",
  "LSHIFTASSIGN",
  "LTE",
  "DOT",
  "TYPENAME",
  "LPAR",
  "RPAR",
  "ALIGNAS",
  "DOTS",
  "CLASS",
  "STRUCT",
  "UNION",
  "FINAL",
  "EXPLICIT",
  "COMMA",
  "COLON",
  "NEQOP",
  "LSHIFT",
  "CONSTEXPR",
  "XORASSIGN",
  "STAR",
  "SEPPTR",
  "OROP",
  "SOROP",
  "XOROP",
  "EQOP",
  "RSHIFT",
  "DIVOP",
  "MODOP",
  "DOTSTAR",
  "ARROWSTAR",
  "FRIEND",
  "MINUSOP",
  "PLUSOP",
  "ALIGNOF",
  "AUTOINCRM",
  "AUTODECRM",
  "SIZEOF",
  "LOGNEGATION",
  "BINNEG",
  "TYPEID",
  "TYPEDEF",
  "THIS",
  "NULLPTR",
  "PRIVATE",
  "PUBLIC",
  "PROTECTED",
  "DECLTYPE",
  "DEFAULT",
  "INTEGER",
  "STRING_LITERAL",
  "STRING",
  "AUTO",
  "BREAK",
  "CASE",
  "CHAR",
  "CONTINUE",
  "DO",
  "DOUBLE",
  "ELSE",
  "FLOAT",
  "FOR",
  "GOTO",
  "IF",
  "INT",
  "BOOL",
  "BYTE",
  "WORD",
  "FLOATING",
  "LONG",
  "REGISTER",
  "RETURN",
  "SHORT",
  "SIGNED",
  "STATIC",
  "SWITCH",
  "UNSIGNED",
  "VOID",
  "WHILE",
  "OPERATOR",
  "NEW",
  "INCLUDE",
  "INCLUDE_LITERAL",
  "IDENTIFIER",
  "TRUE",
  "FALSE"
  };
}

