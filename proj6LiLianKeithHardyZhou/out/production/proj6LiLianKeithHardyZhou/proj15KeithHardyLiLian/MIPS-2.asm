# file:  MIPS-2.asm
# author: Zeb Keith-Hardy, Michael Li, Iris Lian
# date: Mar, 15, 2019
# This program use function call to compute the multiple of two integers
# it then output the result by printing it to the console

    .text       # the code segment of the program
    .globl main # the starting point of the program

main:
    li $s0, 0 #initialize the sum to zero
    li $s3, 1 #set the counter for addition
    
    li $v0, 5 #read next int to v0
    syscall 
    add $s1, $v0, 0 #save the read int to s1
    
    li $v0, 5#read next int to v0
    syscall 
    add $s2, $v0, 0 #save the read int to s2
    
    addi $sp, $sp, -16    #allocate room for s0-3
    sw $s0, 0($sp)        #save parameters by stack pointer's position
    sw $s1, 4($sp)
    sw $s2, 8($sp)
    sw $s3, 16($sp)
    
    jal multiply	   #call the multiply function
    addi $sp, $sp, 16     #free the memory in the stack
    li $v0, 1             #print the result on the console
    syscall
    li $v0, 10
    syscall     # exit
multiply:
    lw $t0, 0($sp)      #load parameters
    lw $t1, 4($sp)  
    lw $t2, 8($sp)  
    lw $t3, 16($sp)
loop:
    add $t0, $t0, $t1   #use the recursive addition to sum the two input integers
    sub $t2, $t2, $t3
    blez $t2, endloop	 #branch to endloop when the counter is done
    b loop		 #branch to loop if the counter is not depleted
endloop:
    move $a0, $t0	 #save the "temporary" register value to v0    
    jr $ra 		 #return 
    