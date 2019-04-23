#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-04-22
#Compiled From Source: test.btm
	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	-1

	# String Constants:
Class_6:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	1
	.ascii	"A"
	.byte	0
	.align	2
Class_7:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	1
	.ascii	"B"
	.byte	0
	.align	2
Class_0:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	6
	.ascii	"Object"
	.byte	0
	.align	2
Class_2:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	6
	.ascii	"String"
	.byte	0
	.align	2
Class_3:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	3
	.ascii	"Sys"
	.byte	0
	.align	2
Class_5:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	7
	.ascii	"SubMain"
	.byte	0
	.align	2
Class_1:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	6
	.ascii	"TextIO"
	.byte	0
	.align	2
Class_4:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2


class_name_table:
	.word	Class_0
	.word	Class_6
	.word	Class_7
	.word	Class_2
	.word	Class_3
	.word	Class_4
	.word	Class_5
	.word	Class_1

	# Object Templates:
	.globl	A_template
	.globl	B_template
	.globl	Object_template
	.globl	String_template
	.globl	Sys_template
	.globl	SubMain_template
	.globl	TextIO_template
	.globl	Main_template

A_template:
	.word	1
	.word	20
	.word	A_dispatch_table
	.word	0
	.word	0

B_template:
	.word	2
	.word	28
	.word	B_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0

Object_template:
	.word	0
	.word	12
	.word	Object_dispatch_table

String_template:
	.word	3
	.word	16
	.word	String_dispatch_table
	.word	0

Sys_template:
	.word	4
	.word	12
	.word	Sys_dispatch_table

SubMain_template:
	.word	6
	.word	24
	.word	SubMain_dispatch_table
	.word	0
	.word	0
	.word	0

TextIO_template:
	.word	7
	.word	20
	.word	TextIO_dispatch_table
	.word	0
	.word	0

Main_template:
	.word	5
	.word	20
	.word	Main_dispatch_table
	.word	0
	.word	0

	# Dispatch Tables:
	.globl	A_dispatch_table
	.globl	B_dispatch_table
	.globl	Object_dispatch_table
	.globl	String_dispatch_table
	.globl	Sys_dispatch_table
	.globl	SubMain_dispatch_table
	.globl	TextIO_dispatch_table
	.globl	Main_dispatch_table
A_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	A.getX
B_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	B.getX
Object_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
String_dispatch_table:
	.word	Object.clone
	.word	String.equals
	.word	String.toString
	.word	String.length
	.word	String.substring
	.word	String.concat
Sys_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Sys.exit
	.word	Sys.time
	.word	Sys.random
SubMain_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Main.main
TextIO_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	TextIO.readStdin
	.word	TextIO.readFile
	.word	TextIO.writeStdout
	.word	TextIO.writeStderr
	.word	TextIO.writeFile
	.word	TextIO.getString
	.word	TextIO.getInt
	.word	TextIO.putString
	.word	TextIO.putInt
Main_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Main.main

	.text
	.globl	main
	.globl	Main_init
	.globl	Main.main
main:
	jal __start
A_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	jal Object_init
	# constant int expression
	li $v0 4
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	# constant int expression
	li $v0 2
	# store the field 16 away from $a0 to $v0
	sw $v0 16($a0)
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
B_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	jal Object_init
	jal A_init
	# constant int expression
	li $v0 5
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	# constant int expression
	li $v0 3
	# store the field 16 away from $a0 to $v0
	sw $v0 16($a0)
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
Object_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
String_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	li $v0 0
	sw $v0 12($a0)
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
Sys_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
SubMain_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	jal Object_init
	jal Main_init
	# constant int expression
	li $v0 400
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
TextIO_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	li $v0 0
	sw $v0 12($a0)
	li $v0 1
	sw $v0 16($a0)
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
Main_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	jal Object_init
	# constant int expression
	li $v0 3
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	# constant int expression
	li $v0 4
	# store the field 16 away from $a0 to $v0
	sw $v0 16($a0)
	move $v0 $a0
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
Main.main:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 8 from $sp and store the result to $fp
	sub $fp $sp 8
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	# load the address of B_template to $a0
	la $a0 B_template
	# load (8)$a0 to $v0
	# jump to $v0
	jal Object.clone
	move $a0 $v0
	# jump to B_init
	jal B_init
	lw $a0 8($fp)
	# store $v0 to (0)$fp
	sw $v0 0($fp)
	sub $sp $sp 4
	sw $a0 0($sp)
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is null
	# load (0)$fp to $v0 
	lw $v0 0($fp)
	lw $a0 0($sp)
	add $sp $sp 4
	move $a0 $v0
	lw $v0 8($v0)
	# load method address
	lw $a1 12($v0)
	jalr $a1
	lw $a0 0($sp)
	add $sp $sp 4
	# store $v0 to (4)$fp
	sw $v0 4($fp)
	move $a0 $v0 
	li $v0 1
	syscall
	# Start Epilogue
	# add 8 to $fp and store the result to $sp
	add $sp $fp 8
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
A.getX:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is /this./
	# load (12)$a0 to $v0 
	lw $v0 12($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue
B.getX:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $ra to $sp
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $fp to $sp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $a0 to $sp
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	# gen left side of expression
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is /this./
	# load (12)$a0 to $v0 
	lw $v0 12($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# store left expression on stack
	sub $sp $sp 4
	sw $v0 0($sp)
	# gen right side of expression
	sub $sp $sp 4
	sw $a0 0($sp)
	# access dispatch_table
	# load the address of A_template to $a0
	la $a0 A_template
	# load (8)$a0 to $v0
	# jump to $v0
	jal Object.clone
	move $a0 $v0
	# jump to A_init
	jal A_init
	lw $a0 0($fp)
	move $a0 $v0
	la $v0 A_dispatch_table
	# load method address
	lw $a1 12($v0)
	jalr $a1
	lw $a0 0($sp)
	add $sp $sp 4
	# load left expression on stack
	lw $v1 0($sp)
	add $sp $sp 4
	# add left and right sides of expression
	add $v0 $v0 $v1
	# Start Epilogue
	# add 0 to $fp and store the result to $sp
	add $sp $fp 0
	# load $sp to $a0
	lw $a0 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $fp
	lw $fp 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# load $sp to $ra
	lw $ra 0($sp)
	# add 4 to $sp
	add $sp $sp 4
	# add 0 to $sp and store the result to $sp
	add $sp $sp 0
	jr $ra
	# End Epilogue