#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-05-16
#Compiled From Source: test.btm
	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	-1

	# String Constants:
Class_14:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	1
	.ascii	"A"
	.byte	0
	.align	2
Class_16:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	1
	.ascii	"B"
	.byte	0
	.align	2
Class_15:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	3
	.ascii	"A[]"
	.byte	0
	.align	2
Class_18:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	1
	.ascii	"C"
	.byte	0
	.align	2
Class_19:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	3
	.ascii	"C[]"
	.byte	0
	.align	2
Class_17:
	.word	1		# String Identifier
	.word	20		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	3
	.ascii	"B[]"
	.byte	0
	.align	2
Class_9:
	.word	1		# String Identifier
	.word	28		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	9
	.ascii	"Boolean[]"
	.byte	0
	.align	2
Class_10:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	5
	.ascii	"int[]"
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
Class_7:
	.word	1		# String Identifier
	.word	28		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	8
	.ascii	"String[]"
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
Class_8:
	.word	1		# String Identifier
	.word	28		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	9
	.ascii	"Integer[]"
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
	.word	7
	.ascii	"Integer"
	.byte	0
	.align	2
Class_6:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	5
	.ascii	"Array"
	.byte	0
	.align	2
Class_13:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	6
	.ascii	"Main[]"
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
Class_11:
	.word	1		# String Identifier
	.word	28		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	9
	.ascii	"boolean[]"
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
Class_12:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2
StringConst_0:
	.word	1		# String Identifier
	.word	28		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	8
	.ascii	"test.btm"
	.byte	0
	.align	2


class_name_table:
	.word	Class_0
	.word	Class_1
	.word	Class_14
	.word	Class_18
	.word	Class_16
	.word	Class_4
	.word	Class_5
	.word	Class_6
	.word	Class_15
	.word	Class_19
	.word	Class_17
	.word	Class_8
	.word	Class_9
	.word	Class_10
	.word	Class_11
	.word	Class_13
	.word	Class_7
	.word	Class_12
	.word	Class_2
	.word	Class_3

	# Object Templates:
	.globl	A_template
	.globl	B_template
	.globl	C_template
	.globl	String_template
	.globl	Sys_template
	.globl	TextIO_template
	.globl	Integer_template
	.globl	Array_template
	.globl	Object_template
	.globl	Boolean_template
	.globl	Main_template

A_template:
	.word	2		# Class ID
	.word	16		# Size of Object in Bytes
	.word	A_dispatch_table
	.word	0

B_template:
	.word	4		# Class ID
	.word	16		# Size of Object in Bytes
	.word	B_dispatch_table
	.word	0

C_template:
	.word	3		# Class ID
	.word	16		# Size of Object in Bytes
	.word	C_dispatch_table
	.word	0

String_template:
	.word	18		# Class ID
	.word	16		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	0

Sys_template:
	.word	19		# Class ID
	.word	12		# Size of Object in Bytes
	.word	Sys_dispatch_table

TextIO_template:
	.word	1		# Class ID
	.word	20		# Size of Object in Bytes
	.word	TextIO_dispatch_table
	.word	0
	.word	0

Integer_template:
	.word	5		# Class ID
	.word	16		# Size of Object in Bytes
	.word	Integer_dispatch_table
	.word	0

Array_template:
	.word	7		# Class ID
	.word	16		# Size of Object in Bytes
	.word	Object_dispatch_table
	.word	0

Object_template:
	.word	0		# Class ID
	.word	12		# Size of Object in Bytes
	.word	Object_dispatch_table

Boolean_template:
	.word	6		# Class ID
	.word	16		# Size of Object in Bytes
	.word	Boolean_dispatch_table
	.word	0

Main_template:
	.word	17		# Class ID
	.word	20		# Size of Object in Bytes
	.word	Main_dispatch_table
	.word	0
	.word	0

	# Dispatch Tables:
	.globl	A_dispatch_table
	.globl	B_dispatch_table
	.globl	C_dispatch_table
	.globl	String_dispatch_table
	.globl	Sys_dispatch_table
	.globl	TextIO_dispatch_table
	.globl	Integer_dispatch_table
	.globl	Object_dispatch_table
	.globl	Boolean_dispatch_table
	.globl	Main_dispatch_table
