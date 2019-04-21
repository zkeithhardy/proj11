#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-04-21
#Compiled From Source: test.btm
	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	-1

	# String Constants:
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
	.word	Class_2
	.word	Class_3
	.word	Class_4
	.word	Class_1

	# Object Templates:
	.globl	Object_template
	.globl	String_template
	.globl	Sys_template
	.globl	TextIO_template
	.globl	Main_template

Object_template:
	.word	0
	.word	12
	.word	Object_dispatch_table

String_template:
	.word	1
	.word	16
	.word	String_dispatch_table
	.word	0

Sys_template:
	.word	2
	.word	12
	.word	Sys_dispatch_table

TextIO_template:
	.word	4
	.word	20
	.word	TextIO_dispatch_table
	.word	0
	.word	0

Main_template:
	.word	3
	.word	20
	.word	Main_dispatch_table
	.word	0
	.word	0

	# Dispatch Tables:
	.globl	Object_dispatch_table
	.globl	String_dispatch_table
	.globl	Sys_dispatch_table
	.globl	TextIO_dispatch_table
	.globl	Main_dispatch_table
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
Object_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $ra
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $fp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $a0
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
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
	# move $fp to $sp
	move $sp $fp
	jr $ra
	# End Epilogue
String_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $ra
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $fp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $a0
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	li $v0 0
	sw $v0 12($a0)
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
	# move $fp to $sp
	move $sp $fp
	jr $ra
	# End Epilogue
Sys_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $ra
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $fp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $a0
	sw $a0 0($sp)
	# subtract 0 from $sp and store the result to $fp
	sub $fp $sp 0
	# move $fp to $sp
	move $sp $fp
	# End Prologue
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
	# move $fp to $sp
	move $sp $fp
	jr $ra
	# End Epilogue
TextIO_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $ra
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $fp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $a0
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
	# move $fp to $sp
	move $sp $fp
	jr $ra
	# End Epilogue
Main_init:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $ra
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $fp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $a0
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
	# move $fp to $sp
	move $sp $fp
	jr $ra
	# End Epilogue
Main.main:
	# Start Prologue
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $ra
	sw $ra 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $fp
	sw $fp 0($sp)
	# subtract 4 from $sp
	sub $sp $sp 4
	# store $sp to $a0
	sw $a0 0($sp)
	# subtract 4 from $sp and store the result to $fp
	sub $fp $sp 4
	# move $fp to $sp
	move $sp $fp
	# End Prologue
	# gen left side of expression
	# var expression
	sub $sp $sp 4
	sw $a0 0($sp)
	# accept the reference object and save its location $v0
	# case where the reference object is /this./
	lw $v0 12($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# move v0 to v1
	move $v1 $v0
	# gen right side of expression
	# constant int expression
	li $v0 3
	# compare left and right sides of expression
	sle $v0 $v1 $v0
	sub $v0 $zero $v0
	# store (0)$fp to $v0
	sw $v0 0($fp)
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
	# move $fp to $sp
	move $sp $fp
	jr $ra
	# End Epilogue