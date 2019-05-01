#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-05-01
#Compiled From Source: test.btm
	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	-1

	# String Constants:
Class_4:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	7
	.ascii	"Integer"
	.byte	0
	.align	2
Class_7:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	1
	.ascii	"A"
	.byte	0
	.align	2
Class_8:
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
	.ascii	"Boolean"
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
Class_6:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2
StringConst_0:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"In A"
	.byte	0
	.align	2


class_name_table:
	.word	Class_0
	.word	Class_1
	.word	Class_4
	.word	Class_7
	.word	Class_8
	.word	Class_5
	.word	Class_6
	.word	Class_2
	.word	Class_3

	# Object Templates:
	.globl	Integer_template
	.globl	A_template
	.globl	B_template
	.globl	Object_template
	.globl	String_template
	.globl	Sys_template
	.globl	Boolean_template
	.globl	TextIO_template
	.globl	Main_template

Integer_template:
	.word	2		# Class ID
	.word	16		# Size of Object in Bytes
	.word	Integer_dispatch_table
	.word	0

A_template:
	.word	3		# Class ID
	.word	24		# Size of Object in Bytes
	.word	A_dispatch_table
	.word	0
	.word	0
	.word	0

B_template:
	.word	4		# Class ID
	.word	32		# Size of Object in Bytes
	.word	B_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0

Object_template:
	.word	0		# Class ID
	.word	12		# Size of Object in Bytes
	.word	Object_dispatch_table

String_template:
	.word	7		# Class ID
	.word	16		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	0

Sys_template:
	.word	8		# Class ID
	.word	12		# Size of Object in Bytes
	.word	Sys_dispatch_table

Boolean_template:
	.word	5		# Class ID
	.word	16		# Size of Object in Bytes
	.word	Boolean_dispatch_table
	.word	0

TextIO_template:
	.word	1		# Class ID
	.word	20		# Size of Object in Bytes
	.word	TextIO_dispatch_table
	.word	0
	.word	0

Main_template:
	.word	6		# Class ID
	.word	16		# Size of Object in Bytes
	.word	Main_dispatch_table
	.word	0

	# Dispatch Tables:
	.globl	Integer_dispatch_table
	.globl	A_dispatch_table
	.globl	B_dispatch_table
	.globl	Object_dispatch_table
	.globl	String_dispatch_table
	.globl	Sys_dispatch_table
	.globl	Boolean_dispatch_table
	.globl	TextIO_dispatch_table
	.globl	Main_dispatch_table
Integer_dispatch_table:
	.word	Object.clone
	.word	Integer.equals
	.word	Integer.toString
	.word	Integer.intValue
	.word	Integer.setValue
A_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	A.getX
	.word	A.dale
B_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	B.getX
	.word	A.dale
	.word	B.setX
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
Boolean_dispatch_table:
	.word	Object.clone
	.word	Boolean.equals
	.word	Boolean.toString
	.word	Boolean.booleanValue
	.word	Boolean.setValue
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
	.word	Main.foo
	.word	Main.main

	.text
	.globl	main
	.globl	Main_init
	.globl	Main.main
