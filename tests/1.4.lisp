(and? 'T 'T)
(and? () (car a)) ; short-circuit: (car a) would be an error
(or? () ())
(or? 'T (car a)) ; short-circuit
(eq? a a)
(eq? a b)
(eq? () ())
(eq? '(a) '(a))
(if 'T ''T ())
(if () ''T ())
(if () (car a) b) ; only one branch is evaluated
(cond (() a (eq? a b) b 'T d))
