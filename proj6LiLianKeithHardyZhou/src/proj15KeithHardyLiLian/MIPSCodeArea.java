/*
 * File: JavaCodeArea.java
 * Names: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 * ---------------------------
 * Edited By: Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * Project 6/7/9
 * Date: October 26, 2018/ November 3, 2018/ November 20, 2018
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
 * This class is the controller for all of the toolbar functionality.
 * Specifically the compile, compile and run, and stop buttons
 *
 * @author  Zeb Keith-Hardy, Michael Li, Iris Lian, Kevin Zhou
 * @author  Kevin Ahn, Jackie Hang, Matt Jones, Kevin Zhou
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou
 * @version 2.0
 * @since   10-3-2018
 */
public class MIPSCodeArea extends CodeArea{

    /**
     * This is the constructor of JavaCodeArea
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
 * source:  https://moodle.colby.edu/pluginfile.php/294745/mod_resource/content/0/JavaKeywordsDemo.java
 * @author  Matt Jones, Kevin Zhou, Kevin Ahn, Jackie Hang
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou
 * @author  Iris Lian, Zeb Keith-Hardy, Michael Li
 * @version 3.0
 * @since   09-30-2018
 */
class MIPSStyle {

    // a list of strings that contain the operations for the IDE to identify.
    private static final String[] OPERATION = new String[]{
            "abs","b","beqz","bge","bgeu","bgt","bgtu","ble","bleu","blt","bltu","bnez",
            "l.d","l.s","la","lbu","ld","ldc1","lh","lhu","li","ll","lwc1","lwl","lwr",
            "mfc1.d","move","mtcl.d","mul","mulo","mulou","mulu","neg","not","rem","remu",
            "rol","ror","s.d","s.s","sc","sd","sdc1","seq","sge","sgeu","sgt","sgtu","sh",
            "sle","sleu","sne","subi","subiu","swc1","swl","swr","ulh","ulhu","ulw","ush",
            "usw",

            "abs.d","abs.s","add.d","add.s","bc1f","bc1t","break","c.eq.d","c.eq.s","c.le.d",
            "c.le.s","c.lt.d","c.lt.s","ceil.w.d","ceil.w.s","clo","clz","cvt.d.s","cvt.d.w",
            "cvt.s.d","cvt.s.w","cvt.w.d","cvt.w.s","div.d","div.s","eret","floor.w.d",
            "floor.w.s","lui","madd","maddu","mfc0","mfc1","mov.d","mov.s","movf","movf.d",
            "movf.s","movn","movn.d","movn.s","movt","movt.d","movt.s","movz","movz.d",
            "movz.s","msub","msubu","mtc0","mtc1","mthi","mtlo","mul.d","mul.s","neg.d",
            "neg.s","nop","nor","round.w.d","round.w.s","sqrt.d","sqrt.s","sub.d","sub.s",
            "teq","teqi","tge","tgei","tgeiu","tgeu","tlt","tlti","tltiu","tltu","tne",
            "tnei","trunc.w.d","trunc.w.s",

            "add","addi","addiu","addu","and","andi","beq","bgez","bgezal","bgtz","blez",
            "bltz","bltzal","bne","div","divu","j","jal","jalr","jr","lb","lur","lw","mfhi",
            "mflo","mult","multu","noop","or","ori","sb","sll","sllv","slt","slti","sltiu",
            "sltu","sra","srl","srlv","sub","subu","sw","syscall","xor","xori"
    };

    // a list of strings that contain the directives for the IDE to identify.
    private static final String[] DIRECTIVE = new String[]{
            "data","text","word","ascii","asciiz","byte","align","half","space",
            "double","float","extern","kdata","ktext","globl","set","eqv","macro",
            "end_macro","include"
    };

    // a list of strings that contain the registers for the IDE to identify.
    private static final String[] REGISTER = new String[]{
            "zero","at","v0","v1","a0","a1","a2","a3","t0","t1","t2","t3",
            "t4","t5","t6","t7","t8","t9","s0","s1","s2","s3","s4","s5","s6",
            "s7","s8","k0","k1","gp","sp","fp","ra"
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
     * Code obtained from the RichTextFX Demo from GitHub.
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

