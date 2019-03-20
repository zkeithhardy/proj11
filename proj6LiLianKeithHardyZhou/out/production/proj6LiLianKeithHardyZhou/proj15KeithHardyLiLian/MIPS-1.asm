# file:  MIPS-1.asm
# author: Zeb Keith-Hardy, Michael Li, Iris Lian
# date: March 15, 2019
# This program computes the product of two integers using addition

    .text       # the code segment of the program
    .globl main # the starting point of the program
    
main:   
    li $t0, 0   # initialize the sum to 0 
    li $t3, 1   # subtraction counter
    li $v0 5
    syscall     # read next int into $v0
    add $t1,$v0, 0 # move to $t1
    li $v0 5
    syscall # read next int into $v0
    add $t2,$v0, 0 # move to $t2
loop:    
    add $t0, $t0, $t1 # add value to the sum
    sub $t2, $t2, $t3 # subtract 1 from the counter
    blez $t2,endloop
    b loop

endloop: 
    move $a0, $t0
    li $v0, 1
    syscall     # output the value in $a0
    li $v0, 10
    syscall     # exit