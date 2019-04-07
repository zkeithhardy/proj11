#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-04-07
#Compiled From Source: test.btm
	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	-1

StringConst_0:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2

Class_1:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	7
	.ascii	"SubMain"
	.byte	0
	.align	2
Class_0:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2

class_name_table:
	.word	Class_1
	.word	Class_0

	.globl	SubMain_template
	.globl	Main_template

SubMain_template:
	.word	0
	.word	52
	.word	SubMain_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0

Main_template:
	.word	1
	.word	36
	.word	Main_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0

	.globl	SubMain_dispatch_table
	.globl	Main_dispatch_table
SubMain_dispatch_table:
	.word	SubMain.foo
	.word	SubMain.equals
	.word	Main.foo
	.word	Main.equals
	.word	Main.toString
	.word	Main.main
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
Main_dispatch_table:
	.word	Main.foo
	.word	Main.equals
	.word	Main.toString
	.word	Main.main
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