A_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
B_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	B.getA
C_dispatch_table:
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
Integer_dispatch_table:
	.word	Object.clone
	.word	Integer.equals
	.word	Integer.toString
	.word	Integer.intValue
	.word	Integer.setValue
Object_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
Boolean_dispatch_table:
	.word	Object.clone
	.word	Boolean.equals
	.word	Boolean.toString
	.word	Boolean.booleanValue
	.word	Boolean.setValue
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
	# load the address of Array_template to $a0
	la $a0 Array_template
	# constant int expression: load 4 to $v0
	li $v0 4
	ble $v0 $zero label1
	li $t1 1500
	bge $v0 $t1 label1
	b label0
label1:
	move $t0 $v0
	li $a1 104
	la $a2 StringConst_0
	jal _array_size_error
label0:
	sub $sp $sp 4
	sw $v0 0($sp)
	# save size of array in correct space
	mul $v1 $v0 4
	add $v1 $v1 16
	sw $v1 4($a0)
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	li $v0 11
	sw $v0 0($a0)
	# jump to Array_init
	jal Array_init
	add $sp $sp 4
	# load (0)$fp to $a0
	lw $a0 0($fp)
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
C_init:
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
Array_init:
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
	lw $v1 12($sp)
	sw $v1 12($a0)
	li $t0 16
	li $t1 0
	move $a3 $a0
label2:
	beq $t1 $v1 label3
	li $v0 0
	add $a3 $a0 $t0
	sw $v0 0($a3)
	add $t0 $t0 4
	add $t1 $t1 1
	b label2
label3:
	sw $t0 4($a0)
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
	# constant int expression: load 3 to $v0
	li $v0 3
	# store the field 12 away from $a0 to $v0
	sw $v0 12($a0)
	# load the address of Array_template to $a0
	la $a0 Array_template
	# constant int expression: load 2 to $v0
	li $v0 2
	ble $v0 $zero label5
	li $t1 1500
	bge $v0 $t1 label5
	b label4
label5:
	move $t0 $v0
	li $a1 5
	la $a2 StringConst_0
	jal _array_size_error
label4:
	sub $sp $sp 4
	sw $v0 0($sp)
	# save size of array in correct space
	mul $v1 $v0 4
	add $v1 $v1 16
	sw $v1 4($a0)
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	li $v0 10
	sw $v0 0($a0)
	# jump to Array_init
	jal Array_init
	add $sp $sp 4
	# load (0)$fp to $a0
	lw $a0 0($fp)
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
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	# Array Assign Expr
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $a0 to stack pointer with offset of 0
	sw $a0 0($sp)
	# case where the reference name is null
	# visit the index expression, this will load the expression to v0
	# constant int expression: load 0 to $v0
	li $v0 0
	# get the array's address then add it to the offset 
	lw $v1 16($a0)
	bne $v1 $zero label6
	li $a1 17
	la $a2 StringConst_0
	jal _null_pointer_error
label6:
	lw $t1 12($v1)
	blt $v0 $zero label8
	bge $v0 $t1 label8
	b label7
label8:
	move $t0 $v0
	li $a1 17
	la $a2 StringConst_0
	jal _array_index_error
label7:
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $v1 to stack pointer with offset of 0
	sw $v1 0($sp)
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $v1 to stack pointer with offset of 0
	sw $v0 0($sp)
	# load the address of C_template to $a0
	la $a0 C_template
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	# jump to C_init
	jal C_init
	# load (0)$fp to $a0
	lw $a0 0($fp)
	# save stack pointer result to $v1
	lw $t2 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	# save stack pointer result to $v1
	lw $v1 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	lw $t1 0($v1)
	# load j into $t0 and j+k into $t1
	lw $t0 0($v1)
	li $t1 2
	add $t1 $t1 $t0
	# load type of expression in $v0 into $t3
	lw $t3 0($v0)
	# compare j<=i and i<=j+k, if both true, return true
	sle $t0 $t0 $t3
	sle $t3 $t3 $t1
	# $t0 AND $t3, store in $t3
	and $t3 $t0 $t3
	bne $t3 $zero label10
	lw $t0 0($v1)
	lw $t1 0($v0)
	li $a1 17
	la $a2 StringConst_0
	jal _array_store_error