main:
	jal __start
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
Integer_init:
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
	# constant int expression: load 4 to $v0
	li $v0 4
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	# constant int expression: load 2 to $v0
	li $v0 2
	# store the field 16 away from $a0 to $v0
	sw $v0 16($a0)
	# store the field 16 away from $a0 to $v0
	sw $v0 16($a0)
	# constant string expression: load StringConst_0 to $v0
	la $v0 StringConst_0
	# store the field 20 away from $a0 to $v0
	sw $v0 20($a0)
	# store the field 20 away from $a0 to $v0
	sw $v0 20($a0)
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
	# constant int expression: load 5 to $v0
	li $v0 5
	# store the field 24 away from $a0 to $v0
	sw $v0 24($a0)
	# constant int expression: load 3 to $v0
	li $v0 3
	# store the field 28 away from $a0 to $v0
	sw $v0 28($a0)
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
Boolean_init:
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
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is null
	lw $a0 0($sp)
	add $sp $sp 4
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
Main.foo:
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
	# case where the reference object is null
	# load (12)$fp to $v0 
	lw $v0 12($fp)
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
	# add 4 to $sp and store the result to $sp
	add $sp $sp 4
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
	# assign expr
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $a0 to stack pointer with offset of 0
	sw $a0 0($sp)
	# load the address of A_template to $a0
	la $a0 A_template
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	# jump to A_init
	jal A_init
	# load (8)$fp to $a0
	lw $a0 8($fp)
	# case where the reference name is null
	# move current location's base register with offset to v0
	sw $v0 12($a0)
	# save stack pointer result to $a0
	lw $a0 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is null
	# load (12)$a0 to $v0 
	lw $v0 12($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# move $v0 to $a0
	move $a0 $v0
	# load (8)$v0 to $v0
	lw $v0 8($v0)
	# load method address
	# load (16)$v0 to $a1
	lw $a1 16($v0)
	# jump to $a1
	jalr $a1
	# load (0)$fp to $a0
	lw $a0 0($fp)
	# load the address of B_template to $a0
	la $a0 B_template
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	# jump to B_init
	jal B_init
	# load (8)$fp to $a0
	lw $a0 8($fp)
	# store $v0 to (0)$fp
	sw $v0 0($fp)
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
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
	# case where the reference object is user defined class
	# check for null pointer errors
	# if $v0 == 0, branch to nullError
	beq $zero $v0 label0
	b label1
label0:
	jal _null_pointer_error
	li $v0 17
	syscall
label1:
	# move $v0 to $a0
	move $a0 $v0
	# load (16)$a0 to $v0
	lw $v0 16($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# store $v0 to (4)$fp
	sw $v0 4($fp)
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
A.dale:
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
	# constant int expression: load 0 to $v0
	li $v0 0
	# store $v0 to (0)$fp
	sw $v0 0($fp)
	# assign expr
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $a0 to stack pointer with offset of 0
	sw $a0 0($sp)
	# constant int expression: load 0 to $v0
	li $v0 0
	# case where the reference name is null
	# move current location's base register with offset to v0
	sw $v0 0($fp)
	# save stack pointer result to $a0
	lw $a0 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
label2:
	# gen left side of expression
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
	# move v0 to v1
	move $v1 $v0
	# gen right side of expression
	# constant int expression: load 2 to $v0
	li $v0 2
	# compare left and right sides of expression
	slt $v0 $v1 $v0
	sub $v0 $zero $v0
	# branch to label3 if $v0 is equal to 0
	beq $zero $v0 label3
	# constant int expression: load 11 to $v0
	li $v0 11
	# store $v0 to (4)$fp
	sw $v0 4($fp)
	# increment
	# case where the reference object is null
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
	# add 1 to $v0
	add $v0 $v0 1
	# store (0)$fp to $v0
	sw $v0 0($fp)
	# sub 1 to $v0
	sub $v0 $v0 1
	# unconditional branch to label2
	b label2
label3:
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
	# load (24)$a0 to $v0 
	lw $v0 24($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# store left expression on stack
	sub $sp $sp 4
	sw $v0 0($sp)
	# gen right side of expression
	# access dispatch_table
	# load the address of A_template to $a0
	la $a0 A_template
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	# jump to A_init
	jal A_init
	# load (0)$fp to $a0
	lw $a0 0($fp)
	# move $v0 to $a0
	move $a0 $v0
	# load A_dispatch_table to $v0
	la $v0 A_dispatch_table
	# load method address
	# load (12)$v0 to $a1
	lw $a1 12($v0)
	# jump to $a1
	jalr $a1
	# load (0)$fp to $a0
	lw $a0 0($fp)
	# load left expression on stack
	lw $v1 0($sp)
	# increment sp by 4
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
	# add 4 to $sp and store the result to $sp
	add $sp $sp 4
	jr $ra
	# End Epilogue
B.setX:
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
	# assign expr
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $a0 to stack pointer with offset of 0
	sw $a0 0($sp)
	# constant int expression: load 8 to $v0
	li $v0 8
	# case where the reference name is /super/
	# move current location's base register with offset to v0
	sw $v0 24($a0)
	# save stack pointer result to $a0
	lw $a0 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	# var expression
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is /.super/
	# load (24)$a0 to $v0 
	lw $v0 24($a0)
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