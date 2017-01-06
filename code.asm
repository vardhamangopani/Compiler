#Prolog:
.text
.globl main
main:
move $fp $sp #frame ptr will be start of active stack
la $a0 ProgBegin
li $v0 4
syscall
#End of Prolog

li $t0 10
sw $t0 -8($fp)
lw $t0 -8($fp)
sw $t0 1($fp)
li $t0 4
sw $t0 -12($fp)
lw $t0 -12($fp)
sw $t0 0($fp)

#PostLog:
la $a0 ProgEnd
li $v0 4
syscall
li $v0 10
syscall
.data
ProgBegin: .asciiz "Program Begin \n"
ProgEnd: .asciiz "\nProgram End  "

