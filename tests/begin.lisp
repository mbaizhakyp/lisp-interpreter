(begin (set a 1) (set b 2) (add a b)) ; evaluates in order, returns the last value
(begin)
(def swap (p) (begin (set x (car p)) (cons (car (cdr p)) (cons x ()))))
(swap '(1 2))
