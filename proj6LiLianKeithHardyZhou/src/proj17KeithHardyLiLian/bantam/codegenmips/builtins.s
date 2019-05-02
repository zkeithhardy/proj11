#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-05-01


.text

    .globl Integer.equals
    .globl Integer.toString
    .globl Integer.intValue
    .globl Integer.setValue
    .globl Boolean.equals
    .globl Boolean.toString
    .globl Boolean.booleanValue
    .globl Boolean.setValue


Integer.equals:
        sub $sp $sp 12
        sw $t0 0($sp)
        sw $t1 4($sp)
        sw $t2 8($sp)   #prologue

        lw $t0 12($sp)  #check if objects are of same type
        lw $t1 0($t0)
        lw $t2 0($a0)
        bne $t1 $t2 false_i

        lw $t0 12($t0)  #check if values are of same type
        lw $t1 12($a0)
        seq $v0 $t0 $t1
        sub $v0 $zero $v0
        b return_i_equals

        false_i:
        li $v0 0

        return_i_equals:        #epilogue
        lw $t0 0($sp)
        lw $t1 4($sp)
        lw $t2 8($sp)
        add $sp $sp 12
        jr $ra


Integer.toString:
        jr $ra


Integer.intValue:
        lw $v0 12($a0)
        jr $ra


Integer.setValue:
        lw $t0 0($sp)
        sw $t0 12($a0)
        jr $ra


Boolean.equals:
        sub $sp $sp 12
        sw $t0 0($sp)
        sw $t1 4($sp)
        sw $t2 8($sp)   #prologue

        lw $t0 12($sp)  #check if objects are of same type
        lw $t1 0($t0)
        lw $t2 0($a0)
        bne $t1 $t2 false_b

        lw $t0 12($t0)  #check if values are of same type
        lw $t1 12($a0)
        seq $v0 $t0 $t1
        b return_b_equals

        false_b:
        li $v0 0

        return_b_equals:        #epilogue
        lw $t0 0($sp)
        lw $t1 4($sp)
        lw $t2 8($sp)
        add $sp $sp 12
        jr $ra

Boolean.toString:

        jr $ra


Boolean.booleanValue:
        lw $v0 12($a0)
        jr $ra

Boolean.setValue:
    lw $t0 0($sp)
    sw $t0 12($a0)
    jr $ra