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
