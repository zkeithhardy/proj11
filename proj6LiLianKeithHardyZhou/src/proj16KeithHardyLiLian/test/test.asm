#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-04-16
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
StringConst_1:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	7
	.ascii	"dskrien"
	.byte	0
	.align	2


class_name_table:
	.word	Class_0
	.word	Class_2
	.word	Class_3
	.word	Class_4
	.word	Class_5
	.word	Class_1

	# Object Templates:
	.globl	Object_template
	.globl	String_template
	.globl	Sys_template
	.globl	SubMain_template
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

SubMain_template:
	.word	4
	.word	28
	.word	SubMain_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0

TextIO_template:
	.word	5
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
	.globl	SubMain_dispatch_table
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
SubMain_dispatch_table:
	.word	Object.clone
	.word	SubMain.equals
	.word	Main.toString
	.word	SubMain.foo
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
	.word	Main.equals
	.word	Main.toString
	.word	Main.foo
	.word	Main.main

	.text
	.globl	main
	.globl	Main_init
	.globl	Main.main
main:
	jal __start
Object_init:
String_init:
	li $v0 0
	sw $v0 0($a0)
Sys_init:
SubMain_init:
	jal Object_init
	jal Main_init
	la $v0 StringConst_1
	sw $v0 12($a0)
TextIO_init:
	li $v0 0
	sw $v0 0($a0)
	li $v0 1
	sw $v0 4($a0)
Main_init:
	jal Object_init
	li $v0 3
	sw $v0 12($a0)
Main.foo:
	# Start Prologue
	sub $sp $sp 4
	sw $ra 0($sp)
	sub $sp $sp 4
	sw $fp 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	sub $fp $sp 4
	move $sp $fp
	# End Prologue
	sub $sp $sp 4
	sw $a0 0($sp)
	lw $v0 20($fp)
	lw $a0 0($sp)
	add $sp $sp 4
	sw $v0 4($fp)
	sub $sp $sp 4
	sw $a0 0($sp)
	lw $v0 4($fp)
	lw $a0 0($sp)
	add $sp $sp 4
	# Start Epilogue
	add $sp $fp 4
	lw $a0 0($sp)
	add $sp $sp 4
	lw $fp 0($sp)
	add $sp $sp 4
	lw $ra 0($sp)
	add $sp $sp 4
	move $sp $fp
	jr $ra
	# End Epilogue
Main.equals:
	# Start Prologue
	sub $sp $sp 4
	sw $ra 0($sp)
	sub $sp $sp 4
	sw $fp 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	sub $fp $sp 0
	move $sp $fp
	# End Prologue
	li $v0 -1
	# Start Epilogue
	add $sp $fp 0
	lw $a0 0($sp)
	add $sp $sp 4
	lw $fp 0($sp)
	add $sp $sp 4
	lw $ra 0($sp)
	add $sp $sp 4
	move $sp $fp
	jr $ra
	# End Epilogue
Main.toString:
	# Start Prologue
	sub $sp $sp 4
	sw $ra 0($sp)
	sub $sp $sp 4
	sw $fp 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	sub $fp $sp 0
	move $sp $fp
	# End Prologue
	la $v0 Class_4
	# Start Epilogue
	add $sp $fp 0
	lw $a0 0($sp)
	add $sp $sp 4
	lw $fp 0($sp)
	add $sp $sp 4
	lw $ra 0($sp)
	add $sp $sp 4
	move $sp $fp
	jr $ra
	# End Epilogue
Main.main:
	# Start Prologue
	sub $sp $sp 4
	sw $ra 0($sp)
	sub $sp $sp 4
	sw $fp 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	sub $fp $sp 8
	move $sp $fp
	# End Prologue
	# save $a0 onto stack in case init creates a new object.
	sub $sp $sp 4
	sw $a0 0($sp)
	la $a0 SubMain_template
	la $v0 SubMain_dispatch_table
	lw $v0 0($v0)
	jalr $v0
	jal SubMain_init
	# restore $a0
	lw $a0 0($sp)
	add $sp $sp 4
	sw $v0 0($fp)
	sub $sp $sp 4
	sw $a0 0($sp)
	lw $v0 0($fp)
	lw $a0 0($sp)
	add $sp $sp 4
	# move v0 to a0
	move $a0 $v0
	# access SubMain_dispatch_table
	la $v0 SubMain_dispatch_table
	# load method address
	lw $a1 12($v0)
	li $v0 1
	# save parameters on stack
	sub $sp $sp 4
	sw $v0 0($sp)
	jalr $a1
	sw $v0 4($fp)
	# Start Epilogue
	add $sp $fp 8
	lw $a0 0($sp)
	add $sp $sp 4
	lw $fp 0($sp)
	add $sp $sp 4
	lw $ra 0($sp)
	add $sp $sp 4
	move $sp $fp
	jr $ra
	# End Epilogue
SubMain.foo:
	# Start Prologue
	sub $sp $sp 4
	sw $ra 0($sp)
	sub $sp $sp 4
	sw $fp 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	sub $fp $sp 0
	move $sp $fp
	# End Prologue
	li $v0 1
	# Start Epilogue
	add $sp $fp 0
	lw $a0 0($sp)
	add $sp $sp 4
	lw $fp 0($sp)
	add $sp $sp 4
	lw $ra 0($sp)
	add $sp $sp 4
	move $sp $fp
	jr $ra
	# End Epilogue
SubMain.equals:
	# Start Prologue
	sub $sp $sp 4
	sw $ra 0($sp)
	sub $sp $sp 4
	sw $fp 0($sp)
	sub $sp $sp 4
	sw $a0 0($sp)
	sub $fp $sp 0
	move $sp $fp
	# End Prologue
	li $v0 0
	# Start Epilogue
	add $sp $fp 0
	lw $a0 0($sp)
	add $sp $sp 4
	lw $fp 0($sp)
	add $sp $sp 4
	lw $ra 0($sp)
	add $sp $sp 4
	move $sp $fp
	jr $ra
	# End Epilogue
