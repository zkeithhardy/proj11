# Author:  Dale Skrien
# Date: Feb 2019
# Compiled from source:
# 	Minimum.btm
# The file Minimum.btm contains the following:
#   class Main {
#       void main() { }
#   }

	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	0

class_name_3:
	.word	1
	.word	24
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2
class_name_1:
	.word	1
	.word	24
	.word	String_dispatch_table
	.word	6
	.ascii	"String"
	.byte	0
	.align	2
class_name_4:
	.word	1
	.word	24
	.word	String_dispatch_table
	.word	6
	.ascii	"TextIO"
	.byte	0
	.align	2
label0:
	.word	1
	.word	36
	.word	String_dispatch_table
	.word	16
	.ascii	"sample/Empty.btm"
	.byte	0
	.align	2
class_name_0:
	.word	1
	.word	24
	.word	String_dispatch_table
	.word	6
	.ascii	"Object"
	.byte	0
	.align	2
class_name_2:
	.word	1
	.word	20
	.word	String_dispatch_table
	.word	3
	.ascii	"Sys"
	.byte	0
	.align	2

class_name_table:
	.word	class_name_0
	.word	class_name_1
	.word	class_name_2
	.word	class_name_3
	.word	class_name_4

	.globl	Main_template
	.globl	String_template
	.globl	TextIO_template
	.globl	Object_template
	.globl	Sys_template
String_template:
	.word	1
	.word	16
	.word	String_dispatch_table
	.word	0
Object_template:
	.word	0
	.word	12
	.word	Object_dispatch_table
Sys_template:
	.word	2
	.word	12
	.word	Sys_dispatch_table
Main_template:
	.word	3
	.word	12
	.word	Main_dispatch_table
TextIO_template:
	.word	4
	.word	20
	.word	TextIO_dispatch_table
	.word	0
	.word	0

	.globl	Main_dispatch_table
	.globl	String_dispatch_table
	.globl	TextIO_dispatch_table
	.globl	Object_dispatch_table
	.globl	Sys_dispatch_table
String_dispatch_table:
	.word	Object.clone
	.word	String.equals
	.word	String.toString
	.word	String.length
	.word	String.substring
	.word	String.concat
Object_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
Sys_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Sys.exit
	.word	Sys.time
	.word	Sys.random
Main_dispatch_table:
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

	.text
	.globl	main
	.globl	Main_init
	.globl	Main.main
main:
	jal __start

Object_init:
String_init:
Sys_init:
Main_init:
TextIO_init:
Main.main:
	jr $ra
