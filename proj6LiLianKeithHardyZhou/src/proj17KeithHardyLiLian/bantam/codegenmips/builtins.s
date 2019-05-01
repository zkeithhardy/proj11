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
        lw $t0 0(sp)
        lw $t0 12($t0)
        lw $t1 12($a0)
        seq $v0 $t0 $t1
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