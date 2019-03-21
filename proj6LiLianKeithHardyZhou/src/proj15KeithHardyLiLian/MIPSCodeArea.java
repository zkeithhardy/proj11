/*
 * File: MIPSCodeArea.java
 * Names: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 * ---------------------------
 * Edited By: Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * Project 15
 * Date: March 21 2019/October 26, 2018/ November 3, 2018/ November 20, 2018
 */

package proj15KeithHardyLiLian;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the CodeArea when the user opened  a MIPS file. It contains various
 * styles for MIPS file so that the highlighting is matching MARS style
 *
 * @author  Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * @author  Kevin Ahn, Jackie Hang, Matt Jones, Kevin Zhou
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou
 * @version 2.0
 * @since   10-3-2018
 */
public class MIPSCodeArea extends CodeArea{

    /**
     * This is the constructor of MIPS CodeArea
     */
    public MIPSCodeArea(){
        super();
        this.subscribe();
    }

    /**
     * Method obtained from the RichTextFX Keywords Demo. Method allows
     * for syntax highlighting after a delay of 500ms after typing has ended.
     * This method was copied from JavaKeyWordsDemo
     * Original Author: Jordan Martinez
     */
    private void subscribe() {
        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription codeCheck = this

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> this.setStyleSpans(0, MIPSStyle.computeHighlighting(this.getText())));
    }
}

/**
 * This class defines the keywords in MIPS file that are about to be highlighted by the
 * IDE. The keywords include the operations, directives and the list of all registers.
 * @author  Matt Jones, Kevin Zhou, Kevin Ahn, Jackie Hang
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou
 * @author  Iris Lian, Zeb Keith-Hardy, Michael Li
 * @version 3.0
 * @since   09-30-2018
 */
class MIPSStyle {

    // a list of strings that contain the operations for the IDE to identify.
    private static final String[] OPERATION = new String[]{
            "xori","xor","usw","ush","ulw","ulhu","ulh","trunc.w.s","trunc.w.d","tnei",
            "tne","tltu","tltiu","tlti","tlt","tgeu","tgeiu","tgei","tge","teqi","teq",
            "syscall","swr","swl","swc1","sw","subu","subiu","subi","sub.s","sub.d","sub",
            "srlv","srl","sra","sqrt.s","sqrt.d","sne","sltu","sltiu","slti","slt","sllv",
            "sll","sleu","sle","sh","sgtu","sgt","sgeu","sge","seq","sdc1","sd","sc","sb",
            "s.s","s.d","round.w.s","round.w.d","ror","rol","remu","rem","ori","or","not",
            "nor","nop","noop","neg.s","neg.d","neg","mulu","multu","mult","mulou","mulo",
            "mul.s","mul.d","mul","mtlo","mthi","mtcl.d","mtc1","mtc0","msubu","msub","movz.s",
            "movz.d","movz","movt.s","movt.d","movt","movn.s","movn.d","movn","movf.s","movf.d",
            "movf","move","mov.s","mov.d","mflo","mfhi","mfc1.d","mfc1","mfc0","maddu","madd",
            "lwr","lwl","lwc1","lw","lur","lui","ll","li","lhu","lh","ldc1","ld","lbu","lb",
            "la","l.s","l.d","jr","jalr","jal","j","floor.w.s","floor.w.d","eret","divu","div.s",
            "div.d","div","cvt.w.s","cvt.w.d","cvt.s.w","cvt.s.d","cvt.d.w","cvt.d.s","clz",
            "clo","ceil.w.s","ceil.w.d","c.lt.s","c.lt.d","c.le.s","c.le.d","c.eq.s","c.eq.d",
            "break","bnez","bne","bltzal","bltz","bltu","blt","blez","bleu","ble","bgtz","bgtu",
            "bgt","bgezal","bgez","bgeu","bge","beqz","beq","bc1t","bc1f","b","andi","and","addu",
            "addiu","addi","add.s","add.d","add","abs.s","abs.d","abs"
    };

    // a list of strings that contain the directives for the IDE to identify.
    private static final String[] DIRECTIVE = new String[]{
            "word","text","space","set","macro","ktext","kdata","include","half","globl","float",
            "extern","eqv","end_macro","double","data","byte","asciiz","ascii","align"
    };

    // a list of strings that contain the registers for the IDE to identify.
    private static final String[] REGISTER = new String[]{
            "zero","v1","v0","t9","t8","t7","t6","t5","t4","t3","t2","t1","t0","sp","s8","s7",
            "s6","s5","s4","s3","s2","s1","s0","ra","k1","k0","gp","fp","at","a3","a2","a1","a0"
    };

    // the regex rules for the ide
    private static final String IDENTIFIER_PATTERN = "[a-zA-Z]+[a-zA-Z0-9_]*";
    private static final String FLOAT_PATTERN = "(\\d+\\.\\d+)";
    private static final String INTCONST_PATTERN = "\\d+";
    private static final String OPERATION_PATTERN = "\\b(" + String.join("|", OPERATION) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "#[^\n]*"; //"^([^#]*)#(.*)$"
    private static final String DIRECTIVE_PATTERN = "\\.(" + String.join("|", DIRECTIVE) + ")";
    private static final String REGISTER_PATTERN =  "\\$(" + String.join("|", REGISTER) + ")";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<OPERATION>" + OPERATION_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<FLOAT>" + FLOAT_PATTERN + ")"
                    + "|(?<INTCONST>" + INTCONST_PATTERN + ")"
                    + "|(?<IDENTIFIER>" + IDENTIFIER_PATTERN + ")"
                    + "|(?<DIRECTIVE>" + DIRECTIVE_PATTERN + ")"
                    + "|(?<REGISTER>" + REGISTER_PATTERN + ")"
    );

    /**
     * Method to highlight all of the regex rules and keywords.
     *
     * @param text a string analyzed for proper syntax highlighting
     */
    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("OPERATION") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                            matcher.group("STRING") != null ? "string" :
                                    matcher.group("COMMENT") != null ? "comment" :
                                            matcher.group("IDENTIFIER") != null ? "identifier" :
                                                    matcher.group("INTCONST") != null ? "intconst" :
                                                            matcher.group("DIRECTIVE") != null ? "directive" :
                                                                    matcher.group("REGISTER") != null ? "register" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}

