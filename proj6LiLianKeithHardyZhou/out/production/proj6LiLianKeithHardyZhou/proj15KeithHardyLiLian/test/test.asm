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
	.word	Class_2
	.word	Class_3
	.word	Class_5
	.word	Class_1
	.word	Class_4

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
	.word	3
	.word	28
	.word	SubMain_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0

TextIO_template:
	.word	4
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
	.word	Object.equals
	.word	Object.toString
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
Sys_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
SubMain_dispatch_table:
	.word	Main.foo
	.word	Main.equals
	.word	Main.toString
	.word	Main.main
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
TextIO_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
Main_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