label10:
	# calculate the desired offset to the array's location
	mul $t2 $t2 4
	add $t2 $t2 16
	add $v1 $t2 $v1
	sw $v0 0($v1)
	# save stack pointer result to $a0
	lw $a0 0($sp)
	# add stack pointer with 4
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
B.getA:
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
	# subtract 4 from $sp and store the result to $fp
	sub $fp $sp 4
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	# load the address of Integer_template to $a0
	la $a0 Integer_template
	# jump to Object.clone
	jal Object.clone
	# move $v0 to $a0
	move $a0 $v0
	# jump to Integer_init
	jal Integer_init
	# load (4)$fp to $a0
	lw $a0 4($fp)
	# store $v0 to (0)$fp
	sw $v0 0($fp)
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
	# move $v0 to $a0
	move $a0 $v0
	# load (8)$v0 to $v0
	lw $v0 8($v0)
	# load method address
	# load (16)$v0 to $a1
	lw $a1 16($v0)
	sub $sp $sp 4
	sw $a1 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	# constant int expression: load 4 to $v0
	li $v0 4
	# save parameters on stack
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $v0 to $sp
	sw $v0 0($sp)
	lw $a0 4($sp)
	lw $a1 8($sp)
	# jump to $a1
	jalr $a1
	add $sp $sp 12
	# load (0)$sp to $a0
	lw $a0 4($fp)
	# Array Assign Expr
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $a0 to stack pointer with offset of 0
	sw $a0 0($sp)
	# case where the reference name is /this/
	# visit the index expression, this will load the expression to v0
	# constant int expression: load 0 to $v0
	li $v0 0
	# move current location's base register with offset to v0
	lw $v1 12($a0)
	bne $v1 $zero label11
	li $a1 127
	la $a2 StringConst_0
	jal _null_pointer_error
label11:
	lw $t1 12($v1)
	blt $v0 $zero label13
	bge $v0 $t1 label13
	b label12
label13:
	move $t0 $v0
	li $a1 127
	la $a2 StringConst_0
	jal _array_index_error
label12:
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $v1 to stack pointer with offset of 0
	sw $v1 0($sp)
	# subtract 4 from the the stack pointer
	sub $sp $sp 4
	# save $v1 to stack pointer with offset of 0
	sw $v0 0($sp)
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
	# save stack pointer result to $v1
	lw $t2 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	# save stack pointer result to $v1
	lw $v1 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	lw $t1 0($v1)
	# load j into $t0 and j+k into $t1
	lw $t0 0($v1)
	li $t1 0
	add $t1 $t1 $t0
	# load type of expression in $v0 into $t3
	lw $t3 0($v0)
	# compare j<=i and i<=j+k, if both true, return true
	sle $t0 $t0 $t3
	sle $t3 $t3 $t1
	# $t0 AND $t3, store in $t3
	and $t3 $t0 $t3
	bne $t3 $zero label15
	lw $t0 0($v1)
	lw $t1 0($v0)
	li $a1 127
	la $a2 StringConst_0
	jal _array_store_error
label15:
	# calculate the desired offset to the array's location
	mul $t2 $t2 4
	add $t2 $t2 16
	add $v1 $t2 $v1
	sw $v0 0($v1)
	# save stack pointer result to $a0
	lw $a0 0($sp)
	# add stack pointer with 4
	add $sp $sp 4
	# array expression
	# constant int expression: load 0 to $v0
	li $v0 0
	# subtract stack pointer with 4
	sub $sp $sp 4
	# save value in $a0 to stack pointer with 0 offset
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is /.super/
	# load (12)$a0 to $v0 
	lw $v0 12($a0)
	lw $v0 0($sp)
	add $sp $sp 4
	bne $v1 $zero label16
	li $a1 129
	la $a2 StringConst_0
	jal _null_pointer_error
label16:
	lw $t1 12($v1)
	blt $v0 $zero label18
	bge $v0 $t1 label18
	b label17
label18:
	move $t0 $v0
	li $a1 129
	la $a2 StringConst_0
	jal _array_index_error
label17:
	mul $v0 $v0 4
	add $v0 $v0 16
	add $v0 $v0 $v1
	lw $v0 0($v0)
	# Start Epilogue
	# add 4 to $fp and store the result to $sp
	add $sp $fp 4
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