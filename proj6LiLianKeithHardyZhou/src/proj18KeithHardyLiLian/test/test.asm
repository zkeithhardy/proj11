#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian
#Date: 2019-05-09
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
Class_6:
	.word	1		# String Identifier
	.word	28		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	8
	.ascii	"Object[]"
	.byte	0
	.align	2
StringConst_1:
	.word	1		# String Identifier
	.word	24		# Size of Object in Bytes
	.word	String_dispatch_table
	.word	4
	.ascii	"In A"
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
	.word	Class_16
	.word	Class_4
	.word	Class_5
	.word	Class_6
	.word	Class_15
	.word	Class_17
	.word	Class_8
	.word	Class_9
	.word	Class_13
	.word	Class_7
	.word	Class_12
	.word	Class_2
	.word	Class_3

	# Object Templates:
	.globl	A_template
	.globl	B_template
	.globl	A[]_template
	.globl	B[]_template
	.globl	Boolean[]_template
	.globl	int[]_template
	.globl	String_template
	.globl	String[]_template
	.globl	Sys_template
	.globl	Integer[]_template
	.globl	TextIO_template
	.globl	Integer_template
	.globl	Main[]_template
	.globl	Object_template
	.globl	boolean[]_template
	.globl	Boolean_template
	.globl	Main_template
	.globl	Object[]_template

A_template:
	.word	-1		# Class ID
	.word	24		# Size of Object in Bytes
	.word	A_dispatch_table
	.word	0
	.word	0
	.word	0

B_template:
	.word	-1		# Class ID
	.word	32		# Size of Object in Bytes
	.word	B_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0

A[]_template:
	.word	-1		# Class ID
