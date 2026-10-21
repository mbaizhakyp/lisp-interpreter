; Converts Reverse Polish Notation to a lisp expression, from the instructor's program.
; Polish Notation (+ 2 (- 4 3)) is + 2 - 4 3; Reverse Polish Notation is 2 4 3 - +
; Given an sexp representing an expression in RPN, eg (2 x 3 - +), generate the lisp expression.
; Fixes: (op? (car rpn)) was missing its closing paren; both conds now wrap their
; test/result pairs in one list, as the 1.4 spec's cond requires; not is not? here.
(def op? (sym) (cond (
	(eq? '+ sym) 't
	(eq? '- sym) 't
	(eq? '* sym) 't
	(eq? '/ sym) 't
	't ()
)))
(def value? (sym) (not? (op? sym)))

(def rpn2lisp (rpn_input)
	(r2l rpn_input ())
)
(def r2l (rpn stack) (cond (
	(nil?  rpn) (car stack);stack hold the answer
	(op? (car rpn))
		(r2l
			(cdr rpn) ;parse the rest
			(updatedstack ;pop twice and add the expression
				stack
				(createlispexpr
					(car (cdr stack)) ;left
					(car stack)       ;right
					(car rpn)         ;op
		)	)	)

	't (r2l (cdr rpn) (cons (car rpn) stack))
)))

;remove two items and add 1 item
(def updatedstack(stack item)
	(cons item (cdr (cdr stack)))
)

(def createlispexpr (left right op)
	(list3 op left right)
)

(def list3 (a b c)
	(cons a (cons b (cons c ())))
)

; the generated expression uses + - * /, so define them to evaluate it
(def + (a b) (add a b))
(def - (a b) (sub a b))
(def * (a b) (mul a b))
(def / (a b) (div a b))

(set x 4)
(rpn2lisp '(2 x 3 - +))
(eval (rpn2lisp '(2 x 3 - +)))
(rpn2lisp '(5 1 2 + 4 * + 3 -))
(eval (rpn2lisp '(5 1 2 + 4 * + 3 -)))
(value? 'x)
(value? '+)
