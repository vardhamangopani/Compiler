#Prolog:
.text
.globl main
main:
move $fp $sp #frame ptr will be start of active stack
la $a0 ProgBegin
li $v0 4
syscall
#End of Prolog

li $t0 1
sw $t0 -8($fp)
lw $t0 -8($fp)
sw $t0 -4($fp)
la $a0 lable0
li $v0 4
syscall
li $v0 5
syscall
sw $v0 -4($fp)
li $t0 10
sw $t0 -12($fp)
lw $t0 -4($fp)
lw $t1 -12($fp)
beq $t0 $t1 Lbl1
li $t2 0
j Lbl2
Lbl1:
li $t2 1
Lbl2:
sw $t2 -16($fp)
lw $t0 -16($fp)
beq $t0 $0 LableIF1
loop1:
li $t0 5
sw $t0 -20($fp)
lw $t0 -4($fp)
lw $t1 -20($fp)
slt $t2 $t1 $t0
sw $t2 -24($fp)
lw $t0 -24($fp)
beq $t0 $0 LableWHILE2
la $a0 lable1
li $v0 4
syscall
li $v0 5
syscall
sw $v0 -4($fp)
j loop1
LableWHILE2:
LableIF1:
la $a0 lable2
li $v0 4
syscall
lw $a0 -4($fp)
li $v0 1
syscall
li $t0 5
sw $t0 -28($fp)
lw $t0 -4($fp)
lw $t1 -28($fp)
add $t0 $t0 $t1
sw $t0 -32($fp)
li $t0 4
sw $t0 -36($fp)
li $t0 2
sw $t0 -40($fp)
lw $t0 -36($fp)
lw $t1 -40($fp)
mul $t0 $t0 $t1
sw $t0 -44($fp)
li $t0 2
sw $t0 -48($fp)
lw $t0 -44($fp)
lw $t1 -48($fp)
div $t0 $t0 $t1
sw $t0 -52($fp)
lw $t0 -32($fp)
lw $t1 -52($fp)
sub $t0 $t0 $t1
sw $t0 -56($fp)
lw $t0 -56($fp)
sw $t0 -4($fp)
la $a0 lable3
li $v0 4
syscall
lw $a0 -4($fp)
li $v0 1
syscall

#PostLog:
la $a0 ProgEnd
li $v0 4
syscall
li $v0 10
syscall
.data
ProgBegin: .asciiz "Program Begin \n"
ProgEnd: .asciiz "\nProgram End  "
lable0: .asciiz "\nEnter a: "
lable1: .asciiz "\nEnter a again: "
lable2: .asciiz "\na is : "
lable3: .asciiz "\nAfter all operations, a is: "

